package com.example.aplicacionweb;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrarPasosActivity extends AppCompatActivity {

    private static final String TAG = "RegistrarPasosActivity";

    private EditText etPasos;
    private Button btnGuardarPasos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_pasos);

        Log.d(TAG, "onCreate: Iniciando RegistrarPasosActivity");

        etPasos = findViewById(R.id.etPasos);
        btnGuardarPasos = findViewById(R.id.btnGuardarPasos);

        btnGuardarPasos.setOnClickListener(v -> {
            String pasos = etPasos.getText().toString().trim();

            if (!pasos.isEmpty()) {
                Log.d(TAG, "btnGuardarPasos: Pasos ingresados = " + pasos);
                // Aquí guardarías los pasos en la base de datos, SharedPreferences, etc.
                Toast.makeText(this, "Pasos registrados: " + pasos, Toast.LENGTH_SHORT).show();
                finish(); // Cierra la actividad y vuelve a la anterior
            } else {
                Log.w(TAG, "btnGuardarPasos: Campo de pasos vacío");
                Toast.makeText(this, "Por favor, ingresa el número de pasos.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
