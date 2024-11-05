package org.stus.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Cascade;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "item_order")
public class ItemOrder {
    private static final Logger logger = LogManager.getLogger(ItemOrder.class.getName());

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "create_at")
    private Date createAt;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    @NotNull(message = "Owner should be defined")
    private Person owner;

    @OneToMany(mappedBy = "itemOrder")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @NotNull(message = "Some items (item entries) should be chosen")
    private List<ItemEntry> itemEntries;


    public ItemOrder() {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        logger.debug("catch itemOrder id:" + id);
        this.id = id;
        logger.info("set id: " + id + " in itemOrder");
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        logger.debug("catch itemOrder createAt: " + createAt);
        this.createAt = createAt;
        logger.info("set createAt: " + createAt + " in itemOrder");
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        logger.debug("catch itemOrder owner with id: " + owner.getId());
        this.owner = owner;
        logger.info("set owner with id: " + owner.getId() + " in itemOrder");
    }

    public List<ItemEntry> getItemEntries() {
        return itemEntries;
    }

    public void setItemEntries(List<ItemEntry> itemEntries) {
        logger.debug("catch itemOrder itemEntries with size: " + itemEntries.size());
        this.itemEntries = itemEntries;
        logger.info("set itemEntries with size: " + itemEntries.size() + " in itemOrder");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemOrder itemOrder = (ItemOrder) o;
        return id == itemOrder.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ItemOrder{" +
                "id=" + id +
                ", createAt=" + createAt +
                ", owner{id=" + owner.getId() + "}" +
                ", itemEntries=" + itemEntries +
                '}';
    }
}
