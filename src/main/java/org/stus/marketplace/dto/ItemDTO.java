package org.stus.marketplace.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public class ItemDTO {
    @Column(name = "item_name")
    @NotEmpty(message = "Item name should not be empty")
    private String itemName;

    @Column(name = "number_of_items")
    @Min(value = 0, message = "Number of items should be 0 or higher")
    private int numberOfItems;

    @Column(name = "price")
    @Min(value = 0, message = "price should be higher then 0")
    private int price;

    @Column(name = "item_info")
    @NotEmpty(message = "Item information should not be empty")
    private String itemInfo;

    @Column(name = "category")
    @NotEmpty(message = "Category should not be empty")
    private String category;


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getItemInfo() {
        return itemInfo;
    }

    public void setItemInfo(String itemInfo) {
        this.itemInfo = itemInfo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
