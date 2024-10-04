package org.stus.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cascade;

import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "item")
public class Item {
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
    private List<ItemOrder> itemOrders;


    public Item() {}


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

    public List<ItemOrder> getItemOrders() {
        return itemOrders;
    }

    public void setItemOrders(List<ItemOrder> itemOrders) {
        this.itemOrders = itemOrders;
    }


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


    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", numberOfItems=" + numberOfItems +
                ", price=" + price +
                ", itemInfo='" + itemInfo + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
