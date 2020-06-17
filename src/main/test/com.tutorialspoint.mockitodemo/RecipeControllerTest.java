package com.tutorialspoint.mockitodemo;

import cakeBakery.Ingredient;
import cakeBakery.Recipe;
import cakeBakery.RecipeNotFoundException;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.tutorialspoint.mokitodemo.AbstractTest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;

public class RecipeControllerTest extends AbstractTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void getRecipeList() throws Exception {
        String uri = "/recipes";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult mvcResult = mvc.perform(requestBuilder).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Recipe[] recipes = super.mapFromJson(content, Recipe[].class);
        assertTrue(recipes.length > 0);
    }

    @Test
    public void getRecipeById() throws Exception {
        String uri = "/recipe/3";

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MvcResult mvcResult = mvc.perform(requestBuilder).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertNotNull(content, Recipe.class);
        System.out.println(content);
    }

    @Test
    public void createRecipe() throws Exception {
        String uri = "/recipes/create";

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        Ingredient ingredient = new Ingredient("Suger", 4);
        ingredients.add(ingredient);
        Recipe recipe = new Recipe("Torta", "cream plus spomg", ingredients);

        String inputJson = super.mapToJson(recipe);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson);
        MvcResult mvcResult = mvc.perform(requestBuilder).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        System.out.println(content);
        assertEquals(content, "Product is created successfully");
    }

    @Test
    public void updateProduct() throws Exception {
        String uri = "/products/7";
        Recipe product = new Recipe();
        product.setName("Lemon cake");
        String inputJson = super.mapToJson(product);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Product is updated successfully");
    }

    //@Test
    public void deleteInventory() throws Exception {
        String uri = "/recipes/3";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Product is deleted successfully");
    }

    @Test
    public void createRecipeYummy() throws Exception {
        String uri = "/recipes/{id}/make";
        String actual = "{\"name\": \"Chocolate Doughnuts\",\"instructions\": \"Melt chocolate, dip the Doughnut\",    \"ingredients\": []}";

        JSONAssert.assertEquals(
                "{\"name\": \"Chocolate Doughnuts\",\"instructions\": \"Melt chocolate, dip the Doughnut\",\"ingredients\": []}",
                actual,
                JSONCompareMode.LENIENT);

        JSONAssert.assertNotEquals(
                "{\"name\": \"Chocolate Doughnuts\",\"instructions\": \"Melt chocolate, dip the Doughnut\",\"ingredients\": [" +
                        "{ \"name\": \"Chocolate\", \"quantity\": 0 },{ \"name\": \"Doughnut\", \"quantity\": 4 }]}",
                actual,
                JSONCompareMode.LENIENT);
    }

    @Test
    public void getCountByRecipe() throws Exception {
    String actual =
            "[\n" +
                    "    {\"id\":1, \"count\":11},\n" +
                    "    {\"id\":2, \"count\":4},\n" +
                    "    {\"id\":3, \"count\":1}\n" +
                    "]";
        JSONAssert.assertEquals(
                "[\n"+
                "    {\"id\":1, \"count\":11},\n"+
                "    {\"id\":2, \"count\":4},\n"+
                "    {\"id\":3, \"count\":1}\n"+
                "]",
    actual,
    JSONCompareMode.LENIENT
        );
}

}
