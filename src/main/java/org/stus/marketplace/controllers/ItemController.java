package org.stus.marketplace.controllers;

import jakarta.validation.Valid;
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

    @Autowired
    public ItemController(ItemService itemService, ModelMapper modelMapper, ItemDTOValidator itemDTOValidator, ItemValidator itemValidator) {
        this.itemService = itemService;
        this.modelMapper = modelMapper;
        this.itemDTOValidator = itemDTOValidator;
        this.itemValidator = itemValidator;
    }


    @GetMapping()
    public List<ItemDTO> getAllItems() {
        return itemService.findAllItems().stream()
                .map(i -> convertToItemDTO(i))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDTO getItemById(@PathVariable("id") int id) {
        Optional<Item> foundedItem = itemService.findItemById(id);
        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        return convertToItemDTO(foundedItem.get());
    }

    @GetMapping("/itemName/{itemName}")
    public ItemDTO getItemByItemName(@PathVariable("itemName") String itemName) {
        Optional<Item> foundedItem = itemService.findItemByItemName(itemName);

        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        return convertToItemDTO(foundedItem.get());
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> createItem(@RequestBody @Valid ItemDTO itemDTO, BindingResult bindingResult) {
        itemDTOValidator.validate(itemDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                builder.append(error.getField() + " : " + error.getDefaultMessage() + ";");
            }

            throw new ItemNotCreateException(builder.toString());
        }

        itemService.saveItem(convertToItem(itemDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updateItem(@RequestBody @Valid ItemDTO itemDTO,
                                                 BindingResult bindingResult, @PathVariable("id") int id) {

        Optional<Item> foundedItem = itemService.findItemById(id);
        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

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

        itemService.updateItem(updatedItem);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteItem(@PathVariable("id") int id) {
        Optional<Item> foundedItem = itemService.findItemById(id);
        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        itemService.deleteItem(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    private Item convertToItem(ItemDTO itemDTO) {
        return modelMapper.map(itemDTO, Item.class);
    }

    private ItemDTO convertToItemDTO(Item item) {
        return modelMapper.map(item, ItemDTO.class);
    }


    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(ItemNotCreateException exc) {
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(ItemNotFoundException exc) {
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(ItemNotUpdateException exc) {
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.BAD_REQUEST);
    }


}
