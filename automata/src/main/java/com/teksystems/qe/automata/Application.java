package com.teksystems.qe.automata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;





public abstract class Application {
	protected ApplicationConfiguration config							= null;
	protected Logger LOG 												= LogManager.getLogger(this.getClass());
	protected Map<String, Class<? extends BaseView>> viewMap 			= new HashMap<String, Class<? extends BaseView>>();
	protected boolean breakOut											= false;
	protected Stack<BaseView> viewStack									= new Stack<BaseView>();
	protected BaseView currentView 										= null;
	protected String lastState											= null;
	protected Map<String,ApplicationListener> listeners 				= new HashMap<String,ApplicationListener>();
	
	public Application(){
		this(new ApplicationConfiguration());
	}
	
	public Application(ApplicationConfiguration configuration){
		config = configuration;
		initialize();
	}

	protected abstract String getState();
	protected abstract Object[] getViewData();
	
	private void initialize() {
		viewMap.clear();
		LOG.info("Loading Views.");
		Reflections reflections 				= null;
		if(config.getBasePackage() != null){
			reflections							= new Reflections(config.getBasePackage());
		}else{
			reflections							= new Reflections();
		}
		Set<Class<? extends BaseView>> allPages = reflections.getSubTypesOf(BaseView.class);
		Iterator<Class<? extends BaseView>> it 	= allPages.iterator();
		while (it.hasNext()) {
			Class<? extends BaseView> checkMe 	= it.next();
			LOG.debug("Found view "+checkMe.getSimpleName()+" extends BaseView");
			ViewStates annotation 				= checkMe.getAnnotation(ViewStates.class);

			if (Modifier.isAbstract(checkMe.getModifiers())) {
				LOG.debug("Defined view '"+checkMe.getName()+"' is abstract not loading");
				continue;
			}
			
			if(annotation == null){
				LOG.warn("Defined view '"+checkMe.getName()+"' is missing @ViewStates annotation not loaded");
				continue;
			}
			
			Object[] args 					= getViewData();
			Constructor<?>[] constructors 	= checkMe.getConstructors();
			boolean match				 	= false;
			constructorLoop:
			for (int i=0; i<constructors.length;i++) {
				Constructor<?> thisConstructor = constructors[i];
				if (thisConstructor.getParameterTypes().length != args.length) {
					continue constructorLoop;
				}
				Class<?>[] constParams = thisConstructor.getParameterTypes();
				for (int p=0; p<constParams.length;p++) {
					if (!constParams[p].isAssignableFrom(args[p].getClass())) {
						continue constructorLoop;
					}
				}
				match = true;
				break;
			}
			
			if(!match){
				LOG.warn("Defined view '"+checkMe.getName()+"' does not have a proper constructor matching view data.");
				continue;
			}
			
			String[] stateArr		= annotation.value();
			for (int i=0; i<stateArr.length;i++) {
				String state = stateArr[i];
				if (viewMap.containsKey(state)) {
					LOG.error("Multiple views with the state defined as: \""+state+"\"");
					LOG.error(viewMap.get(state).getSimpleName()+" in map already");
					LOG.error(checkMe.getSimpleName()+" - SKIPPED");
					continue;
				}
				viewMap.put(state, checkMe);
			}
		}//end iterator
		LOG.info("Views loaded found "+viewMap.size()+" views.");
	}
	
	
	public void waitForViewChange() {
		long start = System.currentTimeMillis();
		LOG.info("waiting for transition from: "+currentView.getClass().getSimpleName()+" [state=\""+lastState+"\"]");
		while (lastState.equalsIgnoreCase(getState())) {
			if (System.currentTimeMillis()-start > config.getTransitionTimeout()) { 
				break;
			}
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected BaseView getCurrentView() throws ViewInitializationException, ViewNotDefinedException{
		ExecutorService executor = Executors.newSingleThreadExecutor();

		final Future<String> handler = executor.submit(new Callable() {
			public String call() throws Exception {
				return getState();
			}  
		});
		try {
			lastState = handler.get(1, TimeUnit.MINUTES);
		} catch (Exception e) {
			LOG.error(e);
			handler.cancel(true);
			throw new ViewInitializationException("Failed to get a locator after 1 minute, please verify the method", e);
		}finally{
			executor.shutdownNow();
		}
		if (viewMap.containsKey(lastState)) {
			executor = Executors.newSingleThreadExecutor();
			final Future<Object[]> h = executor.submit(new Callable() {
				public Object[] call() throws Exception {
					return getViewData();
				}  
			});
			try {
				LOG.info("Current View: "+viewMap.get(lastState).getClass().getSimpleName()+" [state=\""+lastState+"\"]");
				Object[] args = h.get(1, TimeUnit.MINUTES);
				Class[] cArg = new Class[args.length];
				for(int i=0; i<args.length;i++)
					cArg[i] = args.getClass();
				return viewMap.get(lastState).getDeclaredConstructor(cArg).newInstance(args);

			} catch (Exception e) {
				LOG.error(e);
				h.cancel(true);
				throw new ViewInitializationException("Failed to get create an isntance of the view for state: \"" + lastState+"\"", e);
			}finally{
				executor.shutdownNow();
			}
		}else{
			throw new ViewNotDefinedException("Could not find view class definition for state: \"" + lastState+"\"", null);
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BaseView process(EndState end) throws Exception{
		try{
			while (!breakOut) {
				currentView 		= getCurrentView();
				viewStack.push(currentView);
				callListeners(ViewEvent.DETECTED);
				
				if(end.stop(this, currentView, lastState)){
					LOG.info("End condition reached, stopping process.");
					break;
				}
				
				if(breakOut){
					LOG.info("Break out set, stopping process");
					break;
				}
				ExecutorService executor = Executors.newSingleThreadExecutor();
				callListeners(ViewEvent.BEFORE_COMPLETE);
				final Future<Boolean> handler = executor.submit(new Callable() {
					public Boolean call() throws Exception {
						currentView.complete();
						return true;
					}  
				});
				try {
					handler.get(config.getViewTimeout(), TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					LOG.error(e);
					handler.cancel(true);
					throw new ViewProcessingException("View "+currentView.getClass().getSimpleName()+" complete action took longer than 2 minutes to complete", e);
				}finally{
					executor.shutdownNow();
				}
				
				callListeners(ViewEvent.AFTER_COMPLETE);
				if(end.stop(this, currentView, lastState)){
					LOG.info("End condition reached, stopping process.");
					break;
				}
				if(breakOut){
					LOG.info("Break out set, stopping process");
					break;
				}
				if(config.isCallNavigate()){
					callListeners(ViewEvent.BEFORE_NAVIGATE);
					executor = Executors.newSingleThreadExecutor();

					final Future<Boolean> navHandler = executor.submit(new Callable() {
						public Boolean call() throws Exception {
							currentView.navigate();
							return true;
						}  
					});
					try {
						navHandler.get(config.getViewTimeout(), TimeUnit.MILLISECONDS);
					} catch (Exception e) {
						LOG.error(e);
						navHandler.cancel(true);
						throw new ViewProcessingException("View "+currentView.getClass().getSimpleName()+" navigate action took longer than 2 minutes to complete", e);
					}finally{
						executor.shutdownNow();
					}
					callListeners(ViewEvent.AFTER_NAVIGATE);
					if(breakOut){
						LOG.info("Break out set, stopping process");
						break;
					}
				}
				callListeners(ViewEvent.VIEW_DONE);
				waitForViewChange();
				callListeners(ViewEvent.VIEW_CHANGE);
				if(end.stop(this, currentView, lastState)){
					LOG.info("End condition reached, stopping process.");
					break;
				}
				if(breakOut){
					LOG.info("Break out set, stopping process");
					break;
				}
			}
		}catch(Exception e){
			LOG.error(e);
			throw new ViewProcessingException("Failed to process view.",e);
		}
		callListeners(ViewEvent.PROCESS_DONE);
		return currentView;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void callListeners(final ViewEvent evt) {
		LOG.debug("Calling event listeners for "+evt.name());
		final Iterator<ApplicationListener> it 	= listeners.values().iterator();
		final Application me 					= this;
		while(it.hasNext()){
			ExecutorService executor = Executors.newSingleThreadExecutor();
			final Future<Void> handler = executor.submit(new Callable() {
				public Void call() throws Exception {
					it.next().handleEvent(evt, me, currentView, lastState);
					return null;
				}  
			});
			try {
				handler.get(1, TimeUnit.MINUTES);
			} catch (Exception e) {
				LOG.error(e);
				handler.cancel(true);
			}finally{
				executor.shutdownNow();
			}
		}
	}
	
	public void registerListener(ApplicationListener listener){
		listeners.put(listener.getClass().getSimpleName(), listener);
	}
	
	public void unregisterListener(ApplicationListener listener){
		Iterator<String> it = listeners.keySet().iterator();
		while(it.hasNext()){
			if(it.next().equalsIgnoreCase(listener.getClass().getSimpleName()))
				it.remove();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BaseView> T runUntilView(final Class<T> classOfPage) throws Exception {
		LOG.info("Run until view: "+classOfPage.getSimpleName());
		class PageEndCondition implements EndState {
			public boolean stop(Application application, BaseView view, String state) {
				if(view.getClass() == classOfPage)
					return true;
				return false;
			}
		};
		return (T) process(new PageEndCondition());
	}
	

}
