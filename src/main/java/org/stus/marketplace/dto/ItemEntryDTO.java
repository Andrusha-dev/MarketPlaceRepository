package org.stus.marketplace.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.stus.marketplace.models.Item;

public class ItemEntryDTO {
    private int id;

    @Min(value = 1, message = "Number of items should be higher then 0")
    private int numberOfItems;

    @NotNull(message = "Some item should be chosen")
    private ItemDTO orderedItemDTO;


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

    public ItemDTO getOrderedItemDTO() {
        return orderedItemDTO;
    }

    public void setOrderedItemDTO(ItemDTO orderedItemDTO) {
        this.orderedItemDTO = orderedItemDTO;
    }


    @Override
    public String toString() {
        return "ItemEntryDTO{" +
                "id=" + id +
                ", numberOfItems=" + numberOfItems +
                ", orderedItemDTO=" + orderedItemDTO +
                '}';
    }
}
