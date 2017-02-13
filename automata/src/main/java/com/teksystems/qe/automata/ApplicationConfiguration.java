package com.teksystems.qe.automata;

import com.teksystems.qe.automata.annotations.ViewStates;
import com.teksystems.qe.automata.interfaces.BaseView;

public class ApplicationConfiguration {

    private long viewTimeout        = 60 * 1000;
    private long transitionTimeout  = 60 * 1000;
    private boolean callNavigate    = true;
    private String basePackage      = null;


    public void setViewTimeout(long viewTimeout) {
        this.viewTimeout = viewTimeout;
    }

    public boolean isCallNavigate() {
        return callNavigate;
    }

    public void setCallNavigate(boolean callNavigate) {
        this.callNavigate = callNavigate;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public long getTransitionTimeout() {
        return transitionTimeout;
    }

    public void setTransitionTimeout(long transitionTimeout) {
        this.transitionTimeout = transitionTimeout;
    }

    public long getViewTimeout(BaseView currentView) {
        ViewStates annotation;
        if((annotation = currentView.getClass().getAnnotation(ViewStates.class))!=null){
            if(annotation.timeOut()>0){
                return annotation.timeOut();
            }
        }
        return viewTimeout;
    }

}
