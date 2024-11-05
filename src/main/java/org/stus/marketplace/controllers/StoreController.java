package org.stus.marketplace.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(StoreController.class.getName());

    @Autowired
    public StoreController(ItemService itemService, StoreService storeService, ModelMapper modelMapper) {
        this.itemService = itemService;
        this.storeService = storeService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/addItemEntryToStore")
    public ResponseEntity<HttpStatus> addItemEntryToStore(HttpServletRequest request, @RequestBody ItemEntryDTO itemEntryDTO) {
        logger.debug("catch HttpServletRequest with session id: " + request.getSession().getId());
        logger.debug("catch ItemEntryDTO with orderedItemDTO id: " + itemEntryDTO.getOrderedItemDTO().getId());

        Optional<Item> item = itemService.findItemById(itemEntryDTO.getOrderedItemDTO().getId());
        HttpSession session = request.getSession();
        if (item.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }
        if ((item.get().getNumberOfItems() - itemEntryDTO.getNumberOfItems()) < 0) {
            throw new NumberOfItemsIsNotEnoughException("Number of items is not enough");
        }

        ItemEntry itemEntry = convertToItemEntry(itemEntryDTO);

        storeService.addItemEntry(session, itemEntry);
        logger.info("add itemEntry with item id: " + itemEntry.getOrderedItem().getId());

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/updateStore")
    public ResponseEntity<HttpStatus> updateStore(@RequestBody List<ItemEntryDTO> itemEntriesDTO, HttpServletRequest request) {
        logger.debug("catch List<ItemEntryDTO> with size: " + itemEntriesDTO.size());
        logger.debug("catch HttpServletRequest with session id: " + request.getSession().getId());

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
        logger.info("converting List<ItemEntryDTO> to List<ItemEntry> with size: " + itemEntries.size());

        storeService.updateStore(session, itemEntries);
        logger.info("updating store used List<ItemEntry> with size: " + itemEntries.size());

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/deleteItemFromStore/{id}")
    public ResponseEntity<HttpStatus> deleteItemEntryFromStore(HttpServletRequest request, @PathVariable("id") int itemId) {
        logger.debug("catch HttpServletRequest with session id: " + request.getSession().getId());
        logger.debug("catch int itemId: " + itemId);

        HttpSession session = request.getSession();
        List<ItemEntry> itemEntries = new ArrayList<>((List<ItemEntry>)session.getAttribute("store"));
        Optional<Item> foundedItem = itemService.findItemById(itemId);
        boolean isPresent = false;

        if (session.getAttribute("store")==null) {
            throw new StoreIsEmptyException("Store is empty");
        }

        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        isPresent = itemEntries.stream()
                .anyMatch(itemEntry -> itemEntry.getOrderedItem().getId()==foundedItem.get().getId());
        if (isPresent==false) {
            throw new ItemIsAbsentInStoreException("Item is absent in store");
        }

        storeService.deleteItemEntry(session, itemId);
        logger.info("deleting ItemEntry with ordereItem id: " + itemId + " from store");

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/clearStore")
    public ResponseEntity<HttpStatus> clearStore(HttpServletRequest request) {
        logger.debug("catch HttpServletRequest with session id: " + request.getSession().getId());

        HttpSession session = request.getSession();
        if (session.getAttribute("store")==null) {
            throw new StoreIsEmptyException("Store is empty");
        }

        storeService.clearStore(session);
        logger.info("clearing store");

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/showStore")
    public List<ItemEntryDTO> showStore(HttpServletRequest request) {
        logger.debug("catch HttpServletRequest with session id: " + request.getSession().getId());

        HttpSession session = request.getSession();
        List<ItemEntry> itemEntries = storeService.showStore(session);
        if (itemEntries==null) {
            throw new StoreIsEmptyException("Store is empty");
        }
        logger.info("get List<ItemEntry> with size: " + itemEntries.size() + " from session");

        List<ItemEntryDTO> itemEntriesDTO = itemEntries.stream()
                .map(itemEntry -> convertToItemEntryDTO(itemEntry))
                .collect(Collectors.toList());
        logger.info("converting List<ItemEntry> to List<ItemEntryDTO> with size: " + itemEntriesDTO.size());

        return itemEntriesDTO;
    }


    private ItemEntry convertToItemEntry(ItemEntryDTO itemEntryDTO) {
        logger.debug("catch ItemEntryDTO with orderedItemDTO id: " + itemEntryDTO.getOrderedItemDTO().getId());
        ItemEntry itemEntry = new ItemEntry();
        itemEntry.setNumberOfItems(itemEntryDTO.getNumberOfItems());
        itemEntry.setOrderedItem(itemService.findItemById(itemEntryDTO.getOrderedItemDTO().getId()).get());
        logger.info("converting ItemEntryDTO to ItemEntry with orderedItem id: "
                + itemEntry.getOrderedItem().getId());

        return itemEntry;
    }

    private ItemEntryDTO convertToItemEntryDTO(ItemEntry itemEntry) {
        logger.debug("catch ItemEntry with orderedItem id: " + itemEntry.getOrderedItem().getId());
        ItemEntryDTO itemEntryDTO = new ItemEntryDTO();
        itemEntryDTO.setNumberOfItems(itemEntry.getNumberOfItems());
        ItemDTO itemDTO = modelMapper.map(itemEntry.getOrderedItem(), ItemDTO.class);
        itemEntryDTO.setOrderedItemDTO(itemDTO);
        logger.info("converting ItemEntry to ItemEntryDTO with orderedItemDTO id: " +
                itemEntryDTO.getOrderedItemDTO().getId());

        return itemEntryDTO;
    }


    @ExceptionHandler
    private ResponseEntity<StoreErrorResponse> handleException(NumberOfItemsIsNotEnoughException exc) {
        logger.error("number of items is not enough", exc);
        StoreErrorResponse storeErrorResponse = new StoreErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<StoreErrorResponse>(storeErrorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    private ResponseEntity<StoreErrorResponse> handleException(StoreIsEmptyException exc) {
        logger.error("store is empty", exc);
        StoreErrorResponse storeErrorResponse = new StoreErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<StoreErrorResponse>(storeErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<StoreErrorResponse> handleException(ItemIsAbsentInStoreException exc) {
        logger.error("item is absent in store", exc);
        StoreErrorResponse storeErrorResponse = new StoreErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<StoreErrorResponse>(storeErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(ItemNotFoundException exc) {
        logger.error("item not found", exc);
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.NOT_FOUND);
    }
}
