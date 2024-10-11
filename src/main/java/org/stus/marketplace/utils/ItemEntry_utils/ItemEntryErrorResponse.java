package org.stus.marketplace.utils.ItemEntry_utils;

public class ItemEntryErrorResponse {
    private String erroeMessage;
    private long createAt;


    public ItemEntryErrorResponse(String erroeMessage, long createAt) {
        this.erroeMessage = erroeMessage;
        this.createAt = createAt;
    }


    public String getErroeMessage() {
        return erroeMessage;
    }

    public void setErroeMessage(String erroeMessage) {
        this.erroeMessage = erroeMessage;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }
}
