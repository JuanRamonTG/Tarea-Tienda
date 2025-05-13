package com.example.aplicacionweb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class AdaptadorHabitos extends BaseAdapter {
    Context context;
    ArrayList<habitos> alHabitos;
    habitos misHabitos;
    LayoutInflater inflater;

    public AdaptadorHabitos(Context context, ArrayList<habitos> alHabitos) {
        this.context = context;
        this.alHabitos = alHabitos;
    }

    @Override
    public int getCount() {
        return alHabitos.size();
    }

    @Override
    public Object getItem(int position) {
        return alHabitos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.fotos, parent, false);
        try {
            misHabitos = alHabitos.get(position);

           TextView tempVal = itemView.findViewById(R.id.lblNombreAdaptador);
            tempVal.setText(misHabitos.getNombre());

            tempVal = itemView.findViewById(R.id.lblTelefonoAdaptador);
            tempVal.setText(misHabitos.getTelefono());

            tempVal = itemView.findViewById(R.id.lblEmailAdaptador);
            tempVal.setText(misHabitos.getEmail());

            ImageView img = itemView.findViewById(R.id.imgFotoAdaptador);
            Bitmap bitmap = BitmapFactory.decodeFile(misHabitos.getFoto());
            img.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return itemView;
    }
}