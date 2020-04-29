package cakeBakery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Access( AccessType.FIELD )
public class Ingredient {

    //@JsonIgnore()
    //private String id;

    private @Id
    @GeneratedValue
    @JsonIgnore()
    Long id;
    private  String name; // name, e.g. "Sugar"
    private  Integer quantity; // how much of this ingredient? Must be > 0

/*    @ManyToOne
    @JoinColumn(name="recipeId")*/

    @ManyToOne
    @JoinColumn(name = "Id_ingredient")
    private Recipe recipe;

   @ManyToOne(optional = false)
    @JoinColumn(name = "Id_ingredient", nullable = true)
    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }


    public Ingredient(String name, Integer quantity){
        this.name= name;
        this.quantity = quantity;
    }

    public Ingredient(Long id, String name, Integer quantity) {
       this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public Ingredient(){}

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
