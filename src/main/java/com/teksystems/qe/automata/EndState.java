package com.teksystems.qe.automata;

public interface EndState {
	public boolean stop(Application application, BaseView view, String state);
}
