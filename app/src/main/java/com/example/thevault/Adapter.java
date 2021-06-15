package com.example.thevault;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {
    @Override
    public int getCount() {
        return arreglo.size();
    }

    @Override
    public Object getItem(int i) {
        return arreglo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return arreglo.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflate;
        inflate= LayoutInflater.from(context);
        View v=inflate.inflate(R.layout.cartas,null);
        TextView nombre=(TextView)v.findViewById(R.id.nombre);
        TextView duracion=(TextView)v.findViewById(R.id.duracion);
        ImageView imagen=(ImageView)v.findViewById(R.id.imagen);
        TextView año=(TextView)v.findViewById(R.id.año);
        TextView numComentarios=(TextView)v.findViewById(R.id.numComentarios);
        TextView numPremios=(TextView)v.findViewById(R.id.numPremios);

        nombre.setText(arreglo.get(i).nombre);
        duracion.setText(arreglo.get(i).duracion + " min");
        año.setText(arreglo.get(i).año);
        numComentarios.setText(arreglo.get(i).comentarios);
        numPremios.setText(arreglo.get(i).premios);
        Picasso.get().load(arreglo.get(i).imagen).into(imagen);

        return v;
    }

    public ArrayList<Card> arreglo;
    public Context context;
}
