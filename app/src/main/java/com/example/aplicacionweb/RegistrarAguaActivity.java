package com.example.aplicacionweb;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrarAguaActivity extends AppCompatActivity {

    private EditText etLitros;
    private Button btnGuardarAgua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_agua);

        etLitros = findViewById(R.id.etLitros);
        btnGuardarAgua = findViewById(R.id.btnGuardarAgua);

        btnGuardarAgua.setOnClickListener(v -> {
            String litros = etLitros.getText().toString().trim();
            if (!litros.isEmpty()) {
                // Aquí guardamos la cantidad de agua (en SQLite, SharedPreferences, o lo que necesites)
                Toast.makeText(RegistrarAguaActivity.this, "Agua registrada: " + litros + " litros", Toast.LENGTH_SHORT).show();
                finish(); // Volver a la pantalla principal
            } else {
                Toast.makeText(RegistrarAguaActivity.this, "Por favor, ingresa la cantidad de agua.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
