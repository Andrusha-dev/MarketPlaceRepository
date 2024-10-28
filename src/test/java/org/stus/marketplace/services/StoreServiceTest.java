package org.stus.marketplace.services;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.ItemEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {
    private StoreService storeService;
    private List<ItemEntry> itemEntries;

    @Mock
    private ItemService itemService;

    @Mock
    private HttpSession httpSession;

    @BeforeEach
    public void createStoreService() {
        this.storeService = new StoreService(itemService);

        itemEntries = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            ItemEntry itemEntry = this.itemEntryFactory(i);

            itemEntries.add(itemEntry);
        }
    }

    @Test
    public void storeServiceShouldShowStore() {
        Mockito.doReturn(itemEntries).when(httpSession).getAttribute("store");

        Assertions.assertEquals(itemEntries, storeService.showStore(httpSession));
        Mockito.verify(httpSession).getAttribute("store");
    }

    @Test
    public void storeServiceShouldAddItem() {
        Mockito.doReturn(itemEntries).when(httpSession).getAttribute("store");
        Mockito.doAnswer(ans -> {
            itemEntries = ans.getArgument(1);

            return true;
        }).when(httpSession).setAttribute(Mockito.anyString(), Mockito.any(ArrayList.class));

        storeService.addItem(httpSession, this.itemEntryFactory(6));

        Assertions.assertEquals(this.itemEntryFactory(6), itemEntries.getLast());
        Mockito.verify(httpSession, Mockito.times(2)).getAttribute("store");
        Mockito.verify(httpSession, Mockito.atMost(2)).setAttribute(Mockito.anyString(), Mockito.any(ArrayList.class));
    }

    @Test
    public void storeServiceShouldUpdateStore() {
        Mockito.doAnswer(ans -> {
            itemEntries = ans.getArgument(1);
            return true;
        }).when(httpSession).setAttribute(Mockito.anyString(), Mockito.any(ArrayList.class));

        List<ItemEntry> updatedItemEntries = new ArrayList<>();
        for (int i = 11; i < 21; i++) {
            ItemEntry itemEntry = this.itemEntryFactory(i);

            updatedItemEntries.add(itemEntry);
        }

        storeService.updateStore(httpSession, updatedItemEntries);

        Assertions.assertEquals(updatedItemEntries, itemEntries);
        Mockito.verify(httpSession).setAttribute("store", updatedItemEntries);
    }

    @Test
    public void storeServiceShouldDeleteItem() {
        Mockito.doAnswer(ans -> {
            int id = ans.getArgument(0);
            Item item = null;
            for (ItemEntry itemEntry : itemEntries) {
                if (itemEntry.getOrderedItem().getId() == id)
                    item = itemEntry.getOrderedItem();
            }

            return Optional.ofNullable(item);
        }).when(itemService).findItemById(Mockito.anyInt());

        Mockito.doReturn(itemEntries).when(httpSession).getAttribute("store");

        Mockito.doAnswer(ans -> {
            itemEntries = ans.getArgument(1);
            return true;
        }).when(httpSession).setAttribute(Mockito.anyString(), Mockito.any(ArrayList.class));

        storeService.deleteItem(httpSession, 5);

        Assertions.assertFalse(this.findIfPresent(5));
        Mockito.verify(itemService).findItemById(5);
        Mockito.verify(httpSession).getAttribute("store");
        Mockito.verify(httpSession).setAttribute(Mockito.anyString(), Mockito.any(ArrayList.class));
    }

    @Test
    public void storeServiceShouldClearStore() {
        Mockito.doAnswer(ans -> {
            itemEntries = null;
            return true;
        }).when(httpSession).removeAttribute("store");

        storeService.clearStore(httpSession);
        Assertions.assertNull(itemEntries);
        Mockito.verify(httpSession).removeAttribute("store");
    }

    private ItemEntry itemEntryFactory(int id) {
        Item item = new Item();
        item.setId(id);

        ItemEntry itemEntry = new ItemEntry();
        itemEntry.setNumberOfItems(id);
        itemEntry.setOrderedItem(item);

        return itemEntry;
    }

    private boolean findIfPresent(int id) {
        for (ItemEntry itemEntry : itemEntries) {
            if (itemEntry.getOrderedItem().getId() == id)
                return true;
        }

        return false;
    }
}
