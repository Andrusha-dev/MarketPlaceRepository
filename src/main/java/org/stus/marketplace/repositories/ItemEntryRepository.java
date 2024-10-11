package org.stus.marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stus.marketplace.models.ItemEntry;

@Repository
public interface ItemEntryRepository extends JpaRepository<ItemEntry, Integer> {
}
