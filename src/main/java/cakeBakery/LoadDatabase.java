
package cakeBakery;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
//
@Configuration
//@Slf4j
class LoadDatabase {
    private final static Logger log = Logger.getLogger(LoadDatabase.class.getName());

   @Bean
    CommandLineRunner initDatabase(RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        Ingredient suger = new Ingredient("Suger", 1);
        Ingredient vanilla = new Ingredient("vanilla", 2);

        List<Ingredient> ingredients_ = new ArrayList<>();
        ingredients_.add(suger);
        ingredients_.add(vanilla);
        Recipe recipe = new Recipe();
        recipe.setName("Pandora cake");
        recipe.setInstructions("Same as cheese cake");
        recipe.setIngredients(ingredients_);


        return args -> {
            Ingredient ingredient1 = ingredientRepository.saveAndFlush(suger);
            log.info("Preloading " + ingredient1);
            log.info("Preloading " + ingredientRepository.saveAndFlush(vanilla));
            log.info("Preloading " + recipeRepository.save(recipe));
        };
    }
}
