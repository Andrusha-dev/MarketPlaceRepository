package org.stus.marketplace.utils.store_utils;

public class ItemIsAbsentInStoreException extends RuntimeException {
    public ItemIsAbsentInStoreException(String msg) {
        super(msg);
    }
}
