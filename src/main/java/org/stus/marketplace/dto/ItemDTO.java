package org.stus.marketplace.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemDTO {
    private static final Logger logger = LogManager.getLogger(ItemDTO.class.getName());

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
        logger.debug("catch itemDTO id: " + id);
        this.id = id;
        logger.info("set id: " + id + " in itemDTO");
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        logger.debug("catch itemDTO itemName: " + itemName);
        this.itemName = itemName;
        logger.info("set itemName: " + itemName + " in itemDTO");
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        logger.debug("catch itemDTO numberOfItems: " + numberOfItems);
        this.numberOfItems = numberOfItems;
        logger.info("set numberOfItems: " + numberOfItems + " in itemDTO");
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        logger.debug("catch itemDTO price: " + price);
        this.price = price;
        logger.info("set price: " + price + " in itemDTO");
    }

    public String getItemInfo() {
        return itemInfo;
    }

    public void setItemInfo(String itemInfo) {
        logger.debug("catch itemDTO itemInfo: " + itemInfo);
        this.itemInfo = itemInfo;
        logger.info("set itemInfo: " + itemInfo + " in itemDTO");
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        logger.debug("catch itemDTO category: " + category);
        this.category = category;
        logger.info("set category: " + category + " in itemDTO");
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
