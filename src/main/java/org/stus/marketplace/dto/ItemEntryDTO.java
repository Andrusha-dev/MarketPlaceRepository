package org.stus.marketplace.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.stus.marketplace.models.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemEntryDTO {
    private static final Logger logger = LogManager.getLogger(ItemEntryDTO.class.getName());
    private int id;

    @Min(value = 1, message = "Number of items should be higher then 0")
    private int numberOfItems;

    @NotNull(message = "Some item should be chosen")
    private ItemDTO orderedItemDTO;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        logger.debug("catch itemEntryDTO id: " + id);
        this.id = id;
        logger.info("set id: " + id + " in itemEntryDTO");
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        logger.debug("catch itemEntryDTO numberOfItems: " + numberOfItems);
        this.numberOfItems = numberOfItems;
        logger.info("set numberOfItems: " + numberOfItems + " in itemEntryDTO");
    }

    public ItemDTO getOrderedItemDTO() {
        return orderedItemDTO;
    }

    public void setOrderedItemDTO(ItemDTO orderedItemDTO) {
        logger.debug("catch itemEntryDTO orderedItemDTO: " + orderedItemDTO);
        this.orderedItemDTO = orderedItemDTO;
        logger.info("set orderedItemDTO: " + orderedItemDTO + " in itemEntryDTO");
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
