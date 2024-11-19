package org.stus.marketplace.controllers;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.stus.marketplace.dto.ItemDTO;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.services.ItemService;
import org.stus.marketplace.utils.item_utils.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/item")
public class ItemController {
    private final ItemService itemService;
    private final ModelMapper modelMapper;
    private final ItemDTOValidator itemDTOValidator;
    private final ItemValidator itemValidator;
    private static final Logger logger = LogManager.getLogger(ItemController.class.getName());

    @Autowired
    public ItemController(ItemService itemService, ModelMapper modelMapper, ItemDTOValidator itemDTOValidator, ItemValidator itemValidator) {
        this.itemService = itemService;
        this.modelMapper = modelMapper;
        this.itemDTOValidator = itemDTOValidator;
        this.itemValidator = itemValidator;
    }


    @GetMapping()
    public List<ItemDTO> getAllItems() {
        List<ItemDTO> itemsDTO = itemService.findAllItems().stream()
                .map(i -> convertToItemDTO(i))
                .collect(Collectors.toList());
        logger.info("find all items");
        return itemsDTO;
    }

    @GetMapping("/{id}")
    public ItemDTO getItemById(@PathVariable("id") int id) {
        logger.debug("catch item id: " + id);
        Optional<Item> foundedItem = itemService.findItemById(id);
        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }
        logger.info("find item with id: " + id);

        return convertToItemDTO(foundedItem.get());
    }

    @GetMapping("/itemName/{itemName}")
    public ItemDTO getItemByItemName(@PathVariable("itemName") String itemName) {
        logger.debug("catch itemName: " + itemName);
        Optional<Item> foundedItem = itemService.findItemByItemName(itemName);

        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }
        logger.info("find item with name: " + itemName);

        return convertToItemDTO(foundedItem.get());
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> createItem(@RequestBody @Valid ItemDTO itemDTO, BindingResult bindingResult) {
        logger.debug("catch itemDTO with itemName: " + itemDTO.getItemName());

        itemDTOValidator.validate(itemDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                builder.append(error.getField() + " : " + error.getDefaultMessage() + ";");
            }

            throw new ItemNotCreateException(builder.toString());
        }
        logger.info("validation of itemDTO completed successfully");

        itemService.saveItem(convertToItem(itemDTO));
        logger.info("saving item with name: " + itemDTO.getItemName());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updateItem(@RequestBody @Valid ItemDTO itemDTO,
                                                 BindingResult bindingResult, @PathVariable("id") int id) {
        logger.debug("catch itemDTO with itemName: " + itemDTO.getItemName());

        Optional<Item> foundedItem = itemService.findItemById(id);
        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }
        logger.info("find item with id: " + id);

        Item updatedItem = convertToItem(itemDTO);
        updatedItem.setId(id);

        itemValidator.validate(updatedItem, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                builder.append(error.getField() + " : " + error.getDefaultMessage() + ";");
            }

            throw new ItemNotUpdateException(builder.toString());
        }
        logger.info("validation of item with id: " + id + " complated successfully");

        itemService.updateItem(updatedItem);
        logger.info("updating item with id: " + id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteItem(@PathVariable("id") int id) {
        logger.debug("catch item id: " + id);
        Optional<Item> foundedItem = itemService.findItemById(id);
        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }
        logger.info("find item with id: " + id);

        itemService.deleteItem(id);
        logger.info("deleting item with id: " + id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    private Item convertToItem(ItemDTO itemDTO) {
        logger.debug("catch itemDTO with itemName: " + itemDTO.getItemName());

        Item item = modelMapper.map(itemDTO, Item.class);
        logger.info("converting itemDTO to Item with itemName: " + item.getItemName());
        return item;
    }

    private ItemDTO convertToItemDTO(Item item) {
        logger.debug("catch item with itemName: " + item.getItemName());
        ItemDTO itemDTO =  modelMapper.map(item, ItemDTO.class);
        logger.info("converting itemDTO to item with itemName: " + itemDTO.getItemName());
        return itemDTO;
    }


    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(ItemNotCreateException exc) {
        logger.error("item not create", exc);
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(ItemNotFoundException exc) {
        logger.error("item not found", exc);
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(ItemNotUpdateException exc) {
        logger.error("item not update", exc);
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.BAD_REQUEST);
    }


}
