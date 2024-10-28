package org.stus.marketplace.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.ItemEntry;
import org.stus.marketplace.models.ItemOrder;
import org.stus.marketplace.models.Person;
import org.stus.marketplace.repositories.ItemOrderRepository;
import org.stus.marketplace.utils.ItemEntry_utils.ItemEntryNotFoundException;
import org.stus.marketplace.utils.item_utils.ItemNotFoundException;
import org.stus.marketplace.utils.person_utils.PersonNotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ItemOrderService {
    private final ItemOrderRepository itemOrderRepository;
    private final ItemService itemService;
    private final PersonService personService;
    private final StoreService storeService;
    private final ItemEntryService itemEntryService;

    @Autowired
    public ItemOrderService(ItemOrderRepository itemOrderRepository, ItemService itemService, PersonService personService, StoreService storeService, ItemEntryService itemEntryService) {
        this.itemOrderRepository = itemOrderRepository;
        this.itemService = itemService;
        this.personService = personService;
        this.storeService = storeService;
        this.itemEntryService = itemEntryService;
    }


    public List<ItemOrder> findAllItemOrders() {
        return itemOrderRepository.findAll();
    }

    public Optional<ItemOrder> findItemOrderById(int id) {
        return itemOrderRepository.findById(id);
    }

    @Transactional
    public void saveItemOrder(ItemOrder itemOrder, HttpSession session) {
        Optional<Person> person = personService.findPersonById(itemOrder.getOwner().getId());

        List<ItemEntry> itemEntries = itemOrder.getItemEntries();
        for (ItemEntry itemEntry : itemEntries) {
            Optional<Item> foundedItem = itemService.findItemById(itemEntry.getOrderedItem().getId());
            itemEntry.setItemOrder(itemOrder);
            itemEntryService.saveItemEntry(itemEntry);
            foundedItem.get().setNumberOfItems(foundedItem.get().getNumberOfItems() - itemEntry.getNumberOfItems());
            foundedItem.get().getItemEntries().add(itemEntry);
        }

        person.get().getItemOders().add(itemOrder);

        itemOrder.setCreateAt(new Date(System.currentTimeMillis()));
        itemOrderRepository.save(itemOrder);

        storeService.clearStore(session);
    }

    @Transactional
    public void updateItemOrder(ItemOrder itemOrder) {
        Optional<ItemOrder> previousItemOrder = itemOrderRepository.findById(itemOrder.getId());

        List<ItemEntry> previousItemEntries = previousItemOrder.get().getItemEntries();
        for (ItemEntry previousItemEntry : previousItemEntries) {
            Optional<Item> previousItem = itemService.findItemById(previousItemEntry.getOrderedItem().getId());
            previousItem.get().setNumberOfItems(previousItem.get().getNumberOfItems() + previousItemEntry.getNumberOfItems());
            previousItem.get().getItemEntries().remove(previousItemEntry);
            itemEntryService.deleteItemEntry(previousItemEntry.getId());
        }


        List<ItemEntry> itemEntries = itemOrder.getItemEntries();
        for (ItemEntry itemEntry : itemEntries) {
            Optional<Item> foundedItem = itemService.findItemById(itemEntry.getOrderedItem().getId());
            itemEntry.setItemOrder(itemOrder);
            itemEntryService.saveItemEntry(itemEntry);
            foundedItem.get().setNumberOfItems(foundedItem.get().getNumberOfItems() - itemEntry.getNumberOfItems());
            foundedItem.get().getItemEntries().add(itemEntry);
        }

        itemOrder.setCreateAt(new Date(System.currentTimeMillis()));
        itemOrderRepository.save(itemOrder);
    }

    @Transactional
    public void deleteItemOrder(int id) {
        /*
        List<ItemEntry> itemEntries = itemOrderRepository.findById(id).get().getItemEntries();
        for (ItemEntry itemEntry : itemEntries) {
            itemEntryService.deleteItemEntry(itemEntry.getId());
        }
        */

        itemOrderRepository.deleteById(id);
    }
}
