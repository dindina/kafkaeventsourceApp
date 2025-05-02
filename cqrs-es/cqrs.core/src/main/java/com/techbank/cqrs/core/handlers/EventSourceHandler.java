package com.techbank.cqrs.core.handlers;

import com.techbank.cqrs.core.domain.AggregateRoot;

public interface EventSourceHandler<T> {
    void save(AggregateRoot root);
    T getId(String id);
}
