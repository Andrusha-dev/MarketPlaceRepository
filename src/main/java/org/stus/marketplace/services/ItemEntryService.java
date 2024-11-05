package org.stus.marketplace.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stus.marketplace.dto.ItemEntryDTO;
import org.stus.marketplace.models.ItemEntry;
import org.stus.marketplace.models.ItemOrder;
import org.stus.marketplace.repositories.ItemEntryRepository;
import org.stus.marketplace.utils.ItemEntry_utils.ItemEntryNotFoundException;
import org.stus.marketplace.utils.item_utils.NumberOfItemsIsNotEnoughException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ItemEntryService {
    private final ItemEntryRepository itemEntryRepository;
    private static final Logger logger = LogManager.getLogger(ItemEntryRepository.class.getName());

    @Autowired
    public ItemEntryService(ItemEntryRepository itemEntryRepository) {
        this.itemEntryRepository = itemEntryRepository;
    }

    public List<ItemEntry> findAllItemEntries() {
        return itemEntryRepository.findAll();
    }

    public Optional<ItemEntry> findItemEntryById(int id) {
        logger.debug("catch itemEntry id: " + id);
        return itemEntryRepository.findById(id);
    }

    @Transactional
    public void saveItemEntry(ItemEntry itemEntry) {
        logger.debug("catch itemEntry with item id: " + itemEntry.getOrderedItem().getId());
        itemEntryRepository.save(itemEntry);
        logger.info("saving itemEntry with item id: " + itemEntry.getItemOrder().getId());
    }

    @Transactional
    public void deleteItemEntry(int id) {
        logger.debug("catch itemEntry id: " + id);
        itemEntryRepository.deleteById(id);
        logger.info("deleting itemEntry with id: " + id);
    }
}
