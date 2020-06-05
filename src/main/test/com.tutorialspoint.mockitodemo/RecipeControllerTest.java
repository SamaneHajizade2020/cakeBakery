package com.tutorialspoint.mockitodemo;

import cakeBakery.Ingredient;
import cakeBakery.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.tutorialspoint.mokitodemo.AbstractTest;

import java.util.ArrayList;

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

        ArrayList< Ingredient > ingredients = new ArrayList<>();
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

   // @Test
    public void updateProduct() throws Exception {
        String uri = "/products/3";
        Recipe product = new Recipe();
        product.setName("Lemon cake");
        String inputJson = super.mapToJson(product);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Product is updated successsfully");
    }

    //@Test
    public void deleteInventory() throws Exception {
        String uri = "/recipes/3";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Product is deleted successsfully");
    }
}
