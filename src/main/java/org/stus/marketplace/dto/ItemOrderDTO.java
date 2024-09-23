package org.stus.marketplace.dto;

import jakarta.validation.constraints.NotNull;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.Person;

public class ItemOrderDTO {
    @NotNull(message = "Owner should be defined")
    private PersonDTO owner;

    @NotNull(message = "Some item should be chosen")
    private ItemDTO orderedItem;


    public PersonDTO getOwner() {
        return owner;
    }

    public void setOwner(PersonDTO owner) {
        this.owner = owner;
    }

    public ItemDTO getOrderedItem() {
        return orderedItem;
    }

    public void setOrderedItem(ItemDTO orderedItem) {
        this.orderedItem = orderedItem;
    }
}