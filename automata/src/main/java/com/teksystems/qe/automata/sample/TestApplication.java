package com.teksystems.qe.automata.sample;

import com.teksystems.qe.automata.Application;
import com.teksystems.qe.automata.ApplicationListener;
import com.teksystems.qe.automata.BaseView;
import com.teksystems.qe.automata.ViewEvent;

public class TestApplication extends Application {
	String state		= "home";
	
	public TestApplication(){
		class ChangeState implements ApplicationListener{
			public void handleEvent(ViewEvent event, Application application, BaseView view, String state) {
				LOG.info("Listener: "+event.name());
				if(event == ViewEvent.VIEW_DONE && state.equalsIgnoreCase("home")){
					((TestApplication)application).setState("page2");	
				}
			}
		};
		this.registerListener(new ChangeState());
	}
	
	@Override
	protected String getState() {
		return state;
	}

	@Override
	protected Object[] getViewData() {
		// TODO Auto-generated method stub
		return new Object[0];
	}

	public void setState(String state){
		this.state = state;
	}
}
