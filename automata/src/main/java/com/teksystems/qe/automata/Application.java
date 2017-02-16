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
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.teksystems.qe.automata.annotations.ViewStates;
import com.teksystems.qe.automata.exceptions.ViewInitializationException;
import com.teksystems.qe.automata.exceptions.ViewNotDefinedException;
import com.teksystems.qe.automata.exceptions.ViewProcessingException;
import com.teksystems.qe.automata.interfaces.ApplicationListener;
import com.teksystems.qe.automata.interfaces.BaseView;
import com.teksystems.qe.automata.interfaces.EndState;

/**
 * Application level automation framework.
 * 
 * @author James Mastri
 * @author Jeff Clyne
 * @author Matt Logston
 * @author Ben Kresky
 * @author David Eltgroth
 * @author Jason Polk
 * @author Tony Hernandez
 * 
 */

public abstract class Application {
    protected Logger LOG                                        = LogManager.getLogger(this.getClass());
    protected ApplicationConfiguration config                   = null;
    protected Map<String, Class<? extends BaseView>> viewMap    = new HashMap<String, Class<? extends BaseView>>();
    protected boolean breakOut                                  = false;
    protected Stack<BaseView> viewStack                         = new Stack<BaseView>();
    protected BaseView currentView                              = null;
    protected String lastState                                  = null;
    protected Map<String, ApplicationListener> listeners        = new HashMap<String, ApplicationListener>();

    public Application() {
        this(new ApplicationConfiguration());
    }

    public Application(ApplicationConfiguration configuration) {
        config = configuration;
        initialize();
    }

    /**
     * abstract method that must return a current state based on application
     * specific logic.
     * 
     * @return string representation of the current state
     */
    protected abstract String getState();

    /**
     * Abstract method used to gather the parameters for the current view
     * 
     * @return an array of data that is passed into view constructors
     */
    protected abstract Object[] getViewData();

    /**
     * Uses reflection to fill the map of views
     */

    protected void initialize() {
        viewMap.clear();
        Reflections reflections = null;
        if (config.getBasePackage() != null) {
            LOG.info("[AUTOMATA] Loading Views from base package: \""+config.getBasePackage()+"\" ...");
            reflections = new Reflections(config.getBasePackage());
        } else {
            LOG.info("[AUTOMATA] Loading Views ...");
            reflections = new Reflections();
        }
        Set<Class<? extends BaseView>> allPages     = reflections.getSubTypesOf(BaseView.class);
        Iterator<Class<? extends BaseView>> it      = allPages.iterator();
        if(!it.hasNext()){
            LOG.error("[AUTOMATA] Failed to find any classes that implement BaseView");
        }
        while (it.hasNext()) {
            Class<? extends BaseView> checkMe = it.next();
            LOG.debug("[AUTOMATA] Found view " + checkMe.getSimpleName() + " extends BaseView");
            
            if (Modifier.isAbstract(checkMe.getModifiers())) {
                LOG.debug("[AUTOMATA] Defined view '" + checkMe.getName() + "' is abstract not loading");
                continue;
            }
            
            ViewStates annotation = checkMe.getAnnotation(ViewStates.class);
            if (annotation == null) {
                LOG.warn("[AUTOMATA] Defined view '" + checkMe.getName() + "' is missing @ViewStates annotation not loaded");
                continue;
            }

            String[] stateArr = annotation.value();
            for (int i = 0; i < stateArr.length; i++) {
                String state = stateArr[i];
                if (viewMap.containsKey(state)) {
                    LOG.error("[AUTOMATA] Multiple views with the state defined as: \"" + state + "\"");
                    LOG.error("[AUTOMATA] "+viewMap.get(state).getSimpleName() + " in map already");
                    LOG.error("[AUTOMATA] "+checkMe.getSimpleName() + " - SKIPPED");
                    continue;
                }
                viewMap.put(state, checkMe);
            }
        } // end iterator
        LOG.info("[AUTOMATA] Views loaded found " + viewMap.size() + " view(s).");
    }

    /**
     * Uses the application defined getState() to determine if the state has
     * changed
     */
    public void waitForStateChange() {
        long start = System.currentTimeMillis();
        LOG.info("[AUTOMATA] waiting for transition from: " + currentView.getClass().getSimpleName() + " [state=\"" + lastState+ "\"]");
        while (lastState.equalsIgnoreCase(getState())) {
            if (System.currentTimeMillis() - start > config.getTransitionTimeout()) {
                break;
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Uses reflection combined with the abstract methods to create an instance
     * of the current view we are on.
     * 
     * @return an instance of the current View based on the current state.
     * @throws ViewInitializationException
     * @throws ViewNotDefinedException
     */

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected BaseView getCurrentView() throws ViewInitializationException, ViewNotDefinedException {
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
            throw new ViewInitializationException("Failed to get a locator after 1 minute, please verify the method",
                    e);
        } finally {
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

                Object[] args = h.get(1, TimeUnit.MINUTES);
                Class[] cArg = new Class[args.length];
                for (int i = 0; i < args.length; i++)
                    cArg[i] = args.getClass();

                Constructor<?>[] constructors = viewMap.get(lastState).getConstructors();
                constructorLoop: for (int i = 0; i < constructors.length; i++) {
                    Constructor<?> thisConstructor = constructors[i];
                    if (thisConstructor.getParameterTypes().length != args.length) {
                        continue constructorLoop;
                    }
                    Class<?>[] constParams = thisConstructor.getParameterTypes();
                    for (int p = 0; p < constParams.length; p++) {
                        if (!constParams[p].isAssignableFrom(args[p].getClass())) {
                            continue constructorLoop;
                        }
                    }
                    LOG.info("[AUTOMATA] Current View: " + viewMap.get(lastState).getSimpleName() + " [state=\"" + lastState
                            + "\"]");
                    return (BaseView) thisConstructor.newInstance(args);
                }

                throw new ViewInitializationException("Failed to get create an instance of the view \""
                        + viewMap.get(lastState).getSimpleName() + "\" unable to find proper constructor", null);

            } catch (Exception e) {
                LOG.error(e);
                h.cancel(true);
                throw new ViewInitializationException(
                        "Failed to get create an instance of the view for state: \"" + lastState + "\"", e);
            } finally {
                executor.shutdownNow();
            }
        } else {
            throw new ViewNotDefinedException("Could not find view class definition for state: \"" + lastState + "\"",
                    null);
        }
    }

    /**
     * Base function for cycling through each view and processing it. Uses the
     * EndState parameter to determine when to stop processing.
     * 
     * @param Class
     *            implementing EndState to trigger process stopping.
     * @return The instance of the page we stopped on
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BaseView process(EndState end) throws Exception {
        try {
            while (!breakOut) {
                currentView = getCurrentView();
                viewStack.push(currentView);
                callListeners(ViewEvent.DETECTED);

                if (end.stop(this, currentView, lastState)) {
                    LOG.info("[AUTOMATA] End condition reached, stopping process.");
                    break;
                }

                if (breakOut) {
                    LOG.info("[AUTOMATA] Break out set, stopping process");
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
                    handler.get(config.getViewTimeout(currentView), TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    LOG.error(e);
                    handler.cancel(true);
                    throw new ViewProcessingException("View " + currentView.getClass().getSimpleName()
                            + " complete action took longer than " + config.getViewTimeout(currentView) + " ms to complete", e);
                } finally {
                    executor.shutdownNow();
                }

                callListeners(ViewEvent.AFTER_COMPLETE);
                if (end.stop(this, currentView, lastState)) {
                    LOG.info("[AUTOMATA] End condition reached, stopping process.");
                    break;
                }
                if (breakOut) {
                    LOG.info("[AUTOMATA] Break out set, stopping process");
                    break;
                }
                if (config.isCallNavigate()) {
                    callListeners(ViewEvent.BEFORE_NAVIGATE);
                    executor = Executors.newSingleThreadExecutor();

                    final Future<Boolean> navHandler = executor.submit(new Callable() {
                        public Boolean call() throws Exception {
                            currentView.navigate();
                            return true;
                        }
                    });
                    try {
                        navHandler.get(config.getViewTimeout(currentView), TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        LOG.error(e);
                        navHandler.cancel(true);
                        throw new ViewProcessingException("View " + currentView.getClass().getSimpleName()
                                + " navigate action took longer than " + config.getViewTimeout(currentView) + " ms to complete", e);
                    } finally {
                        executor.shutdownNow();
                    }
                    callListeners(ViewEvent.AFTER_NAVIGATE);
                    if (breakOut) {
                        LOG.info("[AUTOMATA] Break out set, stopping process");
                        break;
                    }
                }
                callListeners(ViewEvent.VIEW_DONE);
                waitForStateChange();
                callListeners(ViewEvent.VIEW_CHANGE);
                if (end.stop(this, currentView, lastState)) {
                    LOG.info("[AUTOMATA] End condition reached, stopping process.");
                    break;
                }
                if (breakOut) {
                    LOG.info("[AUTOMATA] Break out set, stopping process");
                    break;
                }
            }
        } catch (Exception e) {
            LOG.error(e);
            throw new ViewProcessingException("Failed to process view.", e);
        }
        callListeners(ViewEvent.PROCESS_DONE);
        return currentView;
    }

    /**
     * Triggers all handlers with proper event data
     * 
     * @param Event
     *            type
     */

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void callListeners(final ViewEvent evt) {
        LOG.debug("[AUTOMATA] Calling event listeners for " + evt.name());
        final Iterator<ApplicationListener> it = listeners.values().iterator();
        final Application me = this;
        while (it.hasNext()) {
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
            } finally {
                executor.shutdownNow();
            }
        }
    }

    /**
     * Add a listener to the pool of listeners
     * 
     * @param Listener
     *            that implements ApplicationListener
     */

    public void registerListener(ApplicationListener listener) {
        listeners.put(listener.getClass().getSimpleName(), listener);
    }

    /**
     * Remove a listener to the pool of listeners
     * 
     * @param Listener
     *            that implements ApplicationListener
     */

    public void unregisterListener(ApplicationListener listener) {
        Iterator<String> it = listeners.keySet().iterator();
        while (it.hasNext()) {
            if (it.next().equalsIgnoreCase(listener.getClass().getSimpleName()))
                it.remove();
        }
    }

    /**
     * Utility function for generating a common EndState
     * 
     * @param Listener
     *            that implements ApplicationListener
     */

    @SuppressWarnings("unchecked")
    public <T extends BaseView> T runUntilView(final Class<T> classOfPage) throws Exception {
        if (!viewMap.values().contains(classOfPage)) {
            throw new ViewNotDefinedException(
                    classOfPage.getSimpleName() + " is not mapped, verify you set this view up properly.",
                    new RuntimeException());
        }
        LOG.info("[AUTOMATA] Run until view: " + classOfPage.getSimpleName());
        class ViewEndCondition implements EndState {
            public boolean stop(Application application, BaseView view, String state) {
                if (view.getClass() == classOfPage)
                    return true;
                return false;
            }
        }
        ;
        return (T) process(new ViewEndCondition());
    }

    public ApplicationConfiguration getConfig() {
        return config;
    }

    public void setConfig(ApplicationConfiguration config) {
        this.config = config;
    }

    public boolean isBreakOut() {
        return breakOut;
    }

    public void setBreakOut(boolean breakOut) {
        this.breakOut = breakOut;
    }

    public Map<String, Class<? extends BaseView>> getViewMap() {
        return viewMap;
    }

    public Stack<BaseView> getViewStack() {
        return viewStack;
    }

    public String getLastState() {
        return lastState;
    } 
}
