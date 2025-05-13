package com.example.aplicacionweb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class lista_habito extends Activity {
    Bundle parametros = new Bundle();
    ListView ltsHabitos;
    Cursor cHabitos;
    DB db;
    final ArrayList<habitos> alHabitos = new ArrayList<habitos>();
    final ArrayList<habitos> alHabitosCopia = new ArrayList<habitos>();
    JSONArray jsonArray;
    JSONObject jsonObject;
    habitos misHabitos;
    FloatingActionButton fab;
    int posicion = 0;
    obtenerDatosServidor datosServidor;
    detectarInternet di;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_habitos);

        parametros.putString("accion", "nuevo");
        db = new DB(this);

        fab = findViewById(R.id.fabAgregarHabitos);
        fab.setOnClickListener(view -> abriVentana());
        listarDatos();
        buscarHabitos();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            posicion = info.position;
            menu.setHeaderTitle(jsonArray.getJSONObject(posicion).getJSONObject("value").getString("nombre"));
        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            if (item.getItemId() == R.id.mnxNuevo) {
                abriVentana();
            } else if (item.getItemId() == R.id.mnxModificar) {
                parametros.putString("accion", "modificar");
                parametros.putString("habitos", jsonArray.getJSONObject(posicion).getJSONObject("value").toString());
                abriVentana();
            } else if (item.getItemId() == R.id.mnxEliminar) {
                eliminarProducto();
            }
            return true;
        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
            return super.onContextItemSelected(item);
        }
    }
    private void eliminarProducto() {
        try {
            String nombre = jsonArray.getJSONObject(posicion).getJSONObject("value").getString("nombre");
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
            confirmacion.setTitle("¿Está seguro de eliminar?");
            confirmacion.setMessage(nombre);
            confirmacion.setPositiveButton("Sí", (dialog, which) -> {
                try {
                    di = new detectarInternet(this);
                    if (di.hayConexionInternet()) { //online
                        JSONObject datosProducto = new JSONObject();
                        String _id = jsonArray.getJSONObject(posicion).getJSONObject("value").getString("_id");
                        String _rev = jsonArray.getJSONObject(posicion).getJSONObject("value").getString("_rev");
                        String url = utilidades.url_mto + "/" + _id + "?rev=" + _rev;
                        enviarDatosServidor objEnviarDatosServidor = new enviarDatosServidor(this);
                        String respuesta = objEnviarDatosServidor.execute(datosProducto.toString(), "DELETE", url).get();
                        JSONObject respuestaJSON = new JSONObject(respuesta);
                        if (!respuestaJSON.getBoolean("ok")) {
                            mostrarMsg("Error: " + respuesta);
                        }
                    }
                    String respuesta = db.administrar_habitos("eliminar",
                            new String[]{jsonArray.getJSONObject(posicion).getJSONObject("value").getString("idProducto")});
                    if (respuesta.equals("ok")) {
                        listarDatos();
                        mostrarMsg("Registro eliminado con éxito");
                    } else {
                        mostrarMsg("Error: " + respuesta);
                    }
                } catch (Exception e) {
                    mostrarMsg("Error: " + e.getMessage());
                }
            });
            confirmacion.setNegativeButton("No", (dialog, which) -> {dialog.dismiss();
            });
            confirmacion.create().show();
        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }
    private void abriVentana() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(parametros);
        startActivity(intent);
    }
    private void listarDatos() {
        try {
            di = new detectarInternet(this);
            if (di.hayConexionInternet()) { //online
                datosServidor = new obtenerDatosServidor();
                String respuesta = datosServidor.execute().get();
                jsonObject = new JSONObject(respuesta);
                jsonArray = jsonObject.getJSONArray("rows");
                mostrarDatosProductos();
            } else { //offline
                obtenerDatosLocales();
            }
        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }
    private void obtenerDatosLocales() {
        try {
            cHabitos = db.lista_habitos();
            if (cHabitos.moveToFirst()) {
                jsonArray = new JSONArray();
                do {
                    jsonObject = new JSONObject();
                    JSONObject value = new JSONObject();
                    value.put("idProducto", cHabitos.getString(0));
                    value.put("nombre", cHabitos.getString(1));
                    value.put("direccion", cHabitos.getString(2));
                    value.put("telefono", cHabitos.getString(3));
                    value.put("email", cHabitos.getString(4));
                    value.put("dui", cHabitos.getString(5));
                    value.put("urlFoto", cHabitos.getString(6));
                    jsonObject.put("value", value);
                    jsonArray.put(jsonObject);
                } while (cHabitos.moveToNext());
                mostrarDatosProductos();
            } else {
                mostrarMsg("No hay habitos registrados.");
                abriVentana();
            }
        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }
    private void mostrarDatosProductos() {
        try {
            if (jsonArray.length() > 0) {
                ltsHabitos = findViewById(R.id.ltsHabitos);
                alHabitos.clear();
                alHabitosCopia.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i).getJSONObject("value");
                    misHabitos = new habitos(
                            jsonObject.getString("idProducto"),
                            jsonObject.getString("nombre"),
                            jsonObject.getString("direccion"),
                            jsonObject.getString("telefono"),
                            jsonObject.getString("email"),
                            jsonObject.getString("dui"),
                            jsonObject.getString("urlFoto")
                    );
                    alHabitos.add(misHabitos);
                }
                alHabitosCopia.addAll(alHabitos);
                ltsHabitos.setAdapter(new AdaptadorHabitos(this, alHabitos));
                registerForContextMenu(ltsHabitos);
            } else {
                mostrarMsg("No hay habitos registrados.");
                abriVentana();
            }
        } catch (Exception e) {
            mostrarMsg("Error: " + e.getMessage());
        }
    }
    private void buscarHabitos() {
        TextView tempVal = findViewById(R.id.txtBuscarHabitos);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alHabitos.clear();
                String buscar = tempVal.getText().toString().trim().toLowerCase();
                if (buscar.length() <= 0) {
                    alHabitos.addAll(alHabitosCopia);
                } else {
                    for (habitos item : alHabitosCopia) {
                        if (item.getNombre().toLowerCase().contains(buscar) ||
                                item.getDui().toLowerCase().contains(buscar) ||
                                item.getEmail().toLowerCase().contains(buscar)) {
                            alHabitos.add(item);
                        }
                    }
                    ltsHabitos.setAdapter(new AdaptadorHabitos(getApplicationContext(), alHabitos));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void mostrarMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}