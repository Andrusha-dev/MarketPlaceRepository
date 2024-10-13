package org.stus.marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stus.marketplace.models.ItemEntry;
import org.stus.marketplace.models.ItemOrder;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemEntryRepository extends JpaRepository<ItemEntry, Integer> {

}
