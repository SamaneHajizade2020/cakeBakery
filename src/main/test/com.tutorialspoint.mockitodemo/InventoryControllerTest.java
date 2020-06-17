package com.tutorialspoint.mockitodemo;

import cakeBakery.Inventory;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static junit.framework.TestCase.*;
import static junit.framework.TestCase.assertEquals;
import com.tutorialspoint.mokitodemo.AbstractTest;

import java.util.ArrayList;

public class InventoryControllerTest extends AbstractTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void createInventory() throws Exception {
        String uri = "/createInventory";

        ArrayList<Inventory>  inventoryArrayList = new ArrayList<>();
        Inventory inventory = new Inventory("Chocolate",  4 );
        Inventory inventory2 = new Inventory("Doughnut",  4);
        Inventory inventory3 = new Inventory("Sugar", 30);
        Inventory inventory4 = new Inventory("Cake mix", 20);
        inventoryArrayList.add(inventory);
        inventoryArrayList.add(inventory2);
        inventoryArrayList.add(inventory3);
        inventoryArrayList.add(inventory4);

        String inputJson = super.mapToJson(inventoryArrayList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson);

        MvcResult mvcResult = mvc.perform(requestBuilder).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Product is created successfully");
    }

    @Test
    public void getInventoryList() throws Exception {
        String uri = "/inventory";
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult mvcResult = mvc.perform(requestBuilder).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Inventory[] inventories = super.mapFromJson(content, Inventory[].class);
        assertTrue(inventories.length > 0);
    }

    @Test
    public void fillInventory() throws Exception {
        String uri= "/inventory/fill";
        String inputJson= "[{\"name\": \"Bun\", \"quantity\": 46}, {\"name\": \"kakao\", \"quantity\": 9},{\"name\": \"Sugar\", \"quantity\": 6}]";

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson);

        MvcResult mvcResult = mvc.perform(requestBuilder).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Product is created successfully");
    }


}
