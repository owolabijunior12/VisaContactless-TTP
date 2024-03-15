package com.lovisgod.testVisaTTP.handlers.network.simplecalladapter;

public interface SimpleHandler<T> {
    void accept(T response, Throwable throwable);
}