package cakeBakery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
public class InventoryController {
    private final static Logger log = Logger.getLogger(InventoryController.class.getName());

    @Autowired
    InventoryRepository repository;

    public InventoryController(InventoryRepository repository){this.repository = repository;};

    @RequestMapping(value = "/inventory", method = RequestMethod.GET)
    public ResponseEntity<Object> getInventoryList() {
        List<Inventory> repositoryAll = repository.findAll();
        for (Inventory Inventory : repositoryAll) {
            if(Inventory.getQuantity() == 0)
                repository.delete(Inventory);
        }
        return new ResponseEntity<>(repositoryAll, HttpStatus.OK);
    }

/*    @RequestMapping(value = "/createInventory", method = RequestMethod.POST)
    public ResponseEntity<Object> createInventoryWithoutLimit(@RequestBody ArrayList<Inventory> Inventorys) {
        for (Inventory Inventory : Inventorys) {
            if(Inventory.getQuantity() <= 0) {
                return  new ResponseEntity<>("Rejected cause of zero or negative quantity", HttpStatus.NOT_ACCEPTABLE);
            }
            repository.save(Inventory);
        }
        return new ResponseEntity<>("Product is created successfully", HttpStatus.CREATED);
    }*/

    @RequestMapping(value = "/inventory/fill", method = RequestMethod.POST)
    public ResponseEntity<Object> createInventory(@RequestBody ArrayList<Inventory> Inventorys) {
        for (Inventory Inventory : Inventorys) {
            if(Inventory.getQuantity() <= 0) {
                return  new ResponseEntity<>("Rejected cause of zero or negative quantity", HttpStatus.NO_CONTENT);
            }
        }
        for (Inventory Inventory : Inventorys) {
            repository.save(Inventory);
            log.info("Inventory that is added to Inventorys list: "  + Inventory.getId() + Inventory.getName() + Inventory.getQuantity());
        }
        controlInventoryQuantity();
        return new ResponseEntity<>("Product is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/inventory/deleteAll", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteAll() {
        List<Inventory> all = repository.findAll();
        for (Inventory Inventory : all) {
            repository.delete(Inventory);
        }
        return new ResponseEntity<>("Product is deleted successsfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/inventory/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteById(@PathVariable("id") Long id) {

        if(repository.findById(id) == null)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "404"
            );
        Optional<Inventory> inventory = repository.findById(id);
        repository.delete(inventory.get());

        return new ResponseEntity<>("Product is deleted successsfully", HttpStatus.OK);
    }

    public void controlInventoryQuantity() {
        ArrayList<Inventory> newListOFInventories = new ArrayList<>();
        List<Inventory> listDuplicateInventory = listDuplicateInventory(repository.findAll());
        log.info("Size of list with Duplicate name :" + listDuplicateInventory.size());
        if (listDuplicateInventory.size() > 0) {
            removeSameInventory(listDuplicateInventory, newListOFInventories);
        }
    }

    private void removeSameInventory(List<Inventory> listDuplicateInventory, ArrayList<Inventory> newListOFInventorys) {
        for (Inventory inventory : listDuplicateInventory) {
            log.info("Inventory in duplicate List: " + inventory.getId() + inventory.getName() + inventory.getQuantity());

            Inventory newInventoryInTable = repository.save(new Inventory(inventory.getName(), sumOfSameQuantity(listDuplicateInventory, inventory)));
            newListOFInventorys.add(newInventoryInTable);

            for (Inventory gred : newListOFInventorys) {
                log.info("Inventory in newListOFInventories: " + gred.getId() + gred.getName() + gred.getQuantity());
            }
            log.info("sizeOfNewListOFInventory: " + newListOFInventorys.size()
                    + " " + "Remove duplicate from new list: " + removeDuplicateInventory(newListOFInventorys));
            repository.deleteById(inventory.getId());
            repository.delete(inventory);
        }
    }

    public List<Inventory> listDuplicateInventory(Collection<Inventory> Inventories) {
        return Inventories.stream()
                .collect(Collectors.groupingBy(Inventory:: getName))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList());
    }

    public Integer sumOfSameQuantity(List<Inventory> Inventorys, Inventory Inventory){
        return Inventorys.stream()
                .filter(customer -> Inventory.getName().equals(customer.getName())).map(x -> x.getQuantity()).reduce(0, Integer::sum);
    }

    public boolean removeDuplicateInventory(List<Inventory> listInventory) {
        boolean flag=false;

        for (int i = 0; i < listInventory.size(); i++) {
            for (int j = 0; j < i; j++) {
                if ((listInventory.get(i).getName().equalsIgnoreCase(listInventory.get(j).getName())) &&
                        (listInventory.get(i).getQuantity().compareTo(listInventory.get(j).getQuantity())==0)
                        && (i!=j)){
                    Inventory Inventory = listInventory.get(i);

                    if(Inventory.getId() != null){
                        log.info("Inventory Id to be remover:" + Inventory.getId());
                        listInventory.remove(Inventory);
                        repository.delete(Inventory);
                        flag= true;
                    }
                    else
                        flag= false;
                }
            }
        }
        return flag;
    }
}

