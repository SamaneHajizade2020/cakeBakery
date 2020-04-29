package cakeBakery;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Access( AccessType.FIELD )
public class Recipe {

    private
    @Id
    @GeneratedValue
    Long recipeId;
  //  private Long id; // > 0
    private String name;  // name of the recipe
    private String instructions;  // instructions & howto

    @OneToMany(targetEntity= Ingredient.class, fetch=FetchType.EAGER, cascade = {CascadeType.MERGE})
    //@OneToMany(targetEntity=Ingredient.class, fetch=FetchType.EAGER)
   // @Cascade({SAVE_UPDATE, EVICT, DELETE, DELETE_ORPHAN})

 //   @OneToMany(mappedBy = "recipe", orphanRemoval = true, cascade = CascadeType.MERGE)
    private List<Ingredient> ingredients; // list of ingredients


    public Recipe( String name, String instructions, List<Ingredient> ingredients) {
        this.name = name;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }


    public Recipe(Long id, String name, String instructions, List<Ingredient> ingredients) {
        this.recipeId = id;
        this.name = name;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public Recipe() {
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

     public String getInstructions() {
          return instructions;
     }

     public void setInstructions(String instructions) {
          this.instructions = instructions;
     }


     public List<Ingredient> getIngredients() {
          return ingredients;
     }

    //@OneToMany(mappedBy = "location")
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

/*    public void addIngredients(List<Ingredient> aSet) {
        //this.sonEntities = aSet; //This will override the set that Hibernate is tracking.
       // this.ingredients.clear();
        if (aSet != null) {
            this.ingredients.addAll(aSet);
        }
    }*/
}
