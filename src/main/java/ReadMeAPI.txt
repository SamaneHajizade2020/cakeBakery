https://serverless-kitchen.3rik.se/api
main() | API | BE | FE | Test
API
All endpoint should return relevant HTTP-status, e.g. 200 OK OK, 404 Not Found or 403 Bad Request etc.

In all cases, the request payload (if such exist) and response body (if such exist) should be in JSON format.


API Models
Your serverless kitchen contains two different kinds of items. Recipes and Ingredients

Recipe
{
    "id": integer, // > 0
    "name": string, // name of the recipe
    "instructions": string, // instructions & howto
    "ingredients": [] // list of ingredients
}
Ingredient
{
    "name": string, // name, e.g. "Sugar"
    "quantity": integer // how much of this ingredient? Must be > 0
}
Example recipe
{
    "id": 1,
    "name": "Sugar cake",
    "instructions": "Mix, then bake in oven",
    "ingredients": [
        { "name": "Sugar", "quantity": 4 },
        { "name": "Cake mix", "quantity": 1 }
    ]
}

API Endpoints
GET /ping
Dummy method for testing that the REST controller is set up correcly.


Response body:
pong
POST /clear
Calling this method should clear all entries in the database. No inventory nor recipes should be left. It is mainly used to reset the state when testing. It is typically not a good idea to have this kind of functionality in a real API but part of this exercise for testability and practicing DB commands.


Request payload:
// N/A

Response body:
// N/A, i.e. status 204 No Content
GET /recipes
Should return a JSON-formatted list of all current recipes. See the model structure of a Recipe above. Please note that the response below is simply an example. If no recipes exist, then an empty list, i.e. [], should be returned.


Response body:
[
    {
        "id": 1,
        "name": "Sugar cake",
        "instructions": "Mix, then bake in oven",
        "ingredients": [
            { "name": "Sugar", "quantity": 4 },
            { "name": "Cake mix", "quantity": 1 }
        ]
    },
    {
        "id": 2,
        "name": "Cinnamon bun",
        "instructions": "Put cinnamon on the bun",
        "ingredients": [
            { "name": "Cinnamon", "quantity": 1 },
            { "name": "Bun", "quantity": 1 }
        ]
    }
]
POST /recipes/create
This is how new recipes are created. Should return the newly created recipe, with its newly calculated id property being set. By the way, one cannot have Recipes with negative quantities, but who would ever do that?!


Request payload:
{
    "name": "Chocolate Doughnuts",
    "instructions": "Melt chocolate, dip the Doughnut",
    "ingredients": [
        { "name": "Chocolate", "quantity": 4 },
        { "name": "Doughnut", "quantity": 4 }
    ]
}

Response body:
{
    "id": 3,
    "name": "Chocolate Doughnuts",
    "instructions": "Melt chocolate, dip the Doughnut",
    "ingredients": [
        { "name": "Chocolate", "quantity": 4 },
        { "name": "Doughnut", "quantity": 4 }
    ]
}
GET /recipes/{id}
Get a specific recipe, having id {id}. Should return status: 404 Not Found if there is no such recipe.


Response body:
{
    "id": 3,
    "name": "Chocolate Doughnuts",
    "instructions": "Melt chocolate, dip the Doughnut",
    "ingredients": [
        { "name": "Chocolate", "quantity": 4 },
        { "name": "Doughnut", "quantity": 4 }
    ]
}
DELETE /recipes/{id}
Delete a specific recipe. This method shoud be idempotent.


Response body:
// N/A, i.e. status 204 No Content
PATCH /recipes/{id}
Modify one or more properties of a Recipe. This method shoud be idempotent. Should return 200 OK on success, along with the entire (partly updated) Recipe, or 404 Not Found if no Recipe with id {id} can be found. Please note that it shall not be allowed to change the id-property of a recipe. Trying to do so should result in a 403 Bad Request with some kind of explanation of what went wrong.


Request payload:
{
    "name": "Chocolate Doughnuts v2"
}

Response body:
{
    "id": 3,
    "name": "Chocolate Doughnuts v2",
    "instructions": "Melt chocolate, dip the Doughnut",
    "ingredients": [
        { "name": "Chocolate", "quantity": 4 },
        { "name": "Doughnut", "quantity": 4 }
    ]
}
POST /recipes/{id}/make
What you all have been waiting for. Everything that is being made will be Yummy! Trying to make something that requires more ingredents than what is currently in the inventory should result in 403 Bad Request with some kind of explanation of what went wrong.


Request payload:
// N/A

Response body:
Yummy!
GET /inventory
Get a list of the current inventory. Nothing in the inventory? Then [] should be returned. Please note that you should not return entites that have 0 (zero) quantity. That is sooo pointless.


Response body:
[
    { "name": "Chocolate", "quantity": 4 },
    { "name": "Doughnut", "quantity": 4 },
    { "name": "Sugar", "quantity": 30 },
    { "name": "Cake mix", "quantity": 20 }
]
POST /inventory/fill
When you have made your shopping, this is how you add stuff to your kitchen inventory. If you did not have any of that ingredient before, the quantity in the inventory should now equal the quantity specified in the request. If you already had some, then the new updated quantity should be the sum of what you had before plus what was added by this request. For example, given the api calls in these examples (the one above, and this one), you should now have 130 sugar. By the way, one cannot fill the inventory with negative quantities, but who would ever do that?! If it happens, reject the entire request.


Request payload:
[
    { "name": "Sugar", "quantity": 100 },
    { "name": "Cake mix", "quantity": 50 }
]

Response body:
// N/A, i.e. status 204 No Content
Advanced stuff!
Let's step the API up a notch. Given the following /recipes

[
    {
        "id": 1,
        "name": "Sugar Cake",
        "instructions": "Mix, then bake in oven",
        "ingredients": [
            { "name": "Sugar", "quantity": 4 },
            { "name": "Cake mix", "quantity": 1 }
        ]
    },
    {
        "id": 2,
        "name": "Cinnamon Bun",
        "instructions": "Put cinnamon on the bun",
        "ingredients": [
            { "name": "Cinnamon", "quantity": 1 },
            { "name": "Bun", "quantity": 1 }
        ]
    },
    {
        "id": 3,
        "name": "Cotton Candy",
        "instructions": "Spinn it well!",
        "ingredients": [
            { "name": "Sugar", "quantity": 5 },
            { "name": "Pink color", "quantity": 10 }
        ]
    }
]
And, given the following /inventory

[
    { "name": "Sugar", "quantity": 44 },
    { "name": "Cake mix", "quantity": 11 },
    { "name": "Cinnamon", "quantity": 5 },
    { "name": "Bun", "quantity": 4 },
    { "name": "Pink color", "quantity": 10 }
]
GET /recipes/get-count-by-recipe
For each of the current recipes, state how many of that recipe you can make while taking the current inventory into consideration. The calculation is PER RECIPE, ignoring the other recipes when doing the calculation for each recipe. That is, the entire inventory will be used for this single recipe.


Response body:
[
    {"id":1, "count":11},
    {"id":2, "count":4},
    {"id":3, "count":1}
]
// Total of 16 recipes made, though not possible to do all at the same time. This comment is NOT part of the output
GET /recipes/optimize-total-count
What matters is quantity, right? Now, instead of calculating per recipe in isolation, optimize the total count of recipes that you can do. In the example below, the total count is FIFTEEN; 11 of recipe #1 and 4 of recipe #2. Recipe #3 (from the request above) is excluded, as the other recipes consumed its ingredients and this was the optimal solution to maximize the total count of recipes made. Unused inventory is 10 Pink color, and 1 Cinnamon = 11


Response body:
{
    "recipes":[
        {"id":1, "count":11},
        {"id":2, "count":4}
    ],
    "recipeCount":15,
    "unusedInventoryCount":11
}
GET /recipes/optimize-total-waste
No, wait a minute! Let's minimize the waste instead. Given the total inventory, which recipe(s) should you pick (and how many of them) to reduce the inventory size to as close to zero as possible? That is, when you have done these recipes, there should be as little unused ingredients as possible left in the inventory. The total count will be equal or less than /optimize-total-count, but the sum of all (remaining) ingredients in the inventory should also be equal or less. In this case, 3 Sugar + 2 Cake Mix + 1 Cinnamon = 6 ingredients are unused/wasted (compared to 11 above).


Response body:
{
    "recipes":[
        {"id":1, "count":9},
        {"id":2, "count":4},
        {"id":3, "count":1}
    ],
    "recipeCount":14,
    "unusedInventoryCount":6
}
v.0.5.0-beta
