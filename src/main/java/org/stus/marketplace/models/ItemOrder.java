package org.stus.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cascade;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "item_order")
public class ItemOrder {
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
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public List<ItemEntry> getItemEntries() {
        return itemEntries;
    }

    public void setItemEntries(List<ItemEntry> itemEntries) {
        this.itemEntries = itemEntries;
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
                '}';
    }
}
