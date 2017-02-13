package com.teksystems.qe.automata.exceptions;

public class ViewProcessingException extends Exception {

    private static final long serialVersionUID = 1L;

    public ViewProcessingException(String reason, Exception cause) {
        super(reason, cause);
    }

}
