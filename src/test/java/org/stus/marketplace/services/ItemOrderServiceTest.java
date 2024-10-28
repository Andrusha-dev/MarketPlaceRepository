package org.stus.marketplace.services;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.ItemEntry;
import org.stus.marketplace.models.ItemOrder;
import org.stus.marketplace.models.Person;
import org.stus.marketplace.repositories.ItemOrderRepository;

import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class ItemOrderServiceTest {
    private ItemOrderService itemOrderService;
    private List<ItemOrder> itemOrders;
    private List<Person> persons;
    private List<ItemEntry> itemEntries;
    private List<ItemEntry> store;
    private List<Item> items;

    @Mock
    private ItemOrderRepository itemOrderRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private PersonService personService;

    @Mock
    private StoreService storeService;

    @Mock
    private ItemEntryService itemEntryService;

    @Mock
    private HttpSession httpSession;

    @BeforeEach
    public void createItemOrderService() {
        this.itemOrderService = new ItemOrderService(itemOrderRepository, itemService, personService, storeService, itemEntryService);

        this.persons = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            Person person = this.personFactory(i);
            persons.add(person);
        }

        this.itemEntries = new ArrayList<>();

        this.items = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            Item item = this.itemFactory(i);
            items.add(item);
        }

        this.store = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            ItemEntry itemEntry = this.itemEntryFactory(i);
            store.add(itemEntry);
        }

        this.itemOrders = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            ItemOrder itemOrder = this.itemOrderFactory(i);
            itemOrder.setId(i);
            itemOrders.add(itemOrder);
        }
    }


    @Test
    public void itemOrderServiceShouldFindAllItemOrders() {
        Mockito.doReturn(itemOrders).when(itemOrderRepository).findAll();

        for (ItemOrder itemOrder: itemOrders) {
            System.out.println(itemOrder);
        }

        for (Person person : persons) {
            System.out.println(person);
        }

        for (Item item : items) {
            System.out.println(item);
        }

        for (ItemEntry itemEntry : itemEntries) {
            System.out.println(itemEntry);
        }

        Assertions.assertEquals(itemOrders, itemOrderService.findAllItemOrders());
    }

    @Test
    public void itemOrderServiceShouldFindItemOrderById() {
        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            ItemOrder foundedItemOrder = null;
            for (ItemOrder itemOrder : itemOrders) {
                if (itemOrder.getId() == id) {
                    foundedItemOrder = itemOrder;
                }
            }

            return Optional.ofNullable(foundedItemOrder);
        }).when(itemOrderRepository).findById(Mockito.anyInt());

        Assertions.assertEquals(Optional.ofNullable(this.findItemOrderById(5)), itemOrderService.findItemOrderById(5));
    }

    @Test
    public void itemOrderServiceShouldSaveItemOrder() {
        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            Person foundedPerson = null;
            for (Person person : persons) {
                if (person.getId() == id)
                    foundedPerson = person;
            }

            return Optional.ofNullable(foundedPerson);
        }).when(personService).findPersonById(Mockito.anyInt());

        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            Item foundedItem = null;
            for (Item item : items) {
                if (item.getId() == id)
                    foundedItem = item;
            }

            return Optional.ofNullable(foundedItem);
        }).when(itemService).findItemById(Mockito.anyInt());

        Mockito.doAnswer(ans -> {
            ItemEntry savedItemEntry = ans.getArgument(0);
            int id = 0;
            for (ItemEntry itemEntry : itemEntries) {
                if (itemEntry.getId() > id)
                    id = itemEntry.getId();
            }

            id++;
            savedItemEntry.setId(id);
            itemEntries.add(savedItemEntry);
            return savedItemEntry;
        }).when(itemEntryService).saveItemEntry(Mockito.any(ItemEntry.class));

        Mockito.doAnswer(ans -> {
            ItemOrder savedItemOrder = ans.getArgument(0);
            int id = 0;
            for (ItemOrder itemOrder : itemOrders) {
                if (itemOrder.getId() > id)
                    id = itemOrder.getId();
            }

            id++;
            savedItemOrder.setId(id);
            itemOrders.add(savedItemOrder);
            return savedItemOrder;
        }).when(itemOrderRepository).save(Mockito.any(ItemOrder.class));

        Mockito.doAnswer(ans -> {
            store = null;
            return true;
        }).when(storeService).clearStore(Mockito.any(HttpSession.class));

        ItemOrder savedItemOrder = this.itemOrderFactory(6);
        itemOrderService.saveItemOrder(savedItemOrder, httpSession);

        ItemOrder referrencedItemOrder = this.itemOrderFactory(6);
        referrencedItemOrder.setId(6);

        Assertions.assertEquals(referrencedItemOrder, itemOrders.getLast());
        Assertions.assertEquals(referrencedItemOrder, this.findPersonById(5).getItemOders().getLast());

        int maxIndex = itemEntries.size()-1;
        for (int i = maxIndex-2; i < maxIndex; i++) {
            Assertions.assertEquals(referrencedItemOrder, itemEntries.get(i).getItemOrder());
        }

        Assertions.assertNull(store);
    }

    @Test
    public void itemOrderServiceShouldUpdateItemOrder() {
        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            ItemOrder itemOrder = this.findItemOrderById(id);
            return Optional.ofNullable(itemOrder);
        }).when(itemOrderRepository).findById(Mockito.anyInt());

        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            Item item = this.findItemById(id);
            return Optional.ofNullable(item);
        }).when(itemService).findItemById(Mockito.anyInt());

        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            itemEntries = itemEntries.stream()
                    .filter(itemEntry -> itemEntry.getId() != id)
                    .collect(Collectors.toList());

            return true;
        }).when(itemEntryService).deleteItemEntry(Mockito.anyInt());

        Mockito.doAnswer(ans -> {
            ItemEntry savedItemEntry = ans.getArgument(0);
            int id = 0;
            for (ItemEntry itemEntry : itemEntries) {
                if (itemEntry.getId() > id)
                    id = itemEntry.getId();
            }

            id++;
            savedItemEntry.setId(id);

            itemEntries.add(savedItemEntry);
            return savedItemEntry;
        }).when(itemEntryService).saveItemEntry(Mockito.any(ItemEntry.class));

        Mockito.doAnswer(ans -> {
            ItemOrder savedItemOrder = ans.getArgument(0);
            savedItemOrder.setOwner(this.findPersonById(1));
            itemOrders = itemOrders.stream()
                    .map(itemOrder -> itemOrder.getId()==savedItemOrder.getId() ? savedItemOrder : itemOrder)
                    .collect(Collectors.toList());

            return savedItemOrder;
        }).when(itemOrderRepository).save(Mockito.any(ItemOrder.class));

        ItemOrder updatedItemOrder = this.itemOrderFactory(5);
        updatedItemOrder.setId(1);
        itemOrderService.updateItemOrder(updatedItemOrder);

        ItemOrder referencedItemOrder = this.itemOrderFactory(5);
        referencedItemOrder.setId(1);

        Assertions.assertEquals(referencedItemOrder, this.findItemOrderById(1));
    }

    @Test
    public void itemOrderServiceShouldDeleteItemOrder() {
        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            itemOrders = itemOrders.stream()
                    .filter(itemOrder -> itemOrder.getId()!=id)
                    .collect(Collectors.toList());

            return true;
        }).when(itemOrderRepository).deleteById(Mockito.anyInt());


        itemOrderService.deleteItemOrder(5);

        Assertions.assertFalse(this.findItemOrderIfPresent(5));
    }


    private ItemOrder itemOrderFactory(int id) {
        ItemOrder itemOrder = new ItemOrder();

        int idForOwnerAndItem = id;
        if (idForOwnerAndItem > 5)
            idForOwnerAndItem = 5;

        Person owner = this.findPersonById(idForOwnerAndItem);
        if (owner.getItemOders() == null) {
            owner.setItemOders(new ArrayList<>(Arrays.asList(itemOrder)));
        }  else {
            owner.getItemOders().add(itemOrder);
        }

        List<ItemEntry> itemEntriesInOrder = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            int itemEntryId = 0;
            if (!itemEntries.isEmpty()) {
                for (ItemEntry itemEntry : itemEntries) {
                    if (itemEntry.getId() > itemEntryId)
                        itemEntryId = itemEntry.getId();
                }

            }

            itemEntryId++;

            ItemEntry itemEntry = this.itemEntryFactory(i);
            itemEntry.setId(itemEntryId);

            Item orderedItem = this.findItemById(idForOwnerAndItem);
            if (orderedItem.getItemEntries() == null) {
                orderedItem.setItemEntries(new ArrayList<>(Arrays.asList(itemEntry)));
            } else {
                orderedItem.getItemEntries().add(itemEntry);
            }

            itemEntry.setItemOrder(itemOrder);
            itemEntriesInOrder.add(itemEntry);
            itemEntries.add(itemEntry);
        }

        itemOrder.setCreateAt(new Date(System.currentTimeMillis()));
        itemOrder.setOwner(owner);
        itemOrder.setItemEntries(itemEntriesInOrder);

        return itemOrder;
    }

    private Person personFactory(int id) {
        Person person = new Person();
        person.setId(id);

        return person;
    }

    private ItemEntry itemEntryFactory(int id) {
        ItemEntry itemEntry = new ItemEntry();
        itemEntry.setNumberOfItems(id);
        itemEntry.setOrderedItem(this.findItemById(id));

        return itemEntry;
    }

    private Item itemFactory(int id) {
        Item item = new Item();
        item.setId(id);
        item.setNumberOfItems(id * 10);

        return item;
    }


    private ItemOrder findItemOrderById(int id) {
        ItemOrder foundedItemOrder = null;
        for (ItemOrder itemOrder : itemOrders) {
            if (itemOrder.getId() == id)
                foundedItemOrder = itemOrder;
        }

        return foundedItemOrder;
    }

    private Person findPersonById(int id) {
        Person foundedPerson = null;
        for (Person person : persons) {
            if (person.getId() == id)
                foundedPerson = person;
        }

        return foundedPerson;
    }

    private ItemEntry findItemEntryById(int id) {
        ItemEntry foundedItemEntry = null;
        for (ItemEntry itemEntry : itemEntries) {
            if (itemEntry.getId() == id)
                foundedItemEntry = itemEntry;
        }

        return foundedItemEntry;
    }

    private Item findItemById(int id) {
        Item foundedItem = null;
        for (Item item : items) {
            if (item.getId() == id )
                foundedItem = item;
        }

        return foundedItem;
    }

    private boolean findItemOrderIfPresent(int id) {
        for (ItemOrder itemOrder : itemOrders) {
            if (itemOrder.getId() == id)
                return true;
        }

        return false;
    }
}
