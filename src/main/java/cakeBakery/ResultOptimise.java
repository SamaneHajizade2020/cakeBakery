package cakeBakery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Access( AccessType.FIELD )
public class ResultOptimise {

    @JsonIgnore()
    @GeneratedValue
    private @Id
    Long id;
    @OneToMany(targetEntity= Result.class, fetch=FetchType.EAGER, cascade = {CascadeType.MERGE})
    private List<Result> recipes;
    private String recipeCount;
    private String unusedInventoryCount;

    public ResultOptimise(Long id, List<Result> results, String recipeCount, String unusedInventoryCount) {
        this.id = id;
        this.recipes = results;
        this.recipeCount = recipeCount;
        this.unusedInventoryCount = unusedInventoryCount;
    }

    public ResultOptimise(List<Result> results, String recipeCount, String unusedInventoryCount) {
        this.recipes = results;
        this.recipeCount = recipeCount;
        this.unusedInventoryCount = unusedInventoryCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Result> getResults() {
        return recipes;
    }

    public void setResults(List<Result> results) {
        this.recipes = results;
    }

    public String getRecipeCount() {
        return recipeCount;
    }

    public void setRecipeCount(String recipeCount) {
        this.recipeCount = recipeCount;
    }

    public String getUnusedInventoryCount() {
        return unusedInventoryCount;
    }

    public void setUnusedInventoryCount(String unusedInventoryCount) {
        this.unusedInventoryCount = unusedInventoryCount;
    }
}
