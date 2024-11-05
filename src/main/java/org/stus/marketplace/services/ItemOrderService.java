package org.stus.marketplace.services;

import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(ItemOrderService.class.getName());

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
        logger.debug("catch itemOrder id: " + id);
        return itemOrderRepository.findById(id);
    }

    @Transactional
    public void saveItemOrder(ItemOrder itemOrder, HttpSession session) {
        logger.debug("catch itemOrder with owner id: " + itemOrder.getOwner().getId());
        logger.debug("catch httpSession with id: " + session.getId());
        Optional<Person> person = personService.findPersonById(itemOrder.getOwner().getId());

        List<ItemEntry> itemEntries = itemOrder.getItemEntries();
        for (ItemEntry itemEntry : itemEntries) {
            Optional<Item> foundedItem = itemService.findItemById(itemEntry.getOrderedItem().getId());
            itemEntry.setItemOrder(itemOrder);
            itemEntryService.saveItemEntry(itemEntry);
            foundedItem.get().setNumberOfItems(foundedItem.get().getNumberOfItems() - itemEntry.getNumberOfItems());
            logger.info("set numberOfItems: " + foundedItem.get().getNumberOfItems() + " in item with id: " + foundedItem.get().getId());
            foundedItem.get().getItemEntries().add(itemEntry);
            logger.info("add itemEntry to field 'itemEntries' in item with id: " + foundedItem.get().getId());
        }

        person.get().getItemOders().add(itemOrder);
        logger.info("add itemOrder to field 'itemOrders' in person with id: " + person.get().getId());

        itemOrder.setCreateAt(new Date(System.currentTimeMillis()));
        logger.info("set field 'setCreateAt' with current value: " + itemOrder.getCreateAt());
        itemOrderRepository.save(itemOrder);
        logger.info("saving itemOrder with owner id: " + person.get().getId());

        storeService.clearStore(session);
        logger.info("clearing store");
    }

    @Transactional
    public void updateItemOrder(ItemOrder itemOrder) {
        logger.debug("catch itemOrder with id: " + itemOrder.getId());
        Optional<ItemOrder> previousItemOrder = itemOrderRepository.findById(itemOrder.getId());

        List<ItemEntry> previousItemEntries = previousItemOrder.get().getItemEntries();
        for (ItemEntry previousItemEntry : previousItemEntries) {
            Optional<Item> previousItem = itemService.findItemById(previousItemEntry.getOrderedItem().getId());
            previousItem.get().setNumberOfItems(previousItem.get().getNumberOfItems() + previousItemEntry.getNumberOfItems());
            logger.info("set numberOfItems in previous item: " + previousItem.get().getNumberOfItems());
            previousItem.get().getItemEntries().remove(previousItemEntry);
            logger.info("remove previousItemEntry with id: " + previousItemEntry.getId() + " in previousItem with id: " + previousItem.get().getId());
            itemEntryService.deleteItemEntry(previousItemEntry.getId());
        }


        List<ItemEntry> itemEntries = itemOrder.getItemEntries();
        for (ItemEntry itemEntry : itemEntries) {
            Optional<Item> foundedItem = itemService.findItemById(itemEntry.getOrderedItem().getId());
            itemEntry.setItemOrder(itemOrder);
            logger.info("set itemOrder with id: " + itemOrder.getId() + " in itemEntry with item id: " + foundedItem.get().getId());
            itemEntryService.saveItemEntry(itemEntry);
            foundedItem.get().setNumberOfItems(foundedItem.get().getNumberOfItems() - itemEntry.getNumberOfItems());
            logger.info("set numberOfItems: " + foundedItem.get().getNumberOfItems() + " in item with id: " + foundedItem.get().getId());
            foundedItem.get().getItemEntries().add(itemEntry);
            logger.info("add itemEntry to field 'itemEntries' in item with id: " + foundedItem.get().getId());
        }

        itemOrder.setCreateAt(new Date(System.currentTimeMillis()));
        logger.info("set field 'setCreateAt' with current value: " + itemOrder.getCreateAt());
        itemOrderRepository.save(itemOrder);
        logger.info("saving itemOrder with id: " + itemOrder.getId());
    }

    @Transactional
    public void deleteItemOrder(int id) {
        logger.debug("catch itemOrder id: " + id);
        ItemOrder deletingItemOrder = itemOrderRepository.findById(id).get();

        int ownerId = deletingItemOrder.getOwner().getId();
        Person owner = personService.findPersonById(ownerId).get();
        owner.getItemOders().remove(deletingItemOrder);
        logger.info("remove itemOrder with id: " + id + " in person with id: " + ownerId);

        List<ItemEntry> itemEntries = deletingItemOrder.getItemEntries();
        for (ItemEntry itemEntry : itemEntries) {
            itemEntry.setItemOrder(null);
            logger.info("set 'null' in field 'itemOrder' in itemEntry with id: " + itemEntry.getId());
        }

        itemOrderRepository.deleteById(id);
        logger.info("deleting itemOrder with id: " + deletingItemOrder.getId());
    }
}
