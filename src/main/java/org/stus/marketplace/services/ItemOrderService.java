package org.stus.marketplace.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.ItemOrder;
import org.stus.marketplace.models.Person;
import org.stus.marketplace.repositories.ItemOrderRepository;
import org.stus.marketplace.repositories.ItemRepository;
import org.stus.marketplace.utils.itemOrder_utils.ItemOrderNotFoundException;
import org.stus.marketplace.utils.item_utils.ItemNotFoundException;
import org.stus.marketplace.utils.person_utils.PersonNotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ItemOrderService {
    private final ItemOrderRepository itemOrderRepository;
    private final ItemService itemService;
    private final PersonService personService;

    @Autowired
    public ItemOrderService(ItemOrderRepository itemOrderRepository, ItemService itemService, PersonService personService) {
        this.itemOrderRepository = itemOrderRepository;
        this.itemService = itemService;
        this.personService = personService;
    }


    public List<ItemOrder> findAllItemOrders() {
        return itemOrderRepository.findAll();
    }

    public Optional<ItemOrder> findItemOrderById(int id) {
        return itemOrderRepository.findById(id);
    }

    @Transactional
    public void saveItemOrder(ItemOrder itemOrder) throws PersonNotFoundException, ItemNotFoundException {
        Optional<Person> person = personService.findPersonByUserName(itemOrder.getOwner().getUsername());
        if (person.isEmpty()) {
            throw new PersonNotFoundException("Person not found");
        }

        Optional<Item> item = itemService.findItemByItemName(itemOrder.getOrderedItem().getItemName());
        if (item.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        person.get().getItemOders().add(itemOrder);
        item.get().getItemOrders().add(itemOrder);
        item.get().setNumberOfItems(item.get().getNumberOfItems()-1);

        itemOrder.setCreateAt(new Date(System.currentTimeMillis()));
        itemOrder.getOwner().setId(person.get().getId());
        itemOrder.getOrderedItem().setId(item.get().getId());
        itemOrderRepository.save(itemOrder);
    }

    @Transactional
    public void updateItemOrder(ItemOrder itemOrder) throws PersonNotFoundException, ItemNotFoundException {
        Optional<Person> person = personService.findPersonByUserName(itemOrder.getOwner().getUsername());
        if (person.isEmpty()) {
            throw new PersonNotFoundException("Person not found");
        }

        Optional<Item> item = itemService.findItemByItemName(itemOrder.getOrderedItem().getItemName());
        if (item.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        ItemOrder updatedItemOrder = itemOrderRepository.findById(itemOrder.getId()).get();     //check for null make in ItemOrderController
        updatedItemOrder.getOwner().getItemOders().remove(updatedItemOrder);
        updatedItemOrder.getOrderedItem().getItemOrders().remove(updatedItemOrder);
        updatedItemOrder.getOrderedItem().setNumberOfItems(updatedItemOrder.getOrderedItem().getNumberOfItems()+1);

        person.get().getItemOders().add(itemOrder);
        item.get().getItemOrders().add(itemOrder);
        item.get().setNumberOfItems(item.get().getNumberOfItems()-1);

        itemOrder.setCreateAt(new Date(System.currentTimeMillis()));
        itemOrder.getOwner().setId(person.get().getId());
        itemOrder.getOrderedItem().setId(item.get().getId());
        itemOrderRepository.save(itemOrder);
    }

    @Transactional
    public void deleteItemOrder(int id) {
        itemOrderRepository.deleteById(id);
    }
}
