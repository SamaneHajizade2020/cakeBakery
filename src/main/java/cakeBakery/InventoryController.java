package cakeBakery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> getInventory() {
        List<Inventory> repositoryAll = repository.findAll();
        for (Inventory Inventory : repositoryAll) {
            if(Inventory.getQuantity() == 0)
                repository.delete(Inventory);
        }
        return new ResponseEntity<>(repositoryAll, HttpStatus.OK);
    }

    @RequestMapping(value = "/createInventory", method = RequestMethod.POST)
    public ResponseEntity<Object> createInventoryWithoutLimit(@RequestBody ArrayList<Inventory> Inventorys) {
        for (Inventory Inventory : Inventorys) {
            if(Inventory.getQuantity() <= 0) {
                return  new ResponseEntity<>("Rejected cause of zero or negative quantity", HttpStatus.NOT_ACCEPTABLE);
            }
            repository.save(Inventory);
        }
        return new ResponseEntity<>("Product is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/inventory/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> replaceInventory(@RequestBody Inventory newInventory, @PathVariable Long id) {
        Inventory inventory = replaceInventoryEach(newInventory, id);
/*        return repository.findById(id)
                .map(Inventory -> {
                    Inventory.setName(newInventory.getName());
                    Inventory.setQuantity(newInventory.getQuantity());
                    return repository.save(Inventory);
                })
                .orElseGet(() -> {
                    newInventory.setId(id);
                    return repository.save(newInventory);
                });*/
        return new ResponseEntity<>("Product is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/inventory/fill", method = RequestMethod.POST)
    public ResponseEntity<Object> createInventory(@RequestBody ArrayList<Inventory> Inventorys) {
        for (Inventory Inventory : Inventorys) {
            if(Inventory.getQuantity() <= 0) {
                return  new ResponseEntity<>("Rejected cause of zero or negative quantity", HttpStatus.NOT_ACCEPTABLE);
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

    @RequestMapping(value = "/inventory/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        Optional<Inventory> InventoryId = repository.findById(Long.valueOf(id));
        if(InventoryId.isPresent())
        repository.deleteById(id);
        return new ResponseEntity<>("Product is deleted successsfully", HttpStatus.OK);
    }

    public void controlInventoryQuantity() {
        List<Inventory> newListOFInventorys = new ArrayList<>();
        List<Inventory> removableListOFDuplicateInventory = new ArrayList<>();
        List<Inventory> Inventorys = repository.findAll();

        List<Inventory> listDuplicateInventory = listDuplicateInventory(Inventorys);
        log.info("Size of Duplicate list:" + listDuplicateInventory.size());

        if (listDuplicateInventory.size() > 0) {
            for (Inventory inventory : listDuplicateInventory) {
                log.info("Inventory in duplicate List: " + inventory.getId() + inventory.getName() + inventory.getQuantity());

                Inventory newInventory = new Inventory(inventory.getName(), sumOfSameQuantity(listDuplicateInventory, inventory));
                log.info("newInventory: " + newInventory.getId() + newInventory.getName() + newInventory.getQuantity());

                Inventory newInventoryInTable = repository.save(new Inventory(newInventory.getName(), newInventory.getQuantity()));
                log.info("newInventoryInTable: " + newInventoryInTable.getId() + newInventoryInTable.getName() + newInventoryInTable.getQuantity());

                newListOFInventorys.add(newInventoryInTable);
                for (Inventory gred : newListOFInventorys) {
                    log.info("gred in newListOFInventorys: " + gred.getId() + gred.getName() + gred.getQuantity());
                }
                log.info("sizeOfNewListOFInventory: " + newListOFInventorys.size()
                        + " " + "Remove duplicate from new list: " + removeDuplicateInventory(newListOFInventorys));
                removableListOFDuplicateInventory.add(inventory);
            }

            for (Inventory Inventory : listDuplicateInventory) {
                log.info("Inventory that will be removed: " + Inventory.getId() + Inventory.getName() + Inventory.getQuantity());
                repository.deleteById(Inventory.getId());
                repository.delete(Inventory);
            }
        }
    }

    public List<Inventory> listDuplicateInventory(Collection<Inventory> Inventorys) {
        return Inventorys.stream()
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
                    log.info("It want to be removed: " + Inventory.getId() + Inventory.getName() + Inventory.getQuantity());

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

    private Inventory replaceInventoryEach(Inventory newInventory, Long id){
        return repository.findById(id)
                .map(Inventory -> {
                    Inventory.setName(newInventory.getName());
                    Inventory.setQuantity(newInventory.getQuantity());
                    return repository.save(Inventory);
                })
                .orElseGet(() -> {
                    newInventory.setId(id);
                    return repository.save(newInventory);
                });
    }
}

