package cakeBakery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;

@SpringBootApplication
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, XADataSourceAutoConfiguration.class})
public class PayrollApplication {

    @Autowired
    RecipeRepository recipeRepository;
    IngredientRepository ingredientRepository;
    InventoryRepository inventoryRepository;

    public static void main(String... args) {
        SpringApplication.run(PayrollApplication.class, args);
    }

    public void run(String... args) throws Exception {
        //Code to run at application startup
    }
}