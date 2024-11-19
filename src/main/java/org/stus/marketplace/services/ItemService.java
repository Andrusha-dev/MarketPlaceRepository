package org.stus.marketplace.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.repositories.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private static final Logger logger = LogManager.getLogger(ItemService.class.getName());


    @Autowired
    public ItemService(ItemRepository itemRepository) {this.itemRepository = itemRepository;}


    public List<Item> findAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> findItemById(int id) {
        logger.debug("catch item id: " + id);

        return itemRepository.findById(id);
    }

    public Optional<Item> findItemByItemName(String itemName) {
        logger.debug("catch item name: " + itemName);

        return itemRepository.findByItemName(itemName);
    }

    @Transactional
    public void saveItem(Item item) {
        logger.debug("catch item with itenName: " + item.getItemName());
        itemRepository.save(item);
        logger.info("saving item");
    }

    @Transactional
    public void updateItem(Item item) {
        logger.debug("catch item with id: " + item.getId());
        itemRepository.save(item);
        logger.info("updating item with id: " + item.getId());
    }

    @Transactional
    public void deleteItem(int id) {
        logger.debug("catch item id: " + id);
        itemRepository.deleteById(id);
        logger.info("deleting item with id: " + id);
    }
}
