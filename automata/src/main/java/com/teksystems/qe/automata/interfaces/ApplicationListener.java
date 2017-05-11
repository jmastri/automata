package com.teksystems.qe.automata.interfaces;

import com.teksystems.qe.automata.Application;
import com.teksystems.qe.automata.ViewEvent;

public interface ApplicationListener {

    public void handleEvent(ViewEvent event, Application application, BaseView view, String state);
}
