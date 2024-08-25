package com.mate.exception;

public class PaymentExpiredException extends Exception {
    public PaymentExpiredException(String msg) {
        super(msg);
    }
}
