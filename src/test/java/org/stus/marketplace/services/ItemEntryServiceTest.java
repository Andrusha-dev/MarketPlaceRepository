package org.stus.marketplace.services;

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
import org.stus.marketplace.repositories.ItemEntryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class ItemEntryServiceTest {
    private ItemEntryService itemEntryService;
    private List<ItemEntry> itemEntries;

    @Mock
    private ItemEntryRepository itemEntryRepository;

    @BeforeEach
    public void createItemEntryService() {
        this.itemEntryService = new ItemEntryService(itemEntryRepository);

        this.itemEntries = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            ItemEntry itemEntry = this.itemEntryFactory(i);
            itemEntry.setId(i);
            itemEntries.add(itemEntry);
        }
    }

    @Test
    public void itemEntryServiceShouldFindAllItemEntries() {
        Mockito.doReturn(itemEntries).when(itemEntryRepository).findAll();
        Assertions.assertEquals(itemEntries, itemEntryService.findAllItemEntries());
        Mockito.verify(itemEntryRepository).findAll();
    }

    @Test
    public void itemEntryShouldFindItemEntryById() {
        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            return Optional.ofNullable(this.findItemEntryById(id));
        }).when(itemEntryRepository).findById(Mockito.anyInt());

        Assertions.assertEquals(Optional.ofNullable(this.findItemEntryById(5)), itemEntryService.findItemEntryById(5));
        Mockito.verify(itemEntryRepository).findById(5);
    }

    @Test
    public void ItemEntryServiceShouldSaveItemEntry() {
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
        }).when(itemEntryRepository).save(Mockito.any(ItemEntry.class));

        ItemEntry referencedItemEntry = this.itemEntryFactory(6);
        referencedItemEntry.setId(6);

        itemEntryService.saveItemEntry(this.itemEntryFactory(6));
        Assertions.assertEquals(referencedItemEntry, itemEntries.getLast());
    }

    @Test
    public void itemEntryServiceShouldDeleteItemEntry() {
        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            itemEntries = itemEntries.stream()
                    .filter(itemEntry -> itemEntry.getId() != id)
                    .collect(Collectors.toList());
            return true;
        }).when(itemEntryRepository).deleteById(Mockito.anyInt());

        itemEntryService.deleteItemEntry(5);

        Assertions.assertFalse(this.findIfPresent(5));
    }


    private ItemEntry itemEntryFactory(int id) {
        Item item = new Item();
        item.setId(id);

        ItemOrder itemOrder = new ItemOrder();
        itemOrder.setId(id);

        ItemEntry itemEntry = new ItemEntry();
        itemEntry.setNumberOfItems(id);
        itemEntry.setOrderedItem(item);
        itemEntry.setItemOrder(itemOrder);

        return itemEntry;
    }

    private ItemEntry findItemEntryById(int id) {
        ItemEntry foundedItemEntry = null;
        for (ItemEntry itemEntry : itemEntries) {
            if (itemEntry.getId() == id)
                foundedItemEntry = itemEntry;
        }

        return  foundedItemEntry;
    }

    private boolean findIfPresent(int id) {
        for (ItemEntry itemEntry : itemEntries) {
            if (itemEntry.getId() == id)
                return true;
        }

        return false;
    }

}
