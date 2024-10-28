package org.stus.marketplace.services;

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

    @Autowired
    public ItemEntryService(ItemEntryRepository itemEntryRepository) {
        this.itemEntryRepository = itemEntryRepository;
    }

    public List<ItemEntry> findAllItemEntries() {
        return itemEntryRepository.findAll();
    }

    public Optional<ItemEntry> findItemEntryById(int id) {
        return itemEntryRepository.findById(id);
    }

    @Transactional
    public void saveItemEntry(ItemEntry itemEntry) throws NumberOfItemsIsNotEnoughException {
        itemEntryRepository.save(itemEntry);
    }

    @Transactional
    public void deleteItemEntry(int id) {
        itemEntryRepository.deleteById(id);
    }
}
