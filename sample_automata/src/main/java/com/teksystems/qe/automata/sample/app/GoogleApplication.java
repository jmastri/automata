package com.teksystems.qe.automata.sample.app;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;


import com.teksystems.qe.automata.Application;
import com.teksystems.qe.automata.ViewEvent;
import com.teksystems.qe.automata.interfaces.ApplicationListener;
import com.teksystems.qe.automata.interfaces.BaseView;
import com.teksystems.qe.automata.sample.data.GenericDataObject;


public class GoogleApplication extends Application {

	WebDriver driver;
	GenericDataObject data;
	String currentState 			= "initialize";
	HashMap<String, String> flow	= new HashMap<String,String>();
	
	public GoogleApplication(WebDriver driver, GenericDataObject data) {
		this.driver	= driver;
		this.data = data;
		flow.put("initialize", "search");
		flow.put("search", "results");
		
		//Please don't do this it's no better than procedural, 
		//this is just a sample and this is to show how the listeners work
		// please pull the state from the app or some other logical place
		class ChangeState implements ApplicationListener{
			public void handleEvent(ViewEvent event, Application application, BaseView view, String state) {
				if(event == ViewEvent.VIEW_DONE){
					((GoogleApplication)application).setCurrentState(flow.get(state));	
				}
			}
		};
		this.registerListener(new ChangeState());
		
		class StateLogger implements ApplicationListener{
            public void handleEvent(ViewEvent event, Application application, BaseView view, String state) {
                LOG.info("[STATE LOGGER] "+event.name()+" was triggered on view: "+view.getClass().getSimpleName());
            }
        };
        this.registerListener(new StateLogger());        
        config.setBasePackage("com.teksystems.qe.automata.sample.views");

	}

	@Override
	protected String getState() {
		return currentState;
	}

	@Override
	protected Object[] getViewData() {
		Object[] ret	= {
				this.driver,
				this.data
		};
		return ret;
	}

	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}
	
	

}
