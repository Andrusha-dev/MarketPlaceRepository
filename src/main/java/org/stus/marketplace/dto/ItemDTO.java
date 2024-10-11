package org.stus.marketplace.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public class ItemDTO {
    private int id;

    @NotEmpty(message = "Item name should not be empty")
    private String itemName;

    @Min(value = 0, message = "Number of items should be 0 or higher")
    private int numberOfItems;

    @Min(value = 0, message = "price should be higher then 0")
    private int price;

    @NotEmpty(message = "Item information should not be empty")
    private String itemInfo;

    @NotEmpty(message = "Category should not be empty")
    private String category;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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


    @Override
    public String toString() {
        return "ItemDTO{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", numberOfItems=" + numberOfItems +
                ", price=" + price +
                ", itemInfo='" + itemInfo + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
