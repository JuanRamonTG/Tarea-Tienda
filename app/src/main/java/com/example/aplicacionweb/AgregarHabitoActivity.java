package com.example.aplicacionweb;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AgregarHabitoActivity extends AppCompatActivity {

    private static final String TAG = "AgregarHabitoActivity";

    private EditText etNombreHabito, etDescripcionHabito;
    private Button btnGuardarHabito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_habito);

        Log.d(TAG, "onCreate: Iniciando AgregarHabitoActivity");

        etNombreHabito = findViewById(R.id.etNombreHabito);
        etDescripcionHabito = findViewById(R.id.etDescripcionHabito);
        btnGuardarHabito = findViewById(R.id.btnGuardarHabito);

        btnGuardarHabito.setOnClickListener(v -> {
            String nombreHabito = etNombreHabito.getText().toString().trim();
            String descripcion = etDescripcionHabito.getText().toString().trim();

            if (!nombreHabito.isEmpty()) {
                Log.d(TAG, "btnGuardarHabito: Hábito ingresado = " + nombreHabito + ", Descripción = " + descripcion);
                // Aquí puedes guardar los datos en una base de datos, archivo o preferencias
                Toast.makeText(this, "Hábito agregado: " + nombreHabito, Toast.LENGTH_SHORT).show();
                finish(); // Cierra la actividad
            } else {
                Log.w(TAG, "btnGuardarHabito: El campo nombre del hábito está vacío");
                Toast.makeText(this, "Por favor, ingresa el nombre del hábito.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
