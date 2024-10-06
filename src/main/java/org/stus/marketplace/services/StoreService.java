package org.stus.marketplace.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.Store;
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

    public void addItem(HttpSession session, Store store, int id) throws ItemNotFoundException, NumberOfItemsIsNotEnoughException {
        Optional<Item> foundedItem = itemService.findItemById(id);

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
    }

    public void updateStore(HttpSession session, List<Store> stores) throws ItemNotFoundException, NumberOfItemsIsNotEnoughException {
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
    }

    public void deleteItem(HttpSession session, int id) throws StoreIsEmptyException,
                                                                ItemNotFoundException,
                                                                ItemIsAbsentInStoreException {

        Optional<Item> foundedItem = itemService.findItemById(id);
        List<Store> stores = new ArrayList<>((List<Store>)session.getAttribute("stores"));
        boolean isPresent = false;

        if (session.getAttribute("stores")==null) {
            throw new StoreIsEmptyException("Store is empty");
        }

        if (foundedItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        isPresent = stores.stream().anyMatch(store -> store.getItemId()==foundedItem.get().getId());
        if (isPresent==false) {
            throw new ItemIsAbsentInStoreException("Item is absent in store");
        }

        stores = stores.stream()
                .filter(store -> store.getItemId()!=foundedItem.get().getId())
                .collect(Collectors.toList());

        session.setAttribute("stores", stores);
    }

    public void clearStore(HttpSession session) throws StoreIsEmptyException {
        if (session.getAttribute("stores")==null) {
            throw new StoreIsEmptyException("Store is empty");
        }

        session.removeAttribute("stores");
    }

    public List<Store> showStore(HttpSession session) throws StoreIsEmptyException {
        List<Store> stores = (List<Store>)session.getAttribute("stores");

        if (stores==null) {
            throw new StoreIsEmptyException("Store is empty");
        }

        return stores;
    }
}
