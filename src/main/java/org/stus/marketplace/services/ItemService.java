package org.stus.marketplace.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.repositories.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    public List<Item> findAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> findItemById(int id) {
        return itemRepository.findById(id);
    }

    public Optional<Item> findItemByItemName(String itemName) {
        return itemRepository.findByItemName(itemName);
    }

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(int id) {
        itemRepository.deleteById(id);
    }
}
