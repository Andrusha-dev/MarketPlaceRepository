package org.stus.marketplace.utils.item_utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.services.ItemService;

import java.util.List;

@Component
public class ItemValidator implements Validator {
    private final ItemService itemService;

    @Autowired
    public ItemValidator(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item validatedItem = (Item)target;

        List<Item> items = itemService.findAllItems();
        for (Item item : items) {
            if ((item.getItemName().equals(validatedItem.getItemName()))&&(item.getId()!= validatedItem.getId())) {
                errors.rejectValue("itemName", "", "Such itemName are registered");
            }
        }
    }
}
