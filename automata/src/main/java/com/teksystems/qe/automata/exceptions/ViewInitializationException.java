package com.teksystems.qe.automata.exceptions;

public class ViewInitializationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ViewInitializationException(String reason, Exception cause) {
        super(reason, cause);
    }

}
