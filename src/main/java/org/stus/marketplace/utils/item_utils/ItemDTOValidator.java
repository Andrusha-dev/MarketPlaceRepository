package org.stus.marketplace.utils.item_utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(ItemDTOValidator.class.getName());

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
        logger.debug("catch itemDTO with name: " + itemDTO.getItemName());

        List<Item> items = itemService.findAllItems();
        for (Item item : items) {
            if (item.getItemName().equals(itemDTO.getItemName())) {
                errors.rejectValue("itemName", "", "Such item name are registered");
            }
        }
    }
}
