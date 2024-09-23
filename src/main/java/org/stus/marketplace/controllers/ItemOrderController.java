package org.stus.marketplace.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.stus.marketplace.dto.ItemOrderDTO;
import org.stus.marketplace.models.ItemOrder;
import org.stus.marketplace.services.ItemOrderService;
import org.stus.marketplace.utils.itemOrder_utils.ItemOrderErrorResponse;
import org.stus.marketplace.utils.itemOrder_utils.ItemOrderNotCreateException;
import org.stus.marketplace.utils.itemOrder_utils.ItemOrderNotFoundException;
import org.stus.marketplace.utils.itemOrder_utils.ItemOrderNotUpdateException;
import org.stus.marketplace.utils.item_utils.ItemErrorResponse;
import org.stus.marketplace.utils.item_utils.ItemNotFoundException;
import org.stus.marketplace.utils.person_utils.PersonErrorResponse;
import org.stus.marketplace.utils.person_utils.PersonNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itemOrder")
public class ItemOrderController {
    private final ItemOrderService itemOrderService;
    private final ModelMapper modelMapper;

    @Autowired
    public ItemOrderController(ItemOrderService itemOrderService, ModelMapper modelMapper) {
        this.itemOrderService = itemOrderService;
        this.modelMapper = modelMapper;
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> createItemOrder(@RequestBody @Valid ItemOrderDTO itemOrderDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                builder.append(error.getField() + " : " + error.getDefaultMessage() + ";");
            }

            throw new ItemOrderNotCreateException(builder.toString());
        }

        itemOrderService.saveItemOrder(convertToItemOrder(itemOrderDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping()
    public List<ItemOrderDTO> getAllItemOrders() {
        return itemOrderService.findAllItemOrders().stream()
                .map(i -> convertToItemOrderDTO(i))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemOrderDTO getItemOrderById(@PathVariable("id") int id) {
        Optional<ItemOrder> foundedItemOrder = itemOrderService.findItemOrderById(id);
        if (foundedItemOrder.isEmpty()) {
            throw new ItemOrderNotFoundException("Item order not found");
        }

        return convertToItemOrderDTO(foundedItemOrder.get());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updateItemOrder(@PathVariable("id") int id,
                                                      @RequestBody @Valid ItemOrderDTO itemOrderDTO, BindingResult bindingResult) {

        Optional<ItemOrder> foundedItemOrder = itemOrderService.findItemOrderById(id);
        if (foundedItemOrder.isEmpty()) {
            throw new ItemOrderNotFoundException("Item order not found");
        }

        ItemOrder updatedItemOrder = convertToItemOrder(itemOrderDTO);
        updatedItemOrder.setId(id);

        if (bindingResult.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                builder.append(error.getField() + " : " + error.getDefaultMessage() + ";");
            }

            throw new ItemOrderNotUpdateException(builder.toString());
        }

        itemOrderService.updateItemOrder(updatedItemOrder);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteItemOrder(@PathVariable("id") int id) {
        Optional<ItemOrder> deletedItemOrder = itemOrderService.findItemOrderById(id);
        if (deletedItemOrder.isEmpty()) {
            throw  new ItemOrderNotFoundException("Item order not found");
        }

        itemOrderService.deleteItemOrder(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    private ItemOrder convertToItemOrder(ItemOrderDTO itemOrderDTO) {
        return modelMapper.map(itemOrderDTO, ItemOrder.class);
    }

    private ItemOrderDTO convertToItemOrderDTO(ItemOrder itemOrder) {
        return modelMapper.map(itemOrder, ItemOrderDTO.class);
    }


    @ExceptionHandler
    private ResponseEntity<ItemOrderErrorResponse> handleException(ItemOrderNotCreateException exc) {
        ItemOrderErrorResponse itemOrderErrorResponse = new ItemOrderErrorResponse(exc.getMessage(), System.currentTimeMillis());

        return new ResponseEntity<ItemOrderErrorResponse>(itemOrderErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ItemOrderErrorResponse> handleException(ItemOrderNotFoundException exc) {
        ItemOrderErrorResponse itemOrderErrorResponse = new ItemOrderErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemOrderErrorResponse>(itemOrderErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ItemOrderErrorResponse> handleException(ItemOrderNotUpdateException exc) {
        ItemOrderErrorResponse itemOrderErrorResponse = new ItemOrderErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemOrderErrorResponse>(itemOrderErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException exc) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<PersonErrorResponse>(personErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(ItemNotFoundException exc) {
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.NOT_FOUND);
    }
}
