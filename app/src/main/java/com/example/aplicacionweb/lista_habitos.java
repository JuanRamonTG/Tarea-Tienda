package com.example.aplicacionweb;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class lista_habitos extends Activity {
    Bundle parametros = new Bundle();
    ListView ltsHabitos;
    Cursor cHabitos;
    DB db;
    final ArrayList<habitos> alHabitos = new ArrayList<habitos>();
    final ArrayList<habitos> alHabitosCopia = new ArrayList<habitos>();
    JSONArray jsonArray = new JSONArray();
    JSONObject jsonObject;
    habitos misHabitos;
    FloatingActionButton fab;
    int posicion = 0;
    DatabaseReference databaseReference;
    String miToken = "";
    detectarInternet di;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_habitos);

        ltsHabitos = findViewById(R.id.ltsHabitos);
        parametros.putString("accion", "nuevo");

        fab = findViewById(R.id.fabAgregarHabitos);
        fab.setOnClickListener(view -> abriVentana());
        listarDatos();
        buscarHabitos();
        mostrarChats();
    }
    private void mostrarChats(){
        ltsHabitos.setOnItemClickListener( (parent, view, position, id)->{
            try{
                Bundle parametros = new Bundle();
                parametros.putString("nombre", jsonArray.getJSONObject(position).getString("nombre"));
                parametros.putString("to", jsonArray.getJSONObject(position).getString("to"));
                parametros.putString("from", jsonArray.getJSONObject(position).getString("from"));
                parametros.putString("urlFoto", jsonArray.getJSONObject(position).getString("urlFoto"));
                parametros.putString("urlCompletaFotoFirestore", jsonArray.getJSONObject(position).getString("urlCompletaFotoFirestore"));

                Intent intent = new Intent(getApplicationContext(), chats.class);
                intent.putExtras(parametros);
                startActivity(intent);
            }catch (Exception e){
                mostrarMsg("Error al abrir el chat: " + e.getMessage());
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            posicion = info.position;
            menu.setHeaderTitle(jsonArray.getJSONObject(posicion).getJSONObject("value").getString("nombre"));
        } catch (Exception e) {
            mostrarMsg("Error crear el menu: " + e.getMessage());
        }
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try{
            if( item.getItemId()==R.id.mnxNuevo){
                abriVentana();
            }else if( item.getItemId()==R.id.mnxModificar){
                parametros.putString("accion", "modificar");
                parametros.putString("habitos", jsonArray.getJSONObject(posicion).getJSONObject("value").toString());
                abriVentana();
            } else if (item.getItemId()==R.id.mnxEliminar) {
                eliminarHabito();
            }
            return true;
        }catch (Exception e){
            mostrarMsg("Error al mostrar el menu: " + e.getMessage());
            return super.onContextItemSelected(item);
        }
    }
    private void eliminarHabito(){
        try{
            String nombre = jsonArray.getJSONObject(posicion).getJSONObject("value").getString("nombre");
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
            confirmacion.setTitle("Esta seguro de eliminar a: ");
            confirmacion.setMessage(nombre);
            confirmacion.setPositiveButton("Si", (dialog, which) -> {
                try {
                    di = new detectarInternet(this);
                    if(di.hayConexionInternet()){//online
                        JSONObject datosHabitos = new JSONObject();
                        String _id = jsonArray.getJSONObject(posicion).getJSONObject("value").getString("_id");
                        String _rev = jsonArray.getJSONObject(posicion).getJSONObject("value").getString("_rev");
                        String url = utilidades.url_mto + "/" + _id + "?rev=" + _rev;
                        enviarDatosServidor objEnviarDatosServidor = new enviarDatosServidor(this);
                        String respuesta = objEnviarDatosServidor.execute(datosHabitos.toString(), "DELETE", url).get();
                        JSONObject respuestaJSON = new JSONObject(respuesta);
                        if(!respuestaJSON.getBoolean("ok")) {
                            mostrarMsg("Error al intentar eliminar: " + respuesta);
                        }
                    }
                    String respuesta = db.administrar_habitos("eliminar", new String[]{jsonArray.getJSONObject(posicion).getJSONObject("value").getString("idHabito")});
                    if(respuesta.equals("ok")) {
                        listarDatos();
                        mostrarMsg("Registro eliminado con exito");
                    }else{
                        mostrarMsg("Error al eliminar: " + respuesta);
                    }
                }catch (Exception e){
                    mostrarMsg("Error en eliminar: " + e.getMessage());
                }
            });
            confirmacion.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });
            confirmacion.create().show();
        }catch (Exception e){
            mostrarMsg("Error eliminar: " + e.getMessage());
        }
    }
    private void abriVentana(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(parametros);
        startActivity(intent);
    }
    private void listarDatos(){
        try{
            databaseReference  = FirebaseDatabase.getInstance().getReference("habitos");
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tarea->{
                if(!tarea.isSuccessful()){
                    mostrarMsg("Error al obtener token: "+tarea.getException().getMessage());
                    return;
                }else{
                    miToken = tarea.getResult();
                    if( miToken!=null && miToken.length()>0 ){
                        databaseReference.orderByChild("token").equalTo(miToken).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try{
                                    if( snapshot.getChildrenCount()<=0 ){
                                        mostrarMsg("No hay registros.");
                                        parametros.putString("accion", "nuevo");
                                        abriVentana();
                                    }
                                }catch (Exception e){
                                    mostrarMsg("Error al llamar la ventana: " + e.getMessage());
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                mostrarMsg("Error se cancelo: " + error.getMessage());
                            }
                        });
                    }
                }
            });
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        for( DataSnapshot dataSnapshot : snapshot.getChildren() ){
                            habitos habito = dataSnapshot.getValue(habitos.class);

                            jsonObject = new JSONObject();
                            jsonObject.put("idHabito", habito.getIdHabito());
                            jsonObject.put("nombre", habito.getNombre());
                            jsonObject.put("direccion", habito.getDireccion());
                            jsonObject.put("telefono", habito.getTelefono());
                            jsonObject.put("email", habito.getEmail());
                            jsonObject.put("dui", habito.getDui());
                            jsonObject.put("urlFoto", habito.getFoto());
                            jsonObject.put("urlCompletaFotoFirestore", habito.getUrlCompletaFotoFirestore());
                            jsonObject.put("to", habito.getToken());
                            jsonObject.put("from", miToken);
                            jsonArray.put(jsonObject);
                        }
                        mostrarDatosHabitos();
                    }catch (Exception e){
                        mostrarMsg("Error al escuchar evento de firebase: " + e.getMessage());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){
            mostrarMsg("Error al listar datos: " + e.getMessage());
        }
    }
    private void mostrarDatosHabitos(){
        try{
            if(jsonArray.length()>0){
                alHabitos.clear();
                alHabitosCopia.clear();

                for (int i=0; i<jsonArray.length(); i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    misHabitos = new habitos(
                            jsonObject.getString("idHabito"),
                            jsonObject.getString("nombre"),
                            jsonObject.getString("direccion"),
                            jsonObject.getString("telefono"),
                            jsonObject.getString("email"),
                            jsonObject.getString("dui"),
                            jsonObject.getString("urlFoto"),
                            jsonObject.getString("urlCompletaFotoFirestore"),
                            jsonObject.getString("to")
                    );
                    alHabitos.add(misHabitos);
                }
                alHabitosCopia.addAll(alHabitos);
                ltsHabitos.setAdapter(new AdaptadorHabitos(this, alHabitos));
                registerForContextMenu(ltsHabitos);
            }else{
                mostrarMsg("No hay registrados.");
                abriVentana();
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar: " + e.getMessage());
        }
    }
    private void buscarHabitos(){
        TextView tempVal = findViewById(R.id.txtBuscarHabitos);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alHabitos.clear();
                String buscar = tempVal.getText().toString().trim().toLowerCase();
                if( buscar.length()<=0){
                    alHabitos.addAll(alHabitosCopia);
                }else{
                    for (habitos item: alHabitosCopia){
                        if(item.getNombre().toLowerCase().contains(buscar) ||
                                item.getDui().toLowerCase().contains(buscar) ||
                                item.getEmail().toLowerCase().contains(buscar)){
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
    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}