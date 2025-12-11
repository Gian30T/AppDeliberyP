package com.zipp.delivery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.view.LayoutInflater;

import com.zipp.delivery.R;
import com.zipp.delivery.network.ApiClient;
import com.zipp.delivery.network.request.ActualizarPerfilRequest;
import com.zipp.delivery.network.response.AuthResponse;
import com.zipp.delivery.utils.CartManager;
import com.zipp.delivery.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    
    private ImageView ivBack, ivEdit;
    private TextView tvUserInitials, tvUserName, tvUserEmail;
    private LinearLayout optionOrders, optionFavorites, optionAddresses, optionSettings, optionHelp, optionLogout;

    private SessionManager sessionManager;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        apiClient = ApiClient.getInstance(this);

        initViews();
        setupUserInfo();
        setupClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivEdit = findViewById(R.id.iv_edit);
        tvUserInitials = findViewById(R.id.tv_user_initials);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        optionOrders = findViewById(R.id.option_orders);
        optionFavorites = findViewById(R.id.option_favorites);
        optionAddresses = findViewById(R.id.option_addresses);
        optionSettings = findViewById(R.id.option_settings);
        optionHelp = findViewById(R.id.option_help);
        optionLogout = findViewById(R.id.option_logout);
    }

    private void setupUserInfo() {
        // Obtener datos del SessionManager
        String nombreCompleto = sessionManager.getNombreCompleto();
        String email = sessionManager.getUserEmail();
        
        tvUserName.setText(nombreCompleto);
        tvUserEmail.setText(email);
        
        // Generar iniciales
        String initials = getInitials(nombreCompleto);
        tvUserInitials.setText(initials);
    }
    
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) {
            return "U";
        }
        String[] parts = name.trim().split(" ");
        if (parts.length >= 2) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase() + 
                   String.valueOf(parts[1].charAt(0)).toUpperCase();
        }
        return String.valueOf(name.charAt(0)).toUpperCase();
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        if (ivEdit != null) {
            ivEdit.setOnClickListener(v -> showEditProfileDialog());
        }

        optionOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, OrdersActivity.class));
        });

        optionFavorites.setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritesActivity.class));
        });

        optionAddresses.setOnClickListener(v -> {
            Toast.makeText(this, "Direcciones - Próximamente", Toast.LENGTH_SHORT).show();
        });

        optionSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Configuración - Próximamente", Toast.LENGTH_SHORT).show();
        });

        optionHelp.setOnClickListener(v -> {
            Toast.makeText(this, "Ayuda - Próximamente", Toast.LENGTH_SHORT).show();
        });

        optionLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí, salir", (dialog, which) -> {
                performLogout();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
    
    private void performLogout() {
        Log.d(TAG, "Iniciando logout...");
        
        // 1. Limpiar carrito
        CartManager.getInstance().clearCart();
        Log.d(TAG, "Carrito limpiado");
        
        // 2. Resetear cliente API (para limpiar el token del interceptor)
        ApiClient.resetInstance();
        
        // 3. Cerrar sesión
        sessionManager.logout();
        Log.d(TAG, "Sesión cerrada. isLoggedIn = " + sessionManager.isLoggedIn());
        
        // 4. Mostrar mensaje
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        
        // 5. Ir al Login
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        
        // 6. Finalizar esta actividad
        finish();
    }

    private void showEditProfileDialog() {
        // Crear diálogo personalizado
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_profile);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        EditText etNombre = dialog.findViewById(R.id.et_nombre);
        EditText etApellido = dialog.findViewById(R.id.et_apellido);
        EditText etTelefono = dialog.findViewById(R.id.et_telefono);
        android.widget.Button btnCancelar = dialog.findViewById(R.id.btn_cancelar);
        android.widget.Button btnGuardar = dialog.findViewById(R.id.btn_guardar);
        ProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        
        // Cargar datos actuales
        String[] nombreCompleto = sessionManager.getNombreCompleto().split(" ", 2);
        etNombre.setText(nombreCompleto.length > 0 ? nombreCompleto[0] : "");
        etApellido.setText(nombreCompleto.length > 1 ? nombreCompleto[1] : "");
        etTelefono.setText(sessionManager.getUserPhone());
        
        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        
        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            
            // Validaciones
            if (TextUtils.isEmpty(nombre)) {
                etNombre.setError("El nombre es requerido");
                etNombre.requestFocus();
                return;
            }
            
            // Actualizar perfil
            actualizarPerfil(nombre, apellido, telefono, dialog, progressBar, btnGuardar);
        });
        
        dialog.show();
    }
    
    private void actualizarPerfil(String nombre, String apellido, String telefono, 
                                   Dialog dialog, ProgressBar progressBar, android.widget.Button btnGuardar) {
        progressBar.setVisibility(android.view.View.VISIBLE);
        btnGuardar.setEnabled(false);
        
        long userId = sessionManager.getUserId();
        ActualizarPerfilRequest request = new ActualizarPerfilRequest(nombre, apellido, telefono);
        
        apiClient.getUsuariosApi().actualizarUsuario(userId, request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressBar.setVisibility(android.view.View.GONE);
                btnGuardar.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse.UsuarioApi usuario = response.body().getUsuario();
                    
                    if (usuario != null) {
                        // Actualizar SessionManager con los nuevos datos
                        sessionManager.updateUserInfo(
                            usuario.getNombre(),
                            usuario.getApellido(),
                            usuario.getEmail(),
                            usuario.getNumeroTelefono()
                        );
                        
                        // Actualizar UI
                        setupUserInfo();
                        
                        // Cerrar diálogo
                        dialog.dismiss();
                        
                        Toast.makeText(ProfileActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Perfil actualizado: " + usuario.getNombre() + " " + usuario.getApellido());
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Error al actualizar perfil";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError();
                    }
                    Toast.makeText(ProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al actualizar perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressBar.setVisibility(android.view.View.GONE);
                btnGuardar.setEnabled(true);
                Toast.makeText(ProfileActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error de conexión al actualizar perfil", t);
            }
        });
    }
}
