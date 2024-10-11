package org.stus.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "item_entry")
public class ItemEntry {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "number_of_items")
    @Min(value = 1, message = "Number of items should be higher then 0")
    private int numberOfItems;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @NotNull(message = "Some item should be chosen")
    private Item orderedItem;

    @ManyToOne
    @JoinColumn(name = "item_order_id", referencedColumnName = "id")
    //@NotNull(message = "Some item order should be include this item entry")
    private ItemOrder itemOrder;


    public ItemEntry() {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public Item getOrderedItem() {
        return orderedItem;
    }

    public void setOrderedItem(Item orderedItem) {
        this.orderedItem = orderedItem;
    }

    public ItemOrder getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(ItemOrder itemOrder) {
        this.itemOrder = itemOrder;
    }

    @Override
    public String toString() {
        return "ItemEntry{" +
                "id=" + id +
                ", numberOfItems=" + numberOfItems +
                ", orderedItem=" + orderedItem +
                '}';
    }
}
