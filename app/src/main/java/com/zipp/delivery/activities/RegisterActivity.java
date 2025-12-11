package com.zipp.delivery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zipp.delivery.R;
import com.zipp.delivery.network.ApiClient;
import com.zipp.delivery.network.request.RegistroRequest;
import com.zipp.delivery.network.response.AuthResponse;
import com.zipp.delivery.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etName, etLastName, etEmail, etPhone, etPassword, etConfirmPassword;
    private TextInputLayout tilName, tilLastName, tilEmail, tilPhone, tilPassword, tilConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);
        apiClient = ApiClient.getInstance(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        etName = findViewById(R.id.et_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        tilName = findViewById(R.id.til_name);
        tilLastName = findViewById(R.id.til_last_name);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        
        // ProgressBar si existe
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        btnRegister.setOnClickListener(v -> attemptRegister());

        tvLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Reset errors
        tilName.setError(null);
        tilLastName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Ingresa tu nombre");
            valid = false;
        }

        if (TextUtils.isEmpty(lastName)) {
            tilLastName.setError("Ingresa tu apellido");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Ingresa tu correo electrónico");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Correo electrónico inválido");
            valid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Ingresa tu teléfono");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Ingresa una contraseña");
            valid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres");
            valid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Confirma tu contraseña");
            valid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Las contraseñas no coinciden");
            valid = false;
        }

        if (valid) {
            performRegister(name, lastName, email, phone, password);
        }
    }

    private void performRegister(String name, String lastName, String email, String phone, String password) {
        setLoading(true);
        
        RegistroRequest request = new RegistroRequest(name, lastName, email, password, phone);
        
        apiClient.getAuthApi().registrar(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    if (authResponse.isSuccess()) {
                        // Guardar sesión con token
                        sessionManager.createLoginSession(
                            authResponse.getUsuario(),
                            authResponse.getToken()
                        );
                        
                        Toast.makeText(RegisterActivity.this, 
                            "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show();
                        
                        // Ir al MainActivity
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        showError(authResponse.getError());
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoading(false);
                showError("Error de conexión: " + t.getMessage());
            }
        });
    }
    
    private void handleErrorResponse(Response<AuthResponse> response) {
        try {
            if (response.code() == 400) {
                showError("El email ya está registrado");
            } else if (response.code() == 404) {
                showError("Servicio no disponible");
            } else {
                showError("Error del servidor: " + response.code());
            }
        } catch (Exception e) {
            showError("Error al procesar respuesta");
        }
    }
    
    private void setLoading(boolean loading) {
        btnRegister.setEnabled(!loading);
        btnRegister.setText(loading ? "Registrando..." : getString(R.string.register));
        
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
