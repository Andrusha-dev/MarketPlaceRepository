package org.stus.marketplace.utils.store_utils;


public class StoreErrorResponse {
    private String errorMessage;
    private long createAt;

    public StoreErrorResponse(String errorMessage, long createAt) {
        this.errorMessage = errorMessage;
        this.createAt = createAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }
}
