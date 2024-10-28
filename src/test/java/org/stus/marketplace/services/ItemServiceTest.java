package org.stus.marketplace.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.repositories.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    private ItemService itemService;
    private List<Item> items;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void createItemService() {
        this.itemService = new ItemService(itemRepository);

        items = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            Item item = this.itemFactory(i);
            item.setId(i);

            items.add(item);
        }
    }

    @Test
    public void itemServiceShouldReturnAllItems() {
        Mockito.doReturn(items).when(itemRepository).findAll();
        Assertions.assertEquals(items, itemService.findAllItems());
        Mockito.verify(itemRepository).findAll();
    }

    @Test
    public void itemServiceShouldFindItemById() {
        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);

            return Optional.ofNullable(this.findItemById(id));
        }).when(itemRepository).findById(Mockito.anyInt());

        Assertions.assertEquals(Optional.ofNullable(this.findItemById(5)), itemService.findItemById(5));
        Mockito.verify(itemRepository).findById(5);
    }

    @Test
    public void  itemServiceShouldFindItemByItemName() {
        Mockito.doAnswer(ans -> {
            String itemName = ans.getArgument(0);

            return Optional.ofNullable(this.findItemByItemName(itemName));
        }).when(itemRepository).findByItemName(Mockito.anyString());

        Assertions.assertEquals(Optional.ofNullable(this.findItemByItemName("Item5")), itemService.findItemByItemName("Item5"));
        Mockito.verify(itemRepository).findByItemName("Item5");
    }

    @Test
    public void itemServiceShouldSaveItem() {
        Mockito.doAnswer(ans -> {
            Item savedItem = ans.getArgument(0);
            int id = 0;
            for (Item item : items) {
                if (item.getId()>id)
                    id = item.getId();
            }

            id++;
            savedItem.setId(id);
            items.add(savedItem);
            return savedItem;
        }).when(itemRepository).save(Mockito.any(Item.class));

        Item savedItem = this.itemFactory(6);
        itemService.saveItem(savedItem);

        Item referencedItem = this.itemFactory(6);
        referencedItem.setId(6);

        Assertions.assertEquals(referencedItem, items.getLast());
        Mockito.verify(itemRepository).save(savedItem);
    }

    @Test
    public void itemServiceShouldUpdateItem() {
        Mockito.doAnswer(ans -> {
            Item updatedItem = ans.getArgument(0);
            items = items.stream()
                            .map(item -> item.getId() == updatedItem.getId() ? updatedItem : item)
                            .collect(Collectors.toList());

            return updatedItem;
        }).when(itemRepository).save(Mockito.any(Item.class));

        Item updatedItem = this.itemFactory(10);
        updatedItem.setId(5);
        itemService.saveItem(updatedItem);

        Item referencedItem = this.itemFactory(10);
        referencedItem.setId(5);

        Assertions.assertEquals(referencedItem, items.getLast());
        Mockito.verify(itemRepository).save(updatedItem);
    }

    @Test
    public void itemServiceShouldDeleteItem() {
        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            items = items.stream()
                    .filter(item -> item.getId() != id)
                    .collect(Collectors.toList());

            return 1;
        }).when(itemRepository).deleteById(Mockito.anyInt());

        itemService.deleteItem(5);

        Assertions.assertFalse(this.findIfPresent(5));
        Mockito.verify(itemRepository).deleteById(5);
    }


    private Item findItemById(int id) {
        Item foundedItem = null;
        for (Item item : items) {
            if (item.getId()==id)
                foundedItem = item;
        }

        return foundedItem;
    }

    private Item findItemByItemName(String itemName) {
        Item foundedItem = null;
        for (Item item : items) {
            if (item.getItemName().equals(itemName))
                foundedItem = item;
        }

        return foundedItem;
    }

    private Item itemFactory(int id) {
        Item item = new Item();
        item.setItemName("Item" + id);
        item.setNumberOfItems(30);
        item.setPrice(id * 100);
        item.setItemInfo("Item" + id + " is very good");
        item.setCategory("Category" + id);

        return item;
    }

    private boolean findIfPresent(int id) {
        for (Item item : items) {
            if (item.getId() == id)
               return true;
        }

        return false;
    }
}
