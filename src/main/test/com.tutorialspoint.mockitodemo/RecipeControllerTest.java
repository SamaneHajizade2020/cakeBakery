package com.tutorialspoint.mockitodemo;

import cakeBakery.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import com.tutorialspoint.mokitodemo.AbstractTest;

public class RecipeTest extends AbstractTest {

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
}
