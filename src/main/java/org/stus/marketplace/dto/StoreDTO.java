package org.stus.marketplace.dto;

public class StoreDTO {
    private int itemId;
    private int numberOfItems;


    public StoreDTO() {}

    public StoreDTO(int itemId, int numberOfItems) {
        this.itemId = itemId;
        this.numberOfItems = numberOfItems;
    }


    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }
}
