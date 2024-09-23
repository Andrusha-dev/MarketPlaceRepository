package org.stus.marketplace.utils.item_utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.stus.marketplace.dto.ItemDTO;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.services.ItemService;

import java.util.List;

@Component
public class ItemDTOValidator implements Validator {
    private final ItemService itemService;

    @Autowired
    public ItemDTOValidator(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ItemDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ItemDTO itemDTO = (ItemDTO)target;

        List<Item> items = itemService.findAllItems();
        for (Item item : items) {
            if (item.getItemName().equals(itemDTO.getItemName())) {
                errors.rejectValue("itemName", "", "Such item name are registered");
            }
        }
    }
}
