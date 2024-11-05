package org.stus.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@Entity
@Table(name = "item_entry")
public class ItemEntry {
    private static final Logger logger = LogManager.getLogger(ItemEntry.class.getName());

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
        logger.debug("catch itemEntry id: " + id);
        this.id = id;
        logger.info("set id: " + id + " in itemEntry");
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        logger.debug("catch itemEntry numberOfItems: " + numberOfItems);
        this.numberOfItems = numberOfItems;
        logger.info("set numberOfItems: " + numberOfItems + " in itemEntry");
    }

    public Item getOrderedItem() {
        return orderedItem;
    }

    public void setOrderedItem(Item orderedItem) {
        logger.debug("catch itemEntry orderedItem with id: " + orderedItem.getId());
        this.orderedItem = orderedItem;
        logger.info("set orderedItem with id: " + orderedItem.getId() + " in itemEntry");
    }

    public ItemOrder getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(ItemOrder itemOrder) {
        logger.debug("catch itemOrder with owner id: " + itemOrder.getOwner().getId());
        this.itemOrder = itemOrder;
        logger.info("set itemOrder with owner id: " + itemOrder.getOwner().getId() + " in itemEntry");
    }


    /* //check equals only by field "id"
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEntry itemEntry = (ItemEntry) o;
        return id == itemEntry.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    */

    //check equals by all fields
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEntry itemEntry = (ItemEntry) o;
        return id == itemEntry.id && numberOfItems == itemEntry.numberOfItems && Objects.equals(orderedItem, itemEntry.orderedItem) && Objects.equals(itemOrder, itemEntry.itemOrder);
    }

    //check equals by all fields
    @Override
    public int hashCode() {
        return Objects.hash(id, numberOfItems, orderedItem, itemOrder);
    }

    @Override
    public String toString() {
        return "ItemEntry{" +
                "id=" + id +
                ", numberOfItems=" + numberOfItems +
                ", orderedItem{id=" + orderedItem.getId() + "}" +
                ", itemOrder{id=" + itemOrder.getId() + "}" +
                '}';
    }
}
