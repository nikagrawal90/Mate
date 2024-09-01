package com.mate.exception;

public class InvalidBookingStateException extends Exception {
    public InvalidBookingStateException(String msg) {
        super(msg);
    }
}
