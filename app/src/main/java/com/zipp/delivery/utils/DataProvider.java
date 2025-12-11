package com.zipp.delivery.utils;

import com.zipp.delivery.R;
import com.zipp.delivery.models.Category;
import com.zipp.delivery.models.FoodItem;
import com.zipp.delivery.models.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataProvider {

    public static List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Pizza", R.drawable.ic_pizza, R.color.cat_pizza));
        categories.add(new Category(2, "Hamburguesas", R.drawable.ic_burger, R.color.cat_burger));
        categories.add(new Category(3, "Sushi", R.drawable.ic_sushi, R.color.cat_sushi));
        categories.add(new Category(4, "Mexicana", R.drawable.ic_mexican, R.color.cat_mexican));
        categories.add(new Category(5, "China", R.drawable.ic_chinese, R.color.cat_chinese));
        categories.add(new Category(6, "Postres", R.drawable.ic_dessert, R.color.cat_dessert));
        categories.add(new Category(7, "Bebidas", R.drawable.ic_drinks, R.color.cat_drinks));
        categories.add(new Category(8, "Saludable", R.drawable.ic_healthy, R.color.cat_healthy));
        return categories;
    }

    public static List<Restaurant> getPopularRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();

        Restaurant r1 = new Restaurant(1, "Pizza Palace", 
            "Las mejores pizzas artesanales de la ciudad", 
            "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=600&h=400&fit=crop", 
            "Pizza",
            4.8, 324, "25-35", 0, 30.00, "Av. Principal 123");
        r1.setMenu(getPizzaMenu(1));
        restaurants.add(r1);

        Restaurant r2 = new Restaurant(2, "Burger Kingdom",
            "Hamburguesas gourmet con ingredientes premium", 
            "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&h=400&fit=crop", 
            "Hamburguesas",
            4.6, 256, "20-30", 4.50, 25.00, "Calle Centro 456");
        r2.setMenu(getBurgerMenu(2));
        restaurants.add(r2);

        Restaurant r3 = new Restaurant(3, "Sakura Sushi",
            "Auténtico sushi japonés preparado por chefs expertos", 
            "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=600&h=400&fit=crop", 
            "Sushi",
            4.9, 189, "30-45", 6.00, 40.00, "Plaza Oriental 789");
        r3.setMenu(getSushiMenu(3));
        restaurants.add(r3);

        Restaurant r4 = new Restaurant(4, "Taquería El Sol",
            "Tacos y comida mexicana tradicional", 
            "https://images.unsplash.com/photo-1565299585323-38174c3d0f27?w=600&h=400&fit=crop", 
            "Mexicana",
            4.7, 412, "15-25", 0, 20.00, "Av. México 321");
        r4.setMenu(getMexicanMenu(4));
        restaurants.add(r4);

        Restaurant r5 = new Restaurant(5, "Dragon Wok",
            "Cocina china tradicional y moderna", 
            "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=600&h=400&fit=crop", 
            "China",
            4.5, 178, "25-40", 5.00, 28.00, "Barrio Chino 555");
        r5.setMenu(getChineseMenu(5));
        restaurants.add(r5);

        Restaurant r6 = new Restaurant(6, "Sweet Dreams",
            "Postres artesanales y dulces deliciosos", 
            "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600&h=400&fit=crop", 
            "Postres",
            4.8, 234, "20-30", 3.50, 25.00, "Calle Dulce 888");
        r6.setMenu(getDessertMenu(6));
        restaurants.add(r6);

        return restaurants;
    }

    public static List<Restaurant> getNearbyRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();

        restaurants.add(new Restaurant(7, "Green Bowl",
            "Ensaladas frescas y bowls nutritivos", 
            "https://images.unsplash.com/photo-1546793665-c74683f339c1?w=600&h=400&fit=crop", 
            "Saludable",
            4.4, 145, "15-25", 4.00, 22.00, "Av. Verde 111"));

        restaurants.add(new Restaurant(8, "Juice Bar",
            "Jugos naturales y smoothies", 
            "https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=600&h=400&fit=crop", 
            "Bebidas",
            4.6, 98, "10-15", 0, 15.00, "Plaza Fitness 222"));

        restaurants.add(new Restaurant(9, "La Parrilla",
            "Carnes a la parrilla y cortes selectos", 
            "https://images.unsplash.com/photo-1558030006-450675393462?w=600&h=400&fit=crop", 
            "Parrilla",
            4.7, 267, "30-45", 7.00, 45.00, "Calle Asado 333"));

        restaurants.add(new Restaurant(10, "Pasta Bella",
            "Pastas italianas auténticas", 
            "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=600&h=400&fit=crop", 
            "Italiana",
            4.5, 189, "25-35", 5.00, 30.00, "Via Roma 444"));

        return restaurants;
    }

    public static Restaurant getRestaurantById(int id) {
        List<Restaurant> all = new ArrayList<>();
        all.addAll(getPopularRestaurants());
        all.addAll(getNearbyRestaurants());

        for (Restaurant r : all) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    private static List<FoodItem> getPizzaMenu(int restaurantId) {
        List<FoodItem> items = new ArrayList<>();
        items.add(new FoodItem(101, "Pizza Margherita", 
            "Salsa de tomate, mozzarella fresca y albahaca", 
            "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400&h=400&fit=crop", 
            32.00, "Clásicas", restaurantId, true));
        items.add(new FoodItem(102, "Pizza Pepperoni",
            "Pepperoni premium y queso mozzarella", 
            "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=400&h=400&fit=crop", 
            38.00, "Clásicas", restaurantId, true));
        items.add(new FoodItem(103, "Pizza Hawaiana",
            "Jamón, piña y queso mozzarella", 
            "https://images.unsplash.com/photo-1571997478779-2adcbbe9ab2f?w=400&h=400&fit=crop", 
            35.00, "Especiales", restaurantId));
        items.add(new FoodItem(104, "Pizza 4 Quesos",
            "Mozzarella, gorgonzola, parmesano y provolone", 
            "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400&h=400&fit=crop", 
            42.00, "Especiales", restaurantId, true));
        items.add(new FoodItem(105, "Pizza Vegetariana",
            "Champiñones, pimientos, aceitunas y cebolla", 
            "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400&h=400&fit=crop", 
            36.00, "Especiales", restaurantId));
        items.add(new FoodItem(106, "Pizza BBQ Chicken",
            "Pollo, salsa BBQ, cebolla morada y cilantro", 
            "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=400&h=400&fit=crop", 
            40.00, "Premium", restaurantId));
        items.add(new FoodItem(107, "Palitos de Ajo",
            "Con salsa marinara", 
            "https://images.unsplash.com/photo-1571997478779-2adcbbe9ab2f?w=400&h=400&fit=crop", 
            12.00, "Extras", restaurantId));
        items.add(new FoodItem(108, "Coca-Cola 500ml",
            "Refresco Coca-Cola", 
            "https://images.unsplash.com/photo-1554866585-cd94860890b7?w=400&h=400&fit=crop", 
            6.50, "Bebidas", restaurantId));
        return items;
    }

    private static List<FoodItem> getBurgerMenu(int restaurantId) {
        List<FoodItem> items = new ArrayList<>();
        items.add(new FoodItem(201, "Classic Burger",
            "Carne de res, lechuga, tomate, cebolla y salsa especial", 
            "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&h=400&fit=crop", 
            22.00, "Clásicas", restaurantId, true));
        items.add(new FoodItem(202, "Cheese Burger",
            "Carne de res con doble queso cheddar", 
            "https://images.unsplash.com/photo-1550547660-d9450f859349?w=400&h=400&fit=crop", 
            26.00, "Clásicas", restaurantId, true));
        items.add(new FoodItem(203, "Bacon Burger",
            "Carne de res, bacon crujiente y queso suizo", 
            "https://images.unsplash.com/photo-1586816001966-79b736744398?w=400&h=400&fit=crop", 
            32.00, "Premium", restaurantId, true));
        items.add(new FoodItem(204, "Mushroom Burger",
            "Carne de res con champiñones salteados", 
            "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=400&fit=crop", 
            28.00, "Premium", restaurantId));
        items.add(new FoodItem(205, "Veggie Burger",
            "Hamburguesa de lentejas con aguacate", 
            "https://images.unsplash.com/photo-1544025162-d76694265947?w=400&h=400&fit=crop", 
            24.00, "Vegetarianas", restaurantId));
        items.add(new FoodItem(206, "Papas Fritas",
            "Porción grande con sal de mar", 
            "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400&h=400&fit=crop", 
            11.00, "Extras", restaurantId));
        items.add(new FoodItem(207, "Onion Rings",
            "Aros de cebolla crujientes", 
            "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=400&h=400&fit=crop", 
            13.00, "Extras", restaurantId));
        items.add(new FoodItem(208, "Malteada",
            "Chocolate, vainilla o fresa", 
            "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=400&h=400&fit=crop", 
            14.00, "Bebidas", restaurantId));
        return items;
    }

    private static List<FoodItem> getSushiMenu(int restaurantId) {
        List<FoodItem> items = new ArrayList<>();
        items.add(new FoodItem(301, "California Roll",
            "8 piezas: cangrejo, aguacate y pepino", 
            "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=400&h=400&fit=crop", 
            42.00, "Rolls", restaurantId, true));
        items.add(new FoodItem(302, "Philadelphia Roll",
            "8 piezas: salmón, queso crema y aguacate", 
            "https://images.unsplash.com/photo-1611143669185-af224c5e3252?w=400&h=400&fit=crop", 
            48.00, "Rolls", restaurantId, true));
        items.add(new FoodItem(303, "Dragon Roll",
            "8 piezas: anguila, aguacate y camarón tempura", 
            "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=400&h=400&fit=crop", 
            68.00, "Rolls Premium", restaurantId, true));
        items.add(new FoodItem(304, "Nigiri Salmón",
            "2 piezas de salmón fresco", 
            "https://images.unsplash.com/photo-1611143669185-af224c5e3252?w=400&h=400&fit=crop", 
            24.00, "Nigiri", restaurantId));
        items.add(new FoodItem(305, "Nigiri Atún",
            "2 piezas de atún fresco", 
            "https://images.unsplash.com/photo-1611143669185-af224c5e3252?w=400&h=400&fit=crop", 
            28.00, "Nigiri", restaurantId));
        items.add(new FoodItem(306, "Sashimi Mixto",
            "12 piezas variadas", 
            "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=400&h=400&fit=crop", 
            72.00, "Sashimi", restaurantId));
        items.add(new FoodItem(307, "Edamame",
            "Con sal de mar", 
            "https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=400&h=400&fit=crop", 
            14.00, "Entradas", restaurantId));
        items.add(new FoodItem(308, "Té Verde",
            "Té verde japonés caliente", 
            "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&h=400&fit=crop", 
            8.00, "Bebidas", restaurantId));
        return items;
    }

    private static List<FoodItem> getMexicanMenu(int restaurantId) {
        List<FoodItem> items = new ArrayList<>();
        
        items.add(new FoodItem(401, "Tacos al Pastor",
            "3 tacos con piña, cebolla y cilantro", 
            "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=400&h=400&fit=crop", 
            24.00, "Tacos", restaurantId, true));
        items.add(new FoodItem(402, "Tacos de Carnitas",
            "3 tacos con carnitas y salsa verde", 
            "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&h=400&q=80", 
            26.00, "Tacos", restaurantId, true));
        items.add(new FoodItem(403, "Burrito Supremo",
            "Carne, frijoles, arroz, queso y crema", 
            "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&h=400&q=80", 
            32.00, "Burritos", restaurantId, true));
        items.add(new FoodItem(404, "Quesadilla",
            "Con queso oaxaca y champiñones", 
            "https://images.unsplash.com/photo-1513456852971-30c0b8199d4d?w=400&h=400&fit=crop", 
            20.00, "Antojitos", restaurantId));
        items.add(new FoodItem(405, "Nachos con Carne",
            "Totopos con carne, queso y jalapeños", 
            "https://images.unsplash.com/photo-1513456852971-30c0b8199d4d?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&h=400&q=80", 
            28.00, "Antojitos", restaurantId));
        items.add(new FoodItem(406, "Guacamole",
            "Con totopos caseros", 
            "https://images.unsplash.com/photo-1588168333986-5078d3ae3976?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&h=400&q=80", 
            18.00, "Entradas", restaurantId));
        items.add(new FoodItem(407, "Agua de Horchata",
            "Vaso grande", 
            "https://images.unsplash.com/photo-1544145945-f90425340c7e?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&h=400&q=80", 
            8.00, "Bebidas", restaurantId));
        items.add(new FoodItem(408, "Jamaica",
            "Vaso grande", 
            "https://images.unsplash.com/photo-1544145945-f90425340c7e?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&h=400&q=80", 
            8.00, "Bebidas", restaurantId));
        return items;
    }

    private static List<FoodItem> getChineseMenu(int restaurantId) {
        List<FoodItem> items = new ArrayList<>();
        items.add(new FoodItem(501, "Arroz Frito Especial",
            "Con pollo, camarón, huevo y verduras", 
            "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400&h=400&fit=crop", 
            28.00, "Arroces", restaurantId, true));
        items.add(new FoodItem(502, "Pollo Agridulce",
            "Pollo empanizado con salsa agridulce", 
            "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400&h=400&fit=crop", 
            32.00, "Platillos", restaurantId, true));
        items.add(new FoodItem(503, "Chow Mein",
            "Tallarines salteados con verduras", 
            "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&h=400&fit=crop", 
            26.00, "Tallarines", restaurantId, true));
        items.add(new FoodItem(504, "Cerdo en Salsa Negra",
            "Cerdo con verduras en salsa de soya", 
            "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400&h=400&fit=crop", 
            34.00, "Platillos", restaurantId));
        items.add(new FoodItem(505, "Rollos Primavera",
            "4 piezas con salsa agridulce", 
            "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400&h=400&fit=crop", 
            16.00, "Entradas", restaurantId));
        items.add(new FoodItem(506, "Wonton Frito",
            "6 piezas", 
            "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400&h=400&fit=crop", 
            18.00, "Entradas", restaurantId));
        items.add(new FoodItem(507, "Sopa Wonton",
            "Sopa con dumplings de cerdo", 
            "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400&h=400&fit=crop", 
            14.00, "Sopas", restaurantId));
        items.add(new FoodItem(508, "Té de Jazmín",
            "Tetera individual", 
            "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&h=400&fit=crop", 
            8.00, "Bebidas", restaurantId));
        return items;
    }

    private static List<FoodItem> getDessertMenu(int restaurantId) {
        List<FoodItem> items = new ArrayList<>();
        items.add(new FoodItem(601, "Pastel de Chocolate",
            "Rebanada con ganache de chocolate", 
            "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=400&fit=crop", 
            18.00, "Pasteles", restaurantId, true));
        items.add(new FoodItem(602, "Cheesecake",
            "New York style con frutos rojos", 
            "https://images.unsplash.com/photo-1524351199678-941a58a3df50?w=400&h=400&fit=crop", 
            22.00, "Pasteles", restaurantId, true));
        items.add(new FoodItem(603, "Brownie con Helado",
            "Brownie tibio con helado de vainilla", 
            "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=400&h=400&fit=crop", 
            24.00, "Especiales", restaurantId, true));
        items.add(new FoodItem(604, "Tiramisú",
            "Receta italiana tradicional", 
            "https://images.unsplash.com/photo-1571877227200-a0d98ea4ee82?w=400&h=400&fit=crop", 
            20.00, "Clásicos", restaurantId));
        items.add(new FoodItem(605, "Flan Napolitano",
            "Con caramelo casero", 
            "https://images.unsplash.com/photo-1621303837174-89787a7d4729?w=400&h=400&fit=crop", 
            12.00, "Clásicos", restaurantId));
        items.add(new FoodItem(606, "Helado Artesanal",
            "2 bolas del sabor elegido", 
            "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400&h=400&fit=crop", 
            14.00, "Helados", restaurantId));
        items.add(new FoodItem(607, "Frappé de Café",
            "Con crema batida", 
            "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400&h=400&fit=crop", 
            16.00, "Bebidas", restaurantId));
        items.add(new FoodItem(608, "Chocolate Caliente",
            "Con malvaviscos", 
            "https://images.unsplash.com/photo-1517487881594-2787fef5ebf7?w=400&h=400&fit=crop", 
            12.00, "Bebidas", restaurantId));
        return items;
    }
}




