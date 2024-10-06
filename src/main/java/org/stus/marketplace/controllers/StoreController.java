package org.stus.marketplace.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stus.marketplace.dto.ItemDTO;
import org.stus.marketplace.dto.StoreDTO;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.Store;
import org.stus.marketplace.services.ItemService;
import org.stus.marketplace.services.StoreService;
import org.stus.marketplace.utils.item_utils.ItemErrorResponse;
import org.stus.marketplace.utils.item_utils.ItemNotFoundException;
import org.stus.marketplace.utils.item_utils.NumberOfItemsIsNotEnoughException;
import org.stus.marketplace.utils.store_utils.ItemIsAbsentInStoreException;
import org.stus.marketplace.utils.store_utils.StoreErrorResponse;
import org.stus.marketplace.utils.store_utils.StoreIsEmptyException;

import java.util.ArrayList;
import java.util.Arrays;
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

    @PostMapping("/addItemToStore/{id}")
    public ResponseEntity<HttpStatus> addItemToStore(HttpServletRequest request, @RequestBody StoreDTO storeDTO, @PathVariable("id") int id) {
        Store store = this.convertToStore(storeDTO);
        HttpSession session = request.getSession();

        storeService.addItem(session, store, id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/updateStore")
    public ResponseEntity<HttpStatus> updateStore(@RequestBody List<StoreDTO> storesDTO, HttpServletRequest request) {
        List<Store> stores = new ArrayList<>();
        HttpSession session = request.getSession();

        for (StoreDTO storeDTO : storesDTO) {
            stores.add(this.convertToStore(storeDTO));
        }

        storeService.updateStore(session, stores);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/deleteItemFromStore/{id}")
    public ResponseEntity<HttpStatus> deleteItemFromStore(HttpServletRequest request, @PathVariable("id") int id) {
        HttpSession session = request.getSession();

        storeService.deleteItem(session, id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/clearStore")
    public ResponseEntity<HttpStatus> clearStore(HttpServletRequest request) {
        HttpSession session = request.getSession();

        storeService.clearStore(session);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/showStore")
    public List<StoreDTO> showStore(HttpServletRequest request) {
        HttpSession session = request.getSession();
        List<Store> stores = storeService.showStore(session);

        List<StoreDTO> storesDTO = stores.stream()
                .map(store -> convertToStoreDTO(store))
                .collect(Collectors.toList());

        return storesDTO;
    }


    private Store convertToStore(StoreDTO storeDTO) {
        return modelMapper.map(storeDTO, Store.class);
    }

    private StoreDTO convertToStoreDTO(Store store) {
        return modelMapper.map(store, StoreDTO.class);
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
