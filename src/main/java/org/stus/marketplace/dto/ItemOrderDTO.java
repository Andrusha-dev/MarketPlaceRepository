package org.stus.marketplace.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ItemOrderDTO {
    private int id;

    @NotNull(message = "Owner should be defined")
    private PersonDTO owner;

    @NotNull(message = "Some items (item entries) should be chosen")
    private List<ItemEntryDTO> itemEntriesDTO;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PersonDTO getOwner() {
        return owner;
    }

    public void setOwner(PersonDTO owner) {
        this.owner = owner;
    }

    public List<ItemEntryDTO> getItemEntriesDTO() {
        return itemEntriesDTO;
    }

    public void setItemEntriesDTO(List<ItemEntryDTO> itemEntriesDTO) {
        this.itemEntriesDTO = itemEntriesDTO;
    }
}