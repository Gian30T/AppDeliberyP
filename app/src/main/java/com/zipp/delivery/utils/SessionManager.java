package com.zipp.delivery.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zipp.delivery.models.User;
import com.zipp.delivery.network.response.AuthResponse;

public class SessionManager {
    private static final String PREF_NAME = "ZippSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_APELLIDO = "userApellido";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_ROL = "userRol";
    private static final String KEY_TOKEN = "token";

    private SharedPreferences pref;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Crear sesión con respuesta de la API
     */
    public void createLoginSession(AuthResponse.UsuarioApi usuario, String token) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, usuario.getId());
        editor.putString(KEY_USER_NAME, usuario.getNombre());
        editor.putString(KEY_USER_APELLIDO, usuario.getApellido());
        editor.putString(KEY_USER_EMAIL, usuario.getEmail());
        editor.putString(KEY_USER_PHONE, usuario.getNumeroTelefono());
        editor.putString(KEY_USER_ROL, usuario.getRolUsuario());
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    /**
     * Crear sesión con modelo User (para compatibilidad)
     */
    public void createLoginSession(User user) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.commit();
    }

    /**
     * Verificar si hay sesión activa
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Obtener token JWT
     */
    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    /**
     * Verificar si tiene token válido
     */
    public boolean hasValidToken() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    /**
     * Obtener ID del usuario
     */
    public long getUserId() {
        return pref.getLong(KEY_USER_ID, 0);
    }

    /**
     * Obtener modelo User
     */
    public User getUser() {
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setId((int) pref.getLong(KEY_USER_ID, 0));
        user.setName(getNombreCompleto());
        user.setEmail(pref.getString(KEY_USER_EMAIL, ""));
        user.setPhone(pref.getString(KEY_USER_PHONE, ""));
        return user;
    }

    /**
     * Obtener nombre del usuario
     */
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "Usuario");
    }

    /**
     * Obtener apellido del usuario
     */
    public String getUserApellido() {
        return pref.getString(KEY_USER_APELLIDO, "");
    }

    /**
     * Obtener nombre completo
     */
    public String getNombreCompleto() {
        String nombre = getUserName();
        String apellido = getUserApellido();
        if (apellido != null && !apellido.isEmpty()) {
            return nombre + " " + apellido;
        }
        return nombre;
    }

    /**
     * Obtener email del usuario
     */
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    /**
     * Obtener teléfono del usuario
     */
    public String getUserPhone() {
        return pref.getString(KEY_USER_PHONE, "");
    }

    /**
     * Obtener rol del usuario
     */
    public String getUserRol() {
        return pref.getString(KEY_USER_ROL, "cliente");
    }

    /**
     * Cerrar sesión
     */
    public void logout() {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Actualizar token
     */
    public void updateToken(String token) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    /**
     * Actualizar información del usuario
     */
    public void updateUserInfo(String nombre, String apellido, String email, String telefono) {
        SharedPreferences.Editor editor = pref.edit();
        if (nombre != null) editor.putString(KEY_USER_NAME, nombre);
        if (apellido != null) editor.putString(KEY_USER_APELLIDO, apellido);
        if (email != null) editor.putString(KEY_USER_EMAIL, email);
        if (telefono != null) editor.putString(KEY_USER_PHONE, telefono);
        editor.commit();
    }
}
