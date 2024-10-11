package org.stus.marketplace.services;

import jakarta.servlet.http.HttpSession;
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

    @Autowired
    public StoreService(ItemService itemService) {
        this.itemService = itemService;
    }

    //Methods for change store in session
    public void addItem(HttpSession session, ItemEntry itemEntry) {
        System.out.println(itemEntry);

        if (session.getAttribute("store")==null) {
            session.setAttribute("store", Arrays.asList(itemEntry));
        } else {
            List<ItemEntry> itemEntries = new ArrayList<>((List<ItemEntry>) session.getAttribute("store"));
            itemEntries.add(itemEntry);
            session.setAttribute("store", itemEntries);
        }
    }

    public void updateStore(HttpSession session, List<ItemEntry> itemEntries) {
        session.setAttribute("store", itemEntries);
    }

    public void deleteItem(HttpSession session, int id) {
        Optional<Item> foundedItem = itemService.findItemById(id);
        List<ItemEntry> itemEntries = new ArrayList<>((List<ItemEntry>)session.getAttribute("store"));

        itemEntries = itemEntries.stream()
                .filter(itemEntry -> itemEntry.getOrderedItem().getId()!=foundedItem.get().getId())
                .collect(Collectors.toList());

        session.setAttribute("store", itemEntries);
    }

    public void clearStore(HttpSession session) {
        session.removeAttribute("store");
    }

    public List<ItemEntry> showStore(HttpSession session) throws StoreIsEmptyException {
        List<ItemEntry> itemEntries = (List<ItemEntry>)session.getAttribute("store");

        return itemEntries;
    }
}
