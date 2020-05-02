package cakeBakery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
public class RecipeController {
    private final static Logger log = Logger.getLogger(RecipeController.class.getName());

    @Autowired(required=true)
    RecipeRepository recipeRepository;

    @Autowired(required=true)
    IngredientRepository ingredientRepository;

    @Autowired(required=true)
    InventoryRepository inventoryRepository;

    @Autowired(required=true)
    ResultRepository resultRepository;

    @Autowired(required = true)
    ResultOptimiseRepository resultOptimiseRepo;

    public RecipeController() {
    }

    public RecipeController(RecipeRepository recipeRepository, IngredientRepository ingredientRepository, InventoryRepository inventoryRepository, ResultOptimiseRepository resultOptimiseRepo) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.inventoryRepository = inventoryRepository;
        this.resultOptimiseRepo= resultOptimiseRepo;
    }

    @RequestMapping(value = "/recipes")
    public ResponseEntity<Object> getProduct() {
        return new ResponseEntity<>(recipeRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/recipe/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getProductById(@PathVariable("id") Long id) {
        //if(recipeRepository.findById(id) == null)
        if(recipeRepository.findById(id).isPresent() == false)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "404"
            );
        return new ResponseEntity<>(recipeRepository.findById(id), HttpStatus.OK);
    }

  @RequestMapping(value = "/products/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateProduct(@PathVariable("id") Long id, @RequestBody Recipe newRecipe) {
        log.info("newRecipe.getRecipeId: "+ newRecipe.getRecipeId());
        if(newRecipe.getRecipeId() == null) {
            updaterRecipe(id,newRecipe);
        }else {
            throw new ResponseStatusException( HttpStatus.NOT_ACCEPTABLE, "It is impossible to set id to recipe.");
        }
       return new ResponseEntity<>("Product is updated successfully", HttpStatus.OK);
    }

    public Recipe updaterRecipe(Long id, Recipe newRecipe){
        return recipeRepository.findById(id)
                .map(recipe -> {
                    recipe.setInstructions(newRecipe.getInstructions());
                    recipe.setName(newRecipe.getName());
                    log.info("newRecipe.getIngredients(): " + newRecipe.getIngredients().size());
                    recipe.setIngredients(newRecipe.getIngredients());
                    return recipeRepository.save(recipe);
                })
                .orElseGet(() -> {
                    newRecipe.setRecipeId(id);
                    return recipeRepository.save(newRecipe);
                });
    }

    @RequestMapping(value = "/recipes/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {

        if(recipeRepository.findById(id) == null)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "404"
            );
        Optional<Recipe> recipe = recipeRepository.findById(id);
        log.info("recipe:" + recipe.get().getRecipeId());
        recipeRepository.delete(recipe.get());

        List<Ingredient> ingredients = recipe.get().getIngredients();
        for (Ingredient ingredient : ingredients) {
            ingredientRepository.delete(ingredient);
        }

        return new ResponseEntity<>("Product is deleted successsfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/recipes/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createRecipe(@RequestBody Recipe recipe) {
        List<Ingredient> ingredients = recipe.getIngredients();
        for (Ingredient ingredient : ingredients) {
            ingredientRepository.save(ingredient);
        }
        recipeRepository.save(recipe);
        return new ResponseEntity<>("Product is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/recipes/create/all", method = RequestMethod.POST)
    public ResponseEntity<Object> createRecipis(@RequestBody List<Recipe> recipeis) {
        for (Recipe recipe : recipeis) {

        List<Ingredient> ingredients = recipe.getIngredients();
        for (Ingredient ingredient : ingredients) {
            ingredientRepository.save(ingredient);
        }
        recipeRepository.save(recipe);

        }
        return new ResponseEntity<>("Product is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/recipe/{id}" , method = RequestMethod.PATCH)
    public ResponseEntity<Object> partialUpdateName(@RequestBody Recipe partialUpdate, @PathVariable("id") Long id) {
        if(recipeRepository.findById(id).isPresent() == false)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "404"
            );

        if((partialUpdate.getRecipeId() != null))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, " Please note that it shall not be allowed to change the id-property of a recipe."
            );

        Recipe recipe1 = recipeRepository.findById(id).get();
        if(partialUpdate.getName()!=null)
        recipe1.setName(partialUpdate.getName());

        if(partialUpdate.getInstructions() != null)
            recipe1.setInstructions(partialUpdate.getInstructions());

        if(partialUpdate.getIngredients() != null)
            recipe1.setIngredients(partialUpdate.getIngredients());

        recipe1.setRecipeId(id);
        recipeRepository.save(recipe1);

        return new ResponseEntity<Object>("Product is updated successsfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/recipes/{id}/make", method = RequestMethod.POST)
    public ResponseEntity<Object> createRecipeYummy(@RequestBody Recipe recipe, @PathVariable("id") Long id) {
        try{
            List<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : ingredients) {
                ingredientRepository.save(ingredient);
            }
            recipeRepository.save(recipe);
        }catch (ResponseStatusException e){
            new ResponseEntity<>("403", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Yummy", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/recipes/get-count-by-recipe", method = RequestMethod.GET)
    public ResponseEntity<Object> getCountByRecipe() {
       getCountByRecipe(recipeRepository.findAll(), inventoryRepository.findAll());
        return new ResponseEntity<>(resultRepository.findAll(), HttpStatus.CREATED);
    }


    @RequestMapping(value = "/recipes/optimize-total-count", method = RequestMethod.GET)
    public ResponseEntity<Object> getRecipesOptimizeTotalCount() {
        ResultOptimise optimiseCountByRecipe = getOptimiseCountByRecipe_(recipeRepository.findAll(), inventoryRepository.findAll());
        return new ResponseEntity<>(resultOptimiseRepo.findAll(), HttpStatus.CREATED);
    }

   public void getCountByRecipe(Collection<Recipe> recipes, Collection<Inventory> inventories) {
       for (Recipe recipe : recipes) {
           ArrayList<GradientInventory> resultIngredientInventory= new ArrayList<>();
           log.info( recipe.getRecipeId() + " " + recipe.getName() + recipe.getInstructions() + " " + recipe.getIngredients().size());

           List<Ingredient> ingredientsOfRecipe = recipe.getIngredients();
           for (Ingredient ingredient : ingredientsOfRecipe) {
               log.info( " " + ingredient.getName() + " " + ingredient.getQuantity());
           }

           List<Inventory> ingredientOfInventoriesWhichAreInThisRecipe = inventories.stream()
                   .filter(os -> ingredientsOfRecipe.stream()                    // filter
                           .anyMatch(ns ->                                  // compare both
                                   os.getName().equals(ns.getName())))
                   .collect(Collectors.toList());

           log.info("Inventory which are used in this recipe:" + ingredientOfInventoriesWhichAreInThisRecipe.size());
           for (Inventory ingredient : ingredientOfInventoriesWhichAreInThisRecipe) {
               log.info( " " + ingredient.getName() + " " + ingredient.getQuantity());
           }

           for (Ingredient ingredient : ingredientsOfRecipe) {
               resultIngredientInventory.add(new GradientInventory(ingredient.getId(), ingredient.getName(), ingredient.getQuantity()));
           }

           for (Inventory ingredient : ingredientOfInventoriesWhichAreInThisRecipe) {
               resultIngredientInventory.add(new GradientInventory(ingredient.getId(), ingredient.getName(), ingredient.getQuantity()));
           }

           log.info("resultArr:" + resultIngredientInventory.size());
           for (GradientInventory gradientInventory : resultIngredientInventory) {
               log.info( " " + gradientInventory.getName() + " " + gradientInventory.getQuantity());
           }

           List<Ingredient> list = divOfQuantityForSameIngredientAndInventory(resultIngredientInventory);
           for (Ingredient ingredient : list) {
               log.info( " " + ingredient.getName() + " " + ingredient.getQuantity());
           }

           Integer quantity =  findMinInList(list);
           log.info("min by Comparator" + quantity);

           resultRepository.saveAndFlush(new Result(recipe.getRecipeId(), String.valueOf(quantity)));
       }

   }

    public ResultOptimise getOptimiseCountByRecipe_(List<Recipe> recipes, List<Inventory> inventories) {
        System.out.printf("size of recipe" + recipes.size() + " ");

        List<Integer> integers = resultForEachRecipe(recipes, inventories);

        Integer unusedInventoryCount = integers.stream()
                .reduce(0, (a, b) -> a + b);
        log.info("unusedInventoryCount: " + unusedInventoryCount);

        List<Result> result = resultRepository.findAll().stream().collect(Collectors.toList());
        Integer recipeCount = result.stream()
                .map(x -> Integer.valueOf(x.getCount())).reduce(0, Integer::sum);

        log.info("sumOfRecipe.toString: " + recipeCount.toString());
        return resultOptimiseRepo.save(new ResultOptimise(result, recipeCount.toString(), String.valueOf(unusedInventoryCount)));
    }

    private Integer findMinInList(List<Ingredient> list) {
        return list
                .stream()
                .min(Comparator.comparing(Ingredient::getQuantity))
                .get().getQuantity();
    }

    public List<Ingredient> divOfQuantityForSameIngredient(List<Ingredient> listIngredient) {
        ArrayList<Ingredient> result= new ArrayList<>();
        for (int i = 0; i < listIngredient.size(); i++) {
            for (int j = 0; j < i; j++) {
                if ((listIngredient.get(i).getName().equalsIgnoreCase(listIngredient.get(j).getName()) && ( i !=j)
                        && (listIngredient.get(i).getQuantity()!= 0) && (listIngredient.get(j).getQuantity()!= 0)))  {
                    int divOfQuantity = ((listIngredient.get(i).getQuantity()) / (listIngredient.get(j).getQuantity()));
                    // result.add(new Ingredient(String.valueOf(new Random().nextInt()),listIngredient.get(i).getName(), divOfQuantity));
                    result.add(new Ingredient(listIngredient.get(i).getName(), divOfQuantity));
                }
            }
        }

        return result;
    }

    public List<GradientInventory> divOfQuantityForSameInventory(List<GradientInventory> listIngredient) {
        //ArrayList<Ingredient> result= new ArrayList<>();
        ArrayList<GradientInventory> gradientInventories= new ArrayList<>();
        for (int i = 0; i < listIngredient.size(); i++) {
            for (int j = 0; j < i; j++) {
                if ((listIngredient.get(i).getName().equalsIgnoreCase(listIngredient.get(j).getName()) && ( i !=j)
                        && (listIngredient.get(i).getQuantity()!= 0) && (listIngredient.get(j).getQuantity()!= 0)))  {
                    int divOfQuantity = ((listIngredient.get(i).getQuantity()) / (listIngredient.get(j).getQuantity()));
                    // result.add(new Ingredient(String.valueOf(new Random().nextInt()),listIngredient.get(i).getName(), divOfQuantity));
                    gradientInventories.add(new GradientInventory(listIngredient.get(i).getName(), divOfQuantity));
                }
            }
        }

        return gradientInventories;
    }

    public List<Ingredient> divOfQuantityForSameIngredientAndInventory(List<GradientInventory> listIngredient) {
        ArrayList<Ingredient> result= new ArrayList<>();
        for (int i = 0; i < listIngredient.size(); i++) {
            for (int j = 0; j < i; j++) {
                if ((listIngredient.get(i).getName().equalsIgnoreCase(listIngredient.get(j).getName()) && ( i !=j)
                        && (listIngredient.get(i).getQuantity()!= 0) && (listIngredient.get(j).getQuantity()!= 0)))  {
                    int divOfQuantity = ((listIngredient.get(i).getQuantity()) / (listIngredient.get(j).getQuantity()));
                    // result.add(new Ingredient(String.valueOf(new Random().nextInt()),listIngredient.get(i).getName(), divOfQuantity));
                    result.add(new Ingredient(listIngredient.get(i).getName(), divOfQuantity));
                }
            }
        }

        return result;
    }

  /*  public ResultOptimise getOptimiseCountByRecipe(List<Recipe> recipes, List<Inventory> inventories) {
        System.out.printf("size of recipe" + recipes.size() + " ");
        ArrayList<GradientInventory> listResultGradientInventory = new ArrayList<>();

        ArrayList<Ingredient> listResult = new ArrayList<>();

        lablex: for (Recipe recipe : recipes) {
            ArrayList<GradientInventory> resultIngredientInventory= new ArrayList<>();
            String unusedInventoryCount = null;
            Ingredient ing = new Ingredient();
            log.info(recipe.getRecipeId() + " " + recipe.getName() + recipe.getInstructions() + " " + recipe.getIngredients().size());

            List<Ingredient> ingredientsOfRecipe = recipe.getIngredients();
            for (Ingredient ingredient : ingredientsOfRecipe) {
                log.info("ingredientsOfRecipe: " +  ingredient.getName() + " " + ingredient.getQuantity());
            }

            for (int i = 0; i < ingredientsOfRecipe.size(); i++) {
                System.out.println("i:" + i);
                if (!listResultGradientInventory.isEmpty() && (i < listResultGradientInventory.size())) {
                    for (GradientInventory gradientInventory : listResultGradientInventory) {
                        if (gradientInventory.getName().equalsIgnoreCase(ingredientsOfRecipe.get(i).getName()) &&
                                (gradientInventory.getQuantity().compareTo(ingredientsOfRecipe.get(i).getQuantity()) < 1)) {
                            System.out.println(".............................." + gradientInventory.getName() + " " + ingredientsOfRecipe.get(i).getQuantity());
                           ing = new Ingredient(ingredientsOfRecipe.get(i).getName(), ingredientsOfRecipe.get(i).getQuantity());
                           // break lablex;
                            unusedInventoryCount += unusedInventoryCount;
                        }
                    }
                }

            }


            List<Inventory> neededIngredientsForRecipe = inventories.stream()
                    .filter(os -> ingredientsOfRecipe.stream()                    // filter
                            .anyMatch(ns ->                                  // compare both
                                    os.getName().equals(ns.getName())))
                    .collect(Collectors.toList());

            log.info("Inventories that are needed for this recipe:" + neededIngredientsForRecipe.size());
           // logForIngredient(neededIngredientsForRecipe);
            for (Inventory ingredient : neededIngredientsForRecipe) {
                log.info( " " + ingredient.getName() + " " + ingredient.getQuantity());
            }

            for (Ingredient ingredient : ingredientsOfRecipe) {
                resultIngredientInventory.add(new GradientInventory(ingredient.getId(), ingredient.getName(), ingredient.getQuantity()));
                log.info( " listIngredientInventory in recipe.ingredients: " + ingredient.getName() + " " + ingredient.getQuantity());
            }

            for (Inventory ingredient : neededIngredientsForRecipe) {
                resultIngredientInventory.add(new GradientInventory(ingredient.getId(), ingredient.getName(), ingredient.getQuantity()));
                log.info( " listIngredientInventory in inventory: " + ingredient.getName() + " " + ingredient.getQuantity());
            }

            log.info("ResultIngredientInventory :" + resultIngredientInventory.size());
            for (GradientInventory gradientInventory : resultIngredientInventory) {
                log.info( " " + gradientInventory.getName() + " " + gradientInventory.getQuantity());
            }

            List<GradientInventory> list = divOfQuantityForSameInventory(resultIngredientInventory);
           // List<Ingredient> list = divOfQuantityForSameIngredient(resultArr);
            for (GradientInventory gradientInventory : list) {
                log.info( "list of ingredients and inventories" + gradientInventory.getName() + " " + gradientInventory.getQuantity());
            }

            Integer quantity = list.stream()
                    .min(Comparator.comparing(GradientInventory::getQuantity))
                    .get().getQuantity();

            log.info("min by comparator" + quantity);

            ArrayList<GradientInventory> listOfRemain = remainderForSameIngredient(resultIngredientInventory);
            List<GradientInventory> listx = ifContainSameElemet_(inventories, listOfRemain);

            listResultGradientInventory.addAll(listx);
            for (GradientInventory ingredient : listResultGradientInventory) {
                log.info("Conclusion:" + ingredient.getId() + " " + ingredient.getName() + " " + ingredient.getQuantity());
            }
            resultRepository.save(new Result(recipe.getRecipeId(), String.valueOf(quantity)));
            //resultRepository.put(String.valueOf(new Random().nextInt()), new Result(String.valueOf(new Random().nextInt()), String.valueOf(quantity)));
        }


        System.out.println("jumped out of loop");
        //List<Result> result = resultRepository.values().stream().collect(Collectors.toList());
        List<Result> result = resultRepository.findAll().stream().collect(Collectors.toList());

        Integer sumOfRecipe = result.stream()
                .map(x -> Integer.valueOf(x.getCount())).reduce(0, Integer::sum);

        System.out.println("sumOfRecipe.toString: " + sumOfRecipe.toString());
        return resultOptimiseRepo.save(new ResultOptimise(result, sumOfRecipe.toString(), "ing"));
    }*/

    public ArrayList<GradientInventory> remainderForSameIngredient(List<GradientInventory> listIngredient) {
        ArrayList<GradientInventory> result= new ArrayList<>();
        for (int i = 0; i < listIngredient.size(); i++) {
            for (int j = 0; j < i; j++) {
                if ((listIngredient.get(i).getName().equalsIgnoreCase(listIngredient.get(j).getName()) && ( i !=j)
                        && (listIngredient.get(i).getQuantity()!= 0) && (listIngredient.get(j).getQuantity()!= 0)))  {

                    int remainOfQuantity = ((listIngredient.get(i).getQuantity()) % (listIngredient.get(j).getQuantity()));
                    log.info("remainOfQuantity:" + remainOfQuantity);
                    result.add(new GradientInventory(listIngredient.get(i).getName(), remainOfQuantity));
                }
            }
        }

        return result;
    }

    public ArrayList<Ingredient> remainderOfQuantityForSameIngredient(List<Ingredient> listIngredient) {
        ArrayList<Ingredient> result= new ArrayList<>();
        for (int i = 0; i < listIngredient.size(); i++) {
            for (int j = 0; j < i; j++) {
                if ((listIngredient.get(i).getName().equalsIgnoreCase(listIngredient.get(j).getName()) && ( i !=j)
                        && (listIngredient.get(i).getQuantity()!= 0) && (listIngredient.get(j).getQuantity()!= 0)))  {
                    int remainOfQuantity = ((listIngredient.get(i).getQuantity()) % (listIngredient.get(j).getQuantity()));
                    log.info("remainOfQuantity:" + remainOfQuantity);
                    result.add(new Ingredient(listIngredient.get(i).getName(), remainOfQuantity));
                }
            }
        }

        return result;
    }

    public List<Ingredient> ifContainSameElemet(Collection<Ingredient> listIngredient, ArrayList<Ingredient> ingredients) {
        ArrayList<Ingredient> listResult= new ArrayList<>();
        log.info("==============ingredients size:" + ingredients.size());

        for (int i = 0; i < listIngredient.size(); i++) {
            System.out.println("i:" + i);
            if (i < ingredients.size()) {
                System.out.println("yes");
                for (Ingredient ingredient : listIngredient) {
                    if (ingredient.getName().equalsIgnoreCase(ingredients.get(i).getName())) {
                        listResult.add(ingredients.get(i));
                        System.out.println(".............................." + ingredient.getName() + " " + ingredients.get(i).getQuantity());
                    }
                }
            }
        }
        System.out.println("ListResult size:" + listResult.size());
        for (Ingredient ingredient : listResult) {
            System.out.println(ingredient.getName() + " " + ingredient.getQuantity());
        }
        return listResult;
    }

    public List<GradientInventory> ifContainSameElement_(Collection<Inventory> listIngredient, ArrayList<GradientInventory> gradientInventory) {
        ArrayList<GradientInventory> listResult= new ArrayList<>();
        log.info("==============gradientInventory size:" + gradientInventory.size());

        for (int i = 0; i < listIngredient.size(); i++) {
            System.out.println("i:" + i);
            if (i < gradientInventory.size()) {
                System.out.println("yes");
                for (Inventory inventory : listIngredient) {
                    if (inventory.getName().equalsIgnoreCase(gradientInventory.get(i).getName())) {
                        listResult.add(gradientInventory.get(i));
                        System.out.println(".............................." + inventory.getName() + " " + gradientInventory.get(i).getQuantity());
                    }
                }
            }
        }
        System.out.println("ListResult size:" + listResult.size());
        for (GradientInventory ingredient : listResult) {
            System.out.println(ingredient.getName() + " " + ingredient.getQuantity());
        }
        return listResult;
    }


    private List<Integer> resultForEachRecipe(List<Recipe> recipes, List<Inventory> inventories){
        ArrayList<GradientInventory> listResultGradientInventory = new ArrayList<>();
        List<Integer> unusedInventoryCountList = new ArrayList<>();
        Integer unusedInventoryCountResult = 0;

        lablex:for (Recipe recipe : recipes) {
            boolean flag = true;
                ArrayList<GradientInventory> resultIngredientInventory = new ArrayList<>();
                String unusedInventoryCount = null;
                Ingredient ing = new Ingredient();
                log.info(recipe.getRecipeId() + " " + recipe.getName() + recipe.getInstructions() + " " + recipe.getIngredients().size());

                List<Ingredient> ingredientsOfRecipe = recipe.getIngredients();
                for (Ingredient ingredient : ingredientsOfRecipe) {
                    log.info("ingredientsOfRecipe: " + ingredient.getName() + " " + ingredient.getQuantity());
                }

                for (int i = 0; i < ingredientsOfRecipe.size(); i++) {
                    System.out.println("i:" + i);
                    if (!listResultGradientInventory.isEmpty() && (i < listResultGradientInventory.size())) {
                        for (GradientInventory gradientInventory : listResultGradientInventory) {
                            if (gradientInventory.getName().equalsIgnoreCase(ingredientsOfRecipe.get(i).getName()) &&
                                    (gradientInventory.getQuantity().compareTo(ingredientsOfRecipe.get(i).getQuantity()) < 1)) {
                                System.out.println(".............................." + gradientInventory.getName() + " " + ingredientsOfRecipe.get(i).getQuantity());
                                //ng = new Ingredient(ingredientsOfRecipe.get(i).getName(), ingredientsOfRecipe.get(i).getQuantity());
                                System.out.println(unusedInventoryCount);
                                unusedInventoryCount += unusedInventoryCount;
                                log.info("Before  unusedInventoryCountList: " + unusedInventoryCountList);
                                unusedInventoryCountList.add(ingredientsOfRecipe.get(i).getQuantity());
                                log.info("After unusedInventoryCountList: " + unusedInventoryCountList);
                                flag = false;
                            }
                        }
                    }

                }

                if (flag == true) {
                    List<Inventory> neededIngredientsForRecipe = inventories.stream()
                            .filter(os -> ingredientsOfRecipe.stream()                    // filter
                                    .anyMatch(ns ->                                  // compare both
                                            os.getName().equals(ns.getName())))
                            .collect(Collectors.toList());

                    log.info("Inventories that are needed for this recipe:" + neededIngredientsForRecipe.size());
                    // logForIngredient(neededIngredientsForRecipe);
                    for (Inventory ingredient : neededIngredientsForRecipe) {
                        log.info(" " + ingredient.getName() + " " + ingredient.getQuantity());
                    }

                    for (Ingredient ingredient : ingredientsOfRecipe) {
                        resultIngredientInventory.add(new GradientInventory(ingredient.getId(), ingredient.getName(), ingredient.getQuantity()));
                        log.info(" listIngredientInventory in recipe.ingredients: " + ingredient.getName() + " " + ingredient.getQuantity());
                    }

                    for (Inventory Inventory : neededIngredientsForRecipe) {
                        resultIngredientInventory.add(new GradientInventory(Inventory.getId(), Inventory.getName(), Inventory.getQuantity()));
                        log.info(" listIngredientInventory in inventory: " + Inventory.getName() + " " + Inventory.getQuantity());
                        //}
                    }

                    //doeslistGradientInventoryContainInventory(listResultGradientInventory, neededIngredientsForRecipe);

                    log.info("ResultIngredientInventory :" + resultIngredientInventory.size());
                    for (GradientInventory gradientInventory : resultIngredientInventory) {
                        log.info(" " + gradientInventory.getName() + " " + gradientInventory.getQuantity());
                    }

                    List<GradientInventory> listDivision = divOfQuantityForSameInventory(resultIngredientInventory);
                    // List<Ingredient> list = divOfQuantityForSameIngredient(resultArr);
                    for (GradientInventory gradientInventory : listDivision) {
                        log.info("Inventories after division to Ingredients: " + gradientInventory.getName() + " " + gradientInventory.getQuantity());
                    }

                    Integer minInventory = listDivision.stream()
                            .min(Comparator.comparing(GradientInventory::getQuantity))
                            .get().getQuantity();
                    log.info("min Inventory by comparator" + minInventory);

                    ArrayList<GradientInventory> listOfRemain = new ArrayList<>();
                    for (GradientInventory gradientInventory : listDivision) {
                        Integer quantity = gradientInventory.getQuantity();
                        Integer r = quantity-minInventory;
                        if(r > 0){
                        resultIngredientInventory.add(new GradientInventory(gradientInventory.getName(), r));
                        resultIngredientInventory.remove(gradientInventory);
                            resultIngredientInventory.remove(gradientInventory.getId());
                        }
                        unusedInventoryCountList.add(r);
                        log.info("After unusedInventoryCountList: " + unusedInventoryCountList);
                        listOfRemain.add(new GradientInventory(gradientInventory.getName(), r));
                    }

                    log.info(String.valueOf(listOfRemain.size()));
                    for (GradientInventory gradientInventory : listOfRemain) {
                        log.info("listOfRemain:" +  gradientInventory.getName() + " " + gradientInventory.getQuantity());
                    }
                    for (GradientInventory gradientInventory : resultIngredientInventory) {
                        log.info(String.valueOf(resultIngredientInventory.size()));
                        log.info("resultIngredientInventory:" +  gradientInventory.getName() + " " + gradientInventory.getQuantity());
                    }



                   // ArrayList<GradientInventory> listOfRemain = remainderForSameIngredient(resultIngredientInventory);
                    List<GradientInventory> listx = ifContainSameElement_(inventories, listOfRemain);
                    for (GradientInventory gradientInventory : listx) {
                        log.info(String.valueOf(resultIngredientInventory.size()));
                        log.info("listx:" +  gradientInventory.getName() + " " + gradientInventory.getQuantity());
                    }

                    listResultGradientInventory.addAll(listx);
                    for (GradientInventory ingredient : listResultGradientInventory) {
                        log.info("Conclusion:" + ingredient.getId() + " " + ingredient.getName() + " " + ingredient.getQuantity());
                    }
                    resultRepository.save(new Result(recipe.getRecipeId(), String.valueOf(minInventory)));
                    //resultRepository.put(String.valueOf(new Random().nextInt()), new Result(String.valueOf(new Random().nextInt()), String.valueOf(quantity)));
                }

    }
    return unusedInventoryCountList;
    }


    class GradientInventory{
        private @Id
        @GeneratedValue
        @JsonIgnore()
        Long id;

        private  String name; // name, e.g. "Sugar"
        private  Integer quantity; // how much of this ingredient? Must be > 0

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public GradientInventory(String name, Integer quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public GradientInventory(Long id, String name, Integer quantity) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
        }
    }
}

