package org.stus.marketplace.utils.item_utils;

public class ItemErrorResponse {
    private String errorMessage;
    private long createAt;

    public ItemErrorResponse(String errorMessage, long createAt) {
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
