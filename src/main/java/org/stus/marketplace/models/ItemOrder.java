package org.stus.marketplace.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
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

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @NotNull(message = "Some item should be chosen")
    private Item orderedItem;


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

    public Item getOrderedItem() {
        return orderedItem;
    }

    public void setOrderedItem(Item orderedItem) {
        this.orderedItem = orderedItem;
    }


    @Override
    public String toString() {
        return "ItemOrder{" +
                "id=" + id +
                ", createAt=" + createAt +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemOrder itemOrder = (ItemOrder) o;
        return id == itemOrder.id && Objects.equals(createAt, itemOrder.createAt) && Objects.equals(owner, itemOrder.owner) && Objects.equals(orderedItem, itemOrder.orderedItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createAt, owner, orderedItem);
    }
}
