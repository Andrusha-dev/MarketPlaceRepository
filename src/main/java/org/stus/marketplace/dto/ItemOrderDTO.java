package org.stus.marketplace.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ItemOrderDTO {
    private int id;

    private PersonDTO ownerDTO;

    @NotNull(message = "Some items (item entries) should be chosen")
    private List<ItemEntryDTO> itemEntriesDTO;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PersonDTO getOwnerDTO() {
        return ownerDTO;
    }

    public void setOwnerDTO(PersonDTO ownerDTO) {
        this.ownerDTO = ownerDTO;
    }

    public List<ItemEntryDTO> getItemEntriesDTO() {
        return itemEntriesDTO;
    }

    public void setItemEntriesDTO(List<ItemEntryDTO> itemEntriesDTO) {
        this.itemEntriesDTO = itemEntriesDTO;
    }
}