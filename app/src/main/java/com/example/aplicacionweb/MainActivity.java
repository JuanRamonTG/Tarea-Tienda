package com.example.aplicacionweb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btnPasos, btnAgua, btnHabitos, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Verifica que el nombre del layout sea correcto

        Log.d(TAG, "onCreate: MainActivity iniciada correctamente");
        Toast.makeText(this, "¡Bienvenido a la pantalla principal!", Toast.LENGTH_SHORT).show();

        btnPasos = findViewById(R.id.btnPasos);
        btnAgua = findViewById(R.id.btnAgua);
        btnHabitos = findViewById(R.id.btnHabitos);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnPasos.setOnClickListener(v -> {
            Log.d(TAG, "btnPasos: Navegando a RegistrarPasosActivity");
            startActivity(new Intent(MainActivity.this, RegistrarPasosActivity.class));
        });

        btnAgua.setOnClickListener(v -> {
            Log.d(TAG, "btnAgua: Navegando a RegistrarAguaActivity");
            startActivity(new Intent(MainActivity.this, RegistrarAguaActivity.class));
        });

        btnHabitos.setOnClickListener(v -> {
            Log.d(TAG, "btnHabitos: Navegando a AgregarHabitoActivity");
            startActivity(new Intent(MainActivity.this, AgregarHabitoActivity.class));
        });

        btnCerrarSesion.setOnClickListener(v -> {
            Log.d(TAG, "btnCerrarSesion: Cerrando sesión");
            finish(); // Finaliza la actividad actual
        });
    }
}
