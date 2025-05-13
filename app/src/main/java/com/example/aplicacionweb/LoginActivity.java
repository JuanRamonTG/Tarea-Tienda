package com.example.aplicacionweb;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    // Credenciales de prueba
    private static final String TEST_EMAIL = "usuario@habittracker.com";
    private static final String TEST_PASSWORD = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Activity creada");

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked");
            attemptLogin();
        });

        tvRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad de registro no implementada", Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInputs(email, password)) {
            return;
        }

        if (authenticateUser(email, password)) {
            navigateToMainActivity();
        } else {
            showLoginError();
        }
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingresa tu correo electrónico");
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingresa tu contraseña");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean authenticateUser(String email, String password) {
        return email.equals(TEST_EMAIL) && password.equals(TEST_PASSWORD);
    }

    private void navigateToMainActivity() {
        try {
            Log.d(TAG, "Iniciando MainActivity...");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.d(TAG, "MainActivity iniciada correctamente");
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error al iniciar MainActivity: " + e.getMessage(), e);
            Toast.makeText(this, "Error al redirigir: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showLoginError() {
        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "Intento de inicio de sesión fallido");
    }
}