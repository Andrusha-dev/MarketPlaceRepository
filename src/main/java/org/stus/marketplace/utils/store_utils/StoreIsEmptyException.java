package org.stus.marketplace.utils.store_utils;

public class StoreIsEmptyException extends RuntimeException {
    public StoreIsEmptyException(String msg) {
        super(msg);
    }
}
