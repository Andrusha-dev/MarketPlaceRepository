package org.stus.marketplace.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.stus.marketplace.dto.ItemDTO;
import org.stus.marketplace.dto.ItemEntryDTO;
import org.stus.marketplace.dto.ItemOrderDTO;
import org.stus.marketplace.dto.PersonDTO;
import org.stus.marketplace.models.Item;
import org.stus.marketplace.models.ItemEntry;
import org.stus.marketplace.models.ItemOrder;
import org.stus.marketplace.models.Person;
import org.stus.marketplace.security.PersonDetails;
import org.stus.marketplace.services.ItemEntryService;
import org.stus.marketplace.services.ItemOrderService;
import org.stus.marketplace.services.ItemService;
import org.stus.marketplace.services.PersonService;
import org.stus.marketplace.utils.ItemEntry_utils.ItemEntryErrorResponse;
import org.stus.marketplace.utils.ItemEntry_utils.ItemEntryNotFoundException;
import org.stus.marketplace.utils.itemOrder_utils.*;
import org.stus.marketplace.utils.item_utils.ItemErrorResponse;
import org.stus.marketplace.utils.item_utils.ItemNotFoundException;
import org.stus.marketplace.utils.item_utils.NumberOfItemsIsNotEnoughException;
import org.stus.marketplace.utils.person_utils.PersonErrorResponse;
import org.stus.marketplace.utils.person_utils.PersonNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itemOrder")
public class ItemOrderController {
    private final ItemOrderService itemOrderService;
    private final ModelMapper modelMapper;
    private final PersonService personService;
    private final ItemService itemService;
    private final ItemEntryService itemEntryService;
    private static final Logger logger = LogManager.getLogger(ItemOrderController.class.getName());

    @Autowired
    public ItemOrderController(ItemOrderService itemOrderService, ModelMapper modelMapper, PersonService personService, ItemService itemService, ItemEntryService itemEntryService) {
        this.itemOrderService = itemOrderService;
        this.modelMapper = modelMapper;
        this.personService = personService;
        this.itemService = itemService;
        this.itemEntryService = itemEntryService;
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> createItemOrder(@RequestBody @Valid ItemOrderDTO itemOrderDTO,
                                                      BindingResult bindingResult,
                                                      HttpServletRequest request) {
        logger.debug("catch ItemOrderDTO with ownerDTO id: " + itemOrderDTO.getOwnerDTO().getId());
        logger.debug("catch BindingResult: " + bindingResult.getClass().getName());
        logger.debug("catch HttpServletRequest with session id: " + request.getSession().getId());

        HttpSession session = request.getSession();

        Optional<Person> person = personService.findPersonById(itemOrderDTO.getOwnerDTO().getId());
        if (person.isEmpty()) {
            throw new PersonNotFoundException("Person not found");
        }

        List<ItemEntryDTO> itemEntriesDTO = itemOrderDTO.getItemEntriesDTO();
        for (ItemEntryDTO itemEntryDTO : itemEntriesDTO) {
            Optional<Item> foundedItem = itemService.findItemById(itemEntryDTO.getOrderedItemDTO().getId());
            if (foundedItem.isEmpty()) {
                throw new ItemNotFoundException("Item not found");
            }

            if ((foundedItem.get().getNumberOfItems() - itemEntryDTO.getNumberOfItems()) < 0) {
                throw new NumberOfItemsIsNotEnoughException("Number of items is not enough");
            }
        }

        if (bindingResult.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                builder.append(error.getField() + " : " + error.getDefaultMessage() + ";");
            }

            throw new ItemOrderNotCreateException(builder.toString());
        }
        logger.info("validation of ItemOrderDTO complete successfully");

        itemOrderService.saveItemOrder(convertToItemOrder(itemOrderDTO), session);
        logger.info("saving ItemOrder with owner id: " + itemOrderDTO.getOwnerDTO().getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping()
    public List<ItemOrderDTO> getAllItemOrders() {
        return itemOrderService.findAllItemOrders().stream()
                .map(i -> convertToItemOrderDTO(i))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemOrderDTO getItemOrderById(@PathVariable("id") int id) {
        logger.debug("catch ItemOrder id: " + id);

        Optional<ItemOrder> foundedItemOrder = itemOrderService.findItemOrderById(id);
        if (foundedItemOrder.isEmpty()) {
            throw new ItemOrderNotFoundException("Item order not found");
        }
        logger.info("get itemOrder with id: " + id);

        return convertToItemOrderDTO(foundedItemOrder.get());
    }


    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updateItemOrder(@PathVariable("id") int id,
                                                      @RequestBody @Valid ItemOrderDTO itemOrderDTO, BindingResult bindingResult) {
        logger.debug("catch ItemOrder id: " + id);
        logger.debug("catch ItemOrderDTO with ownerDTO id: " + itemOrderDTO.getOwnerDTO().getId());
        logger.debug("catch BindingResult: " + bindingResult.getClass().getName());

        Optional<ItemOrder> foundedItemOrder = itemOrderService.findItemOrderById(id);
        if (foundedItemOrder.isEmpty()) {
            throw new ItemOrderNotFoundException("Item order not found");
        }

        Optional<Person> person = personService.findPersonById(itemOrderDTO.getOwnerDTO().getId());
        if (person.isEmpty()) {
            throw new PersonNotFoundException("Person not found");
        }

        if (person.get().getId() != foundedItemOrder.get().getOwner().getId()) {
            throw new DifferentOwnersException("Owner in updated item order are differs than owner in current item order");
        }

        List<ItemEntryDTO> itemEntriesDTO = itemOrderDTO.getItemEntriesDTO();
        for (ItemEntryDTO itemEntryDTO : itemEntriesDTO) {
            Optional<Item> foundedItem = itemService.findItemById(itemEntryDTO.getOrderedItemDTO().getId());
            if (foundedItem.isEmpty()) {
                throw new ItemNotFoundException("Item not found");
            }

            if ((foundedItem.get().getNumberOfItems() - itemEntryDTO.getNumberOfItems()) < 0) {
                throw new NumberOfItemsIsNotEnoughException("Number of items is not enough");
            }
        }


        ItemOrder updatedItemOrder = convertToItemOrder(itemOrderDTO);
        logger.info("converting ItemOrderDTO to ItemOrder with owner id: " + updatedItemOrder.getOwner().getId());
        updatedItemOrder.setId(id);
        logger.info("set id: " + id + " in updated ItemOrder");

        if (bindingResult.hasErrors()) {
            StringBuilder builder = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                builder.append(error.getField() + " : " + error.getDefaultMessage() + ";");
            }

            throw new ItemOrderNotUpdateException(builder.toString());
        }
        logger.info("validation of updated ItemOrder with id: " + updatedItemOrder.getId() + " complete successfully");

        itemOrderService.updateItemOrder(updatedItemOrder);
        logger.info("updating ItemOrder with id: " + updatedItemOrder.getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteItemOrder(@PathVariable("id") int id) {
        logger.debug("catch ItemOrder id: " + id);

        Optional<ItemOrder> deletedItemOrder = itemOrderService.findItemOrderById(id);
        if (deletedItemOrder.isEmpty()) {
            throw  new ItemOrderNotFoundException("Item order not found");
        }
        logger.info("get ItemOrder with id: " + deletedItemOrder.get().getId());

        itemOrderService.deleteItemOrder(id);
        logger.info("deleting ItemOrder with id: " + deletedItemOrder.get().getId());
        return ResponseEntity.ok(HttpStatus.OK);
    }


    private ItemOrder convertToItemOrder(ItemOrderDTO itemOrderDTO) {
        logger.debug("catch ItemOrderDTO with ownerDTO id: " + itemOrderDTO.getOwnerDTO().getId());

        ItemOrder itemOrder = new ItemOrder();

        Optional<Person> person = personService.findPersonById(itemOrderDTO.getOwnerDTO().getId());

        itemOrder.setOwner(person.get());
        List<ItemEntryDTO> itemEntriesDTO = itemOrderDTO.getItemEntriesDTO();
        List<ItemEntry> itemEntries = itemEntriesDTO.stream()
                .map(itemEntryDTO -> {
                    ItemEntry itemEntry = new ItemEntry();
                    itemEntry.setNumberOfItems(itemEntryDTO.getNumberOfItems());
                    itemEntry.setOrderedItem(itemService.findItemById(itemEntryDTO.getOrderedItemDTO().getId()).get());
                    return itemEntry;
                })
                .collect(Collectors.toList());
        itemOrder.setItemEntries(itemEntries);
        logger.info("converting ItemOrderDTO to ItemOrder with owner id: " + itemOrder.getOwner().getId());

        return itemOrder;
    }

    private ItemOrderDTO convertToItemOrderDTO(ItemOrder itemOrder) {
        logger.debug("catch ItemOrder with owner id: " + itemOrder.getOwner().getId());

        ItemOrderDTO itemOrderDTO = new ItemOrderDTO();
        itemOrderDTO.setId(itemOrder.getId());
        itemOrderDTO.setOwnerDTO(modelMapper.map(itemOrder.getOwner(), PersonDTO.class));
        List<ItemEntry> itemEntries = itemOrder.getItemEntries();
        List<ItemEntryDTO> itemEntriesDTO = itemEntries.stream()
                .map(itemEntry -> {
                    ItemEntryDTO itemEntryDTO = new ItemEntryDTO();
                    itemEntryDTO.setId(itemEntry.getId());
                    itemEntryDTO.setNumberOfItems(itemEntry.getNumberOfItems());
                    Item item = itemService.findItemById(itemEntry.getOrderedItem().getId()).get();
                    itemEntryDTO.setOrderedItemDTO(modelMapper.map(itemEntry.getOrderedItem(), ItemDTO.class));
                    return itemEntryDTO;
                })
                .collect(Collectors.toList());
        itemOrderDTO.setItemEntriesDTO(itemEntriesDTO);
        logger.info("converting ItemOrder to ItemOrderDTO with ownerDTO id: " + itemOrderDTO.getOwnerDTO().getId());

        return itemOrderDTO;
    }


    @ExceptionHandler
    private ResponseEntity<ItemOrderErrorResponse> handleException(ItemOrderNotCreateException exc) {
        logger.error("itemOrder not create", exc);
        ItemOrderErrorResponse itemOrderErrorResponse = new ItemOrderErrorResponse(exc.getMessage(), System.currentTimeMillis());

        return new ResponseEntity<ItemOrderErrorResponse>(itemOrderErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ItemOrderErrorResponse> handleException(ItemOrderNotFoundException exc) {
        logger.error("itemOrder not found", exc);
        ItemOrderErrorResponse itemOrderErrorResponse = new ItemOrderErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemOrderErrorResponse>(itemOrderErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ItemOrderErrorResponse> handleException(ItemOrderNotUpdateException exc) {
        logger.error("itemOrder not update", exc);
        ItemOrderErrorResponse itemOrderErrorResponse = new ItemOrderErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemOrderErrorResponse>(itemOrderErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ItemOrderErrorResponse> handleException(DifferentOwnersException exc) {
        logger.error("different owners", exc);
        ItemOrderErrorResponse itemOrderErrorResponse = new ItemOrderErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemOrderErrorResponse>(itemOrderErrorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException exc) {
        logger.error("person not found", exc);
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<PersonErrorResponse>(personErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(ItemNotFoundException exc) {
        logger.error("item not found", exc);
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ItemErrorResponse> handleException(NumberOfItemsIsNotEnoughException exc) {
        logger.error("number of items is not enough");
        ItemErrorResponse itemErrorResponse = new ItemErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemErrorResponse>(itemErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ItemEntryErrorResponse> handleException(ItemEntryNotFoundException exc) {
        logger.error("itemEntry not found", exc);
        ItemEntryErrorResponse itemEntryErrorResponse = new ItemEntryErrorResponse(exc.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<ItemEntryErrorResponse>(itemEntryErrorResponse, HttpStatus.NOT_FOUND);
    }
}
