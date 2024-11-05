package org.stus.marketplace.dto;

import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ItemOrderDTO {
    private static final Logger logger = LogManager.getLogger(ItemOrderDTO.class.getName());
    private int id;

    private PersonDTO ownerDTO;

    @NotNull(message = "Some items (item entries) should be chosen")
    private List<ItemEntryDTO> itemEntriesDTO;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        logger.debug("catch itemOrderDTO id: " + id);
        this.id = id;
        logger.info("set id: " + id + " in itemOrderDTO");
    }

    public PersonDTO getOwnerDTO() {
        return ownerDTO;
    }

    public void setOwnerDTO(PersonDTO ownerDTO) {
        logger.debug("catch itemOrderDTO ownerDTO: " + ownerDTO);
        this.ownerDTO = ownerDTO;
        logger.info("set ownerDTO: " + ownerDTO + " in itemOrderDTO");
    }

    public List<ItemEntryDTO> getItemEntriesDTO() {
        return itemEntriesDTO;
    }

    public void setItemEntriesDTO(List<ItemEntryDTO> itemEntriesDTO) {
        logger.debug("catch itemOrderDTO itemEntriesDTO with size: " + itemEntriesDTO.size());
        this.itemEntriesDTO = itemEntriesDTO;
        logger.info("set itemEntriesDTO with size: " + itemEntriesDTO.size() + " in itemOrderDTO");
    }
}