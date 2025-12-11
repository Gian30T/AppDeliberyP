package com.zipp.delivery.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Gestiona los restaurantes favoritos del usuario usando SharedPreferences
 */
public class FavoritesManager {
    private static final String PREF_NAME = "FavoritesPrefs";
    private static final String KEY_FAVORITES = "favorite_restaurants";
    private static final String TAG = "FavoritesManager";
    
    private SharedPreferences prefs;
    private Gson gson;
    
    private static FavoritesManager instance;
    
    private FavoritesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    public static synchronized FavoritesManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoritesManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Agregar un restaurante a favoritos
     */
    public void addFavorite(String restaurantName) {
        Set<String> favorites = getFavoriteNames();
        favorites.add(restaurantName);
        saveFavorites(favorites);
        Log.d(TAG, "Restaurante agregado: " + restaurantName);
    }
    
    /**
     * Eliminar un restaurante de favoritos
     */
    public void removeFavorite(String restaurantName) {
        Set<String> favorites = getFavoriteNames();
        favorites.remove(restaurantName);
        saveFavorites(favorites);
        Log.d(TAG, "Restaurante eliminado: " + restaurantName);
    }
    
    /**
     * Verificar si un restaurante es favorito
     */
    public boolean isFavorite(String restaurantName) {
        Set<String> favorites = getFavoriteNames();
        return favorites.contains(restaurantName);
    }
    
    /**
     * Obtener todos los nombres de restaurantes favoritos
     */
    public Set<String> getFavoriteNames() {
        String json = prefs.getString(KEY_FAVORITES, "[]");
        Type type = new TypeToken<Set<String>>(){}.getType();
        Set<String> favorites = gson.fromJson(json, type);
        
        if (favorites == null) {
            favorites = new HashSet<>();
        }
        
        return favorites;
    }
    
    /**
     * Obtener la cantidad de favoritos
     */
    public int getFavoriteCount() {
        return getFavoriteNames().size();
    }
    
    /**
     * Limpiar todos los favoritos
     */
    public void clearAll() {
        prefs.edit().remove(KEY_FAVORITES).apply();
        Log.d(TAG, "Todos los favoritos eliminados");
    }
    
    /**
     * Guardar favoritos en SharedPreferences
     */
    private void saveFavorites(Set<String> favorites) {
        String json = gson.toJson(favorites);
        prefs.edit().putString(KEY_FAVORITES, json).apply();
    }
}


