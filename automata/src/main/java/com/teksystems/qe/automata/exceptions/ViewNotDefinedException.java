package com.teksystems.qe.automata.exceptions;

public class ViewNotDefinedException extends Exception {

    private static final long serialVersionUID = 1L;

    public ViewNotDefinedException(String reason, Exception cause) {
        super(reason, cause);
    }

}
