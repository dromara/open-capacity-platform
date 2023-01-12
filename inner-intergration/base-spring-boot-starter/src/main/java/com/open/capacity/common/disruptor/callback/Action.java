package com.open.capacity.common.disruptor.callback;

@FunctionalInterface
public interface Action {
    void execute();
}