package com.teksystems.qe.automata.interfaces;

import com.teksystems.qe.automata.Application;

public interface EndState {
    public boolean stop(Application application, BaseView view, String state);
}
