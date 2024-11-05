package org.stus.marketplace.services;

import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.ItemEntry;
import org.stus.marketplace.utils.item_utils.ItemNotFoundException;
import org.stus.marketplace.utils.item_utils.NumberOfItemsIsNotEnoughException;
import org.stus.marketplace.utils.store_utils.ItemIsAbsentInStoreException;
import org.stus.marketplace.utils.store_utils.StoreIsEmptyException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoreService {
    private final ItemService itemService;
    private static final Logger logger = LogManager.getLogger(StoreService.class.getName());

    @Autowired
    public StoreService(ItemService itemService) {
        this.itemService = itemService;
    }

    public void addItemEntry(HttpSession session, ItemEntry itemEntry) {
        logger.debug("catch object HttpSession: " + session.getId());
        logger.debug("catch object ItemEntry with item id: " + itemEntry.getOrderedItem().getId());
        if (session.getAttribute("store")==null) {
            session.setAttribute("store", Arrays.asList(itemEntry));
            logger.info("add itemEntry to store");
        } else {
            List<ItemEntry> itemEntries = new ArrayList<>((List<ItemEntry>) session.getAttribute("store"));
            itemEntries.add(itemEntry);
            session.setAttribute("store", itemEntries);
            logger.info("add itemEntry to store");
        }
    }

    public void updateStore(HttpSession session, List<ItemEntry> itemEntries) {
        logger.debug("catch object HttpSession: " + session.getId());
        logger.debug("catch list ItemEntries for update store with size: " + itemEntries.size());
        session.setAttribute("store", itemEntries);
        logger.info("updating store");
    }

    public void deleteItemEntry(HttpSession session, int itemId) {
        logger.debug("catch object HttpSession: " + session.getId());
        logger.debug("catch itemEntry for delete with item id: " + itemId);
        Optional<Item> foundedItem = itemService.findItemById(itemId);
        List<ItemEntry> itemEntries = new ArrayList<>((List<ItemEntry>)session.getAttribute("store"));

        itemEntries = itemEntries.stream()
                .filter(itemEntry -> itemEntry.getOrderedItem().getId()!=foundedItem.get().getId())
                .collect(Collectors.toList());
        logger.info("deleting itemEntry with item id: " + itemId);

        session.setAttribute("store", itemEntries);
        logger.info("updating store");
    }

    public void clearStore(HttpSession session) {
        logger.debug("catch object HttpSession: " + session.getId());
        session.removeAttribute("store");
        logger.info("clearing store");
    }

    public List<ItemEntry> showStore(HttpSession session) throws StoreIsEmptyException {
        logger.debug("catch object HttpSession: " + session.getId());
        List<ItemEntry> itemEntries = (List<ItemEntry>)session.getAttribute("store");

        return itemEntries;
    }
}
