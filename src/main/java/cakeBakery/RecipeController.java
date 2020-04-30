package cakeBakery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
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

    public RecipeController() {
    }

    public RecipeController(RecipeRepository recipeRepository, IngredientRepository ingredientRepository, InventoryRepository inventoryRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Autowired(required=true)
    ResultRepository resultRepository;

    //@Autowired(required=true)
    //ResultOptionRepository optionRepository;

    public RecipeController(RecipeRepository recipeRepository, IngredientRepository ingredientRepository, ResultRepository resultRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.resultRepository = resultRepository;
       // this.optionRepository = optionRepository;
    }

    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public IngredientRepository getIngredientRepository() {
        return ingredientRepository;
    }

    public void setIngredientRepository(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
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
    public Recipe updateProduct(@PathVariable("id") Long id, @RequestBody Recipe newRecipe) {

        log.info("newRecipe.getRecipeId: "+ newRecipe.getRecipeId());
        if(newRecipe.getRecipeId() == null) {
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
        }else {
            throw new ResponseStatusException( HttpStatus.NOT_ACCEPTABLE, "It is impossible to set id to recipe.");
        }


       // return new ResponseEntity<>("Product is updated successsfully", HttpStatus.OK);
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


//    @RequestMapping(value = "/recipes/optimize-total-count", method = RequestMethod.GET)
//    public ResponseEntity<Object> getRecipesOptimizeTotalCount() {
//         //getOptimiseCountByRecipe(recipeRepository.findAll(), ingredientRepository.findAll());
//        //service.test(service.getRecipes(), ingredientService.getIngredients(),optimiseCountByRecipe);
//        return new ResponseEntity<>(resultRepository.findAll(), HttpStatus.CREATED);
//    }

   public void getCountByRecipe(Collection<Recipe> recipes, Collection<Inventory> inventories) {
       for (Recipe recipe : recipes) {
           log.info( recipe.getRecipeId() + " " + recipe.getName() + recipe.getInstructions() + " " + recipe.getIngredients().size());
           ArrayList<Ingredient> resultArr= new ArrayList<>();
           ArrayList<Inventory> resultArrInventory= new ArrayList<>();
           ArrayList<GradientInventory> resultIngredientInventory= new ArrayList<>();

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

           //resultArr.addAll(ingredientsOfRecipe);
           for (Ingredient ingredient : ingredientsOfRecipe) {
               resultIngredientInventory.add(new GradientInventory(ingredient.getId(), ingredient.getName(), ingredient.getQuantity()));
           }

           //resultArr.addAll(ingredientOfInventoriesWhichAreInThisRecipe);
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

           resultRepository.save(new Result(recipe.getRecipeId(), String.valueOf(quantity)));
                 //  .put(String.valueOf(new Random().nextInt()),
                   //new Result(String.valueOf(new Random().nextInt()), String.valueOf(quantity)));
       }

   }

    private void logForRecipe(Recipe recipe) {
        log.info("Ingredient of recipe" + recipe.getRecipeId());
        for (Ingredient ingredient : recipe.getIngredients()) {
            log.info( " " + ingredient.getName() + " " + ingredient.getQuantity());
        }
    }

    private void logForIngredient(Collection<Ingredient> ingredients) {
        System.out.printf("Size of ing" + ingredients.size());
        for (Ingredient ingredient : ingredients) {
            log.info( " " + ingredient.getName() + " " + ingredient.getQuantity());
        }
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


    public void getOptimiseCountByRecipe(List<Recipe> recipes, List<Ingredient> ingredients) {
        System.out.printf("size of recipe" + recipes.size() + " ");

        ArrayList<Ingredient> listResult = new ArrayList<>();

        lablex: for (Recipe recipe : recipes) {
            ArrayList<Ingredient> resultArr = new ArrayList<>();
            Ingredient ing = new Ingredient();

            log.info(recipe.getRecipeId() + " " + recipe.getName() + recipe.getInstructions() + " " + recipe.getIngredients().size());

            List<Ingredient> ingredientsOfRecipe = recipe.getIngredients();
            for (Ingredient ingredient : ingredientsOfRecipe) {
                log.info(" " + ingredient.getName() + " " + ingredient.getQuantity());
            }

            for (int i = 0; i < ingredientsOfRecipe.size(); i++) {
                System.out.println("i:" + i);
                if (!listResult.isEmpty() && (i < listResult.size())) {
                    for (Ingredient ingredient : listResult) {
                        if (ingredient.getName().equalsIgnoreCase(ingredientsOfRecipe.get(i).getName()) &&
                                (ingredient.getQuantity().compareTo(ingredientsOfRecipe.get(i).getQuantity()) < 1)) {
                            System.out.println(".............................." + ingredient.getName() + " " + ingredientsOfRecipe.get(i).getQuantity());
                            ing = new Ingredient(ingredientsOfRecipe.get(i).getName(), ingredientsOfRecipe.get(i).getQuantity());
                            break lablex;
                        }
                    }
                }
            }


            List<Ingredient> neededIngredientsForRecipe = ingredients.stream()
                    .filter(os -> ingredientsOfRecipe.stream()                    // filter
                            .anyMatch(ns ->                                  // compare both
                                    os.getName().equals(ns.getName())))
                    .collect(Collectors.toList());

            log.info("Ingredient that are in this recipe:" + neededIngredientsForRecipe.size());
            logForIngredient(neededIngredientsForRecipe);

            resultArr.addAll(ingredientsOfRecipe);
            resultArr.addAll(neededIngredientsForRecipe);
            log.info("resultArr:" + resultArr.size());

            List<Ingredient> list = divOfQuantityForSameIngredient(resultArr);
            logForIngredient(list);

            //math(list, 0 , (a, b) -> a - b)
            //Comparator<Ingredient> byName =(Ingredient o1, Ingredient o2) -> o1.getName().compareTo(o2.getName());

            Integer quantity = findMinInList(list);
            log.info("min by comprator" + quantity);

            ArrayList<Ingredient> listOfRemain = remainderOfQuantityForSameIngredient(resultArr);
            List<Ingredient> listx = ifContainSameElemet(ingredients, listOfRemain);

            listResult.addAll(listx);
            for (Ingredient ingredient : listResult) {
                log.info("Conclusion:" + ingredient.getId() + " " + ingredient.getName() + " " + ingredient.getQuantity());
            }
            //todo: it should be fix
            //resultRepository.put(String.valueOf(new Random().nextInt()), new Result(String.valueOf(new Random().nextInt()), String.valueOf(quantity)));
        }

        List<Result> result = null;
        //todo: it should be fix
        //resultRepository.values().stream().collect(Collectors.toList());

        Integer sumOfRecipe = result.stream()
                .map(x -> Integer.valueOf(x.getCount())).reduce(0, Integer::sum);

        //todo: it should be fix
       // resultOptimiseRepo.put(String.valueOf(new Random().nextInt()), new ResultOptimise(result, sumOfRecipe.toString(), "ing"));
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

