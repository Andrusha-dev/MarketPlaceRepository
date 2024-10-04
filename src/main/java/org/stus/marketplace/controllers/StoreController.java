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
    private final ModelMapper modelMapper;

    @Autowired
    public StoreController(ItemService itemService, ModelMapper modelMapper) {
        this.itemService = itemService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/addItemToStore/{id}")
    public ResponseEntity<HttpStatus> addItemToStore(HttpServletRequest request, @RequestBody StoreDTO storeDTO, @PathVariable("id") int id) {
        Optional<Item> foundedItem = itemService.findItemById(id);
        Store store = this.convertToStore(storeDTO);
        HttpSession session = request.getSession();

        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }
        if ((foundedItem.get().getNumberOfItems() - store.getNumberOfItems()) < 0) {
            throw new NumberOfItemsIsNotEnoughException("Number of items is not Enough");
        }


        if (session.getAttribute("stores")==null) {
            session.setAttribute("stores", Arrays.asList(store));
        } else {
            List<Store> stores = new ArrayList<>((List<Store>) session.getAttribute("stores"));
            stores.add(store);
            session.setAttribute("stores", stores);
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/updateStore")
    public ResponseEntity<HttpStatus> updateStore(@RequestBody List<StoreDTO> storesDTO, HttpServletRequest request) {
        List<Store> stores = new ArrayList<>();
        HttpSession session = request.getSession();

        for (StoreDTO storeDTO : storesDTO) {
            stores.add(this.convertToStore(storeDTO));
        }

        for (Store store : stores) {
            Optional<Item> item = itemService.findItemById(store.getItemId());
            if (item.isEmpty()) {
                throw new ItemNotFoundException("Item not found");
            }
            if ((item.get().getNumberOfItems() - store.getNumberOfItems()) < 0) {
                throw new NumberOfItemsIsNotEnoughException("Number of items is not enough");
            }
        }

        session.setAttribute("stores", stores);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/deleteItemFromStore/{id}")
    public ResponseEntity<HttpStatus> deleteItemFromStore(HttpServletRequest request, @PathVariable("id") int id) {
        Optional<Item> foundedItem = itemService.findItemById(id);
        HttpSession session = request.getSession();
        List<Store> stores = new ArrayList<>((List<Store>)session.getAttribute("stores"));
        boolean isPresent = false;

        if (session.getAttribute("stores")==null) {
            throw new StoreIsEmptyException("Store is empty");
        }

        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        isPresent = stores.stream()
                                .anyMatch(store -> store.getItemId()==foundedItem.get().getId());
        if (isPresent==false) {
            throw new ItemIsAbsentInStoreException("Item is absent in store");
        }

        stores = stores.stream()
                    .filter(store -> store.getItemId()!=foundedItem.get().getId())
                    .collect(Collectors.toList());

        session.setAttribute("stores", stores);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/clearStore")
    public ResponseEntity<HttpStatus> clearStore(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("stores")==null) {
            throw new StoreIsEmptyException("Store is empty");
        }

        session.removeAttribute("stores");

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/showStore")
    public List<StoreDTO> showStore(HttpServletRequest request) {
        HttpSession session = request.getSession();
        List<Store> stores = (List<Store>)session.getAttribute("stores");

        if (stores==null) {
            throw new StoreIsEmptyException("Store is empty");
        }

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
