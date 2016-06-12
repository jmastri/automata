package com.teksystems.qe.automata;

public interface ApplicationListener {

	public void handleEvent(ViewEvent event, Application application, BaseView view, String state);
}
