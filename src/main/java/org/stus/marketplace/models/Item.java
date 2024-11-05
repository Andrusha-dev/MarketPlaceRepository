package org.stus.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cascade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "item")
public class Item {
    private static final Logger logger = LogManager.getLogger(Item.class.getName());
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

    @OneToMany(mappedBy = "orderedItem")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private List<ItemEntry> itemEntries;


    public Item() {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        logger.debug("catch item id: " + id);
        this.id = id;
        logger.info("set id: " + id + " in item");
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        logger.debug("catch item itemName: " + itemName);
        this.itemName = itemName;
        logger.info("set itemName: " + itemName + " in item");
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        logger.debug("catch item numberOfItems: " + numberOfItems);
        this.numberOfItems = numberOfItems;
        logger.info("set numberOfItems: " + numberOfItems + " in item");
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        logger.debug("catch item price: " + price);
        this.price = price;
        logger.info("set price: " + price + " in item");
    }

    public String getItemInfo() {
        return itemInfo;
    }

    public void setItemInfo(String itemInfo) {
        logger.debug("catch item itemInfo: " + itemInfo);
        this.itemInfo = itemInfo;
        logger.info("set itemInfo: " + itemInfo + " in item");
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        logger.debug("catch item category: " + category);
        this.category = category;
        logger.info("set category: " + category + " in item");
    }

    public List<ItemEntry> getItemEntries() {
        return itemEntries;
    }

    public void setItemEntries(List<ItemEntry> itemEntries) {
        logger.debug("catch item itemEntries with size: " + itemEntries.size());
        this.itemEntries = itemEntries;
        logger.info("set itemEntries with size: " + itemEntries.size() + " in item");
    }

    /* //based only on "id"
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    */

    //based on all fields
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && numberOfItems == item.numberOfItems && price == item.price && Objects.equals(itemName, item.itemName) && Objects.equals(itemInfo, item.itemInfo) && Objects.equals(category, item.category);
    }

    //based on all fields
    @Override
    public int hashCode() {
        return Objects.hash(id, itemName, numberOfItems, price, itemInfo, category);
    }


    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", numberOfItems=" + numberOfItems +
                ", price=" + price +
                ", itemInfo='" + itemInfo + '\'' +
                ", category='" + category + '\'' +
                ", itemEntries=" + itemEntries +
                '}';
    }
}
