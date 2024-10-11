package org.stus.marketplace.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stus.marketplace.dto.ItemDTO;
import org.stus.marketplace.dto.ItemEntryDTO;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.ItemEntry;
import org.stus.marketplace.services.ItemService;
import org.stus.marketplace.services.StoreService;
import org.stus.marketplace.utils.item_utils.ItemErrorResponse;
import org.stus.marketplace.utils.item_utils.ItemNotFoundException;
import org.stus.marketplace.utils.item_utils.NumberOfItemsIsNotEnoughException;
import org.stus.marketplace.utils.store_utils.ItemIsAbsentInStoreException;
import org.stus.marketplace.utils.store_utils.StoreErrorResponse;
import org.stus.marketplace.utils.store_utils.StoreIsEmptyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/store")
public class StoreController {
    private final ItemService itemService;
    private final StoreService storeService;
    private final ModelMapper modelMapper;

    @Autowired
    public StoreController(ItemService itemService, StoreService storeService, ModelMapper modelMapper) {
        this.itemService = itemService;
        this.storeService = storeService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/addItemEntryToStore")
    public ResponseEntity<HttpStatus> addItemEntryToStore(HttpServletRequest request, @RequestBody ItemEntryDTO itemEntryDTO) {
        Optional<Item> item = itemService.findItemById(itemEntryDTO.getOrderedItemDTO().getId());
        HttpSession session = request.getSession();
        if (item.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }
        if ((item.get().getNumberOfItems() - itemEntryDTO.getNumberOfItems()) < 0) {
            throw new NumberOfItemsIsNotEnoughException("Number of items is not enough");
        }

        ItemEntry itemEntry = convertToItemEntry(itemEntryDTO);

        storeService.addItem(session, itemEntry);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/updateStore")
    public ResponseEntity<HttpStatus> updateStore(@RequestBody List<ItemEntryDTO> itemEntriesDTO, HttpServletRequest request) {
        List<ItemEntry> itemEntries = new ArrayList<>();
        HttpSession session = request.getSession();

        for (ItemEntryDTO itemEntryDTO : itemEntriesDTO) {
            itemEntries.add(this.convertToItemEntry(itemEntryDTO));
        }

        for (ItemEntry itemEntry : itemEntries) {
            Optional<Item> item = itemService.findItemById(itemEntry.getOrderedItem().getId());
            if (item.isEmpty()) {
                throw new ItemNotFoundException("Item not found");
            }
            if ((item.get().getNumberOfItems() - itemEntry.getNumberOfItems()) < 0) {
                throw new NumberOfItemsIsNotEnoughException("Number of items is not enough");
            }
        }

        storeService.updateStore(session, itemEntries);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/deleteItemFromStore/{id}")
    public ResponseEntity<HttpStatus> deleteItemFromStore(HttpServletRequest request, @PathVariable("id") int id) {
        HttpSession session = request.getSession();
        List<ItemEntry> itemEntries = new ArrayList<>((List<ItemEntry>)session.getAttribute("store"));
        Optional<Item> foundedItem = itemService.findItemById(id);
        boolean isPresent = false;

        if (session.getAttribute("store")==null) {
            throw new StoreIsEmptyException("Store is empty");
        }

        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        isPresent = itemEntries.stream().anyMatch(itemEntry -> itemEntry.getOrderedItem().getId()==foundedItem.get().getId());
        if (isPresent==false) {
            throw new ItemIsAbsentInStoreException("Item is absent in store");
        }

        storeService.deleteItem(session, id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/clearStore")
    public ResponseEntity<HttpStatus> clearStore(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("store")==null) {
            throw new StoreIsEmptyException("Store is empty");
        }

        storeService.clearStore(session);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/showStore")
    public List<ItemEntryDTO> showStore(HttpServletRequest request) {
        HttpSession session = request.getSession();
        List<ItemEntry> itemEntries = storeService.showStore(session);
        if (itemEntries==null) {
            throw new StoreIsEmptyException("Store is empty");
        }

        List<ItemEntryDTO> itemEntriesDTO = itemEntries.stream()
                .map(itemEntry -> convertToItemEntryDTO(itemEntry))
                .collect(Collectors.toList());

        return itemEntriesDTO;
    }


    private ItemEntry convertToItemEntry(ItemEntryDTO itemEntryDTO) {
        ItemEntry itemEntry = new ItemEntry();
        itemEntry.setNumberOfItems(itemEntryDTO.getNumberOfItems());
        itemEntry.setOrderedItem(itemService.findItemById(itemEntryDTO.getOrderedItemDTO().getId()).get());
        return itemEntry;
    }

    private ItemEntryDTO convertToItemEntryDTO(ItemEntry itemEntry) {
        ItemEntryDTO itemEntryDTO = new ItemEntryDTO();
        itemEntryDTO.setNumberOfItems(itemEntry.getNumberOfItems());
        ItemDTO itemDTO = modelMapper.map(itemEntry.getOrderedItem(), ItemDTO.class);
        itemEntryDTO.setOrderedItemDTO(itemDTO);

        return itemEntryDTO;
    }


    @ExceptionHandler
    private ResponseEntity<StoreErrorResponse> handleException(NumberOfItemsIsNotEnoughException exc) {
        StoreErrorResponse storeErrorResponse = new StoreErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<StoreErrorResponse>(storeErrorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    private ResponseEntity<StoreErrorResponse> handleException(StoreIsEmptyException exc) {
        StoreErrorResponse storeErrorResponse = new StoreErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<StoreErrorResponse>(storeErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<StoreErrorResponse> handleException(ItemIsAbsentInStoreException exc) {
        StoreErrorResponse storeErrorResponse = new StoreErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<StoreErrorResponse>(storeErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(ItemNotFoundException exc) {
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.NOT_FOUND);
    }
}
