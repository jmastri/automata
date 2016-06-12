package com.teksystems.qe.automata.sample.app;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;


import com.teksystems.qe.automata.Application;
import com.teksystems.qe.automata.ApplicationListener;
import com.teksystems.qe.automata.BaseView;
import com.teksystems.qe.automata.ViewEvent;
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
		
		class ChangeState implements ApplicationListener{
			public void handleEvent(ViewEvent event, Application application, BaseView view, String state) {
				if(event == ViewEvent.VIEW_DONE){
					((GoogleApplication)application).setCurrentState(flow.get(state));	
				}
			}
		};
		this.registerListener(new ChangeState());
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
