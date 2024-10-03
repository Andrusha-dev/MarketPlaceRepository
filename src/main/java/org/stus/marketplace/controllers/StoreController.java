package org.stus.marketplace.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stus.marketplace.dto.ItemDTO;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.services.ItemService;
import org.stus.marketplace.utils.item_utils.ItemErrorResponse;
import org.stus.marketplace.utils.item_utils.ItemNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/store")
public class StoreController {
    private final ItemService itemService;
    private final ModelMapper modelMapper;

    @Autowired
    public StoreController(ItemService itemService, ModelMapper modelMapper) {
        this.itemService = itemService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/addItemToStore/{id}")
    public ResponseEntity<HttpStatus> addItemToStore(HttpServletRequest request, @PathVariable("id") int id) {
        Optional<Item> foundedItem = itemService.findItemById(id);
        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        HttpSession session = request.getSession();
        List<Item> items = null;

        if (session.getAttribute("items")==null) {
            session.setAttribute("items", Arrays.asList(foundedItem.get()));
        } else {
            items = new ArrayList<>((List<Item>) session.getAttribute("items"));
            items.add(foundedItem.get());
            session.setAttribute("items", items);
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    /*
    @DeleteMapping("/deleteItemFromStore/{id}")
    public ResponseEntity<HttpStatus> deleteItemFromStore(HttpServletRequest request, @PathVariable("id") int id) {
        HttpSession session = request.getSession();
        if (session.getAttribute("items")==null) {

        }
        Optional<Item> foundedItem = itemService.findItemById(id);
        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }


        List<Item> items = null;
    }
    */

    @GetMapping("/showStore")
    public List<ItemDTO> showStore(HttpServletRequest request) {
        HttpSession session = request.getSession();

        List<Item> items = (List<Item>)session.getAttribute("items");


        if (items==null) {
            return new ArrayList<>();
        }

        List<ItemDTO> itemsDTO = items.stream()
                .map(item -> convertToItemDTO(item))
                .collect(Collectors.toList());

        return itemsDTO;
    }


    private Item convertToItem(ItemDTO itemDTO) {
        return modelMapper.map(itemDTO, Item.class);
    }

    private ItemDTO convertToItemDTO(Item item) {
        return modelMapper.map(item, ItemDTO.class);
    }


    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(ItemNotFoundException exc) {
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
