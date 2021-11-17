package com.example.thevault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Feed extends AppCompatActivity {

    private EditText etBusqueda;
    private ImageButton btnBusqueda;
    private String nombrePelicula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        etBusqueda=(EditText)findViewById(R.id.etBusqueda);
        btnBusqueda=(ImageButton)findViewById(R.id.btnBusqueda);

        final ListView lista;
        final Adapter adaptador=new Adapter();
        lista=(ListView)findViewById(R.id.lista);

        if(getIntent().hasExtra("peliculaNombre")) {
            nombrePelicula = getIntent().getStringExtra("peliculaNombre");
            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.get(BEConection.URL + "buscar_pelicula.php?nombre="+nombrePelicula ,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            if(statusCode == 200){
                                try{
                                    String x = new String(responseBody);
                                    if( !x.equals("0")){
                                        JSONArray peliculas = new JSONArray(new String(responseBody));
                                        ArrayList<Card> resultado = new ArrayList<Card>();
                                        for (int i=0;i<peliculas.length();i++){
                                            Card carta=new Card();
                                            carta.id=peliculas.getJSONObject(i).getInt("id");
                                            carta.nombre=peliculas.getJSONObject(i).getString("nombre");
                                            carta.imagen=peliculas.getJSONObject(i).getString("imagen");
                                            carta.duracion=peliculas.getJSONObject(i).getString("duracion");
                                            carta.anio=peliculas.getJSONObject(i).getString("fecha_estreno");
                                            carta.premios=peliculas.getJSONObject(i).getString("premios");
                                            carta.comentarios=peliculas.getJSONObject(i).getString("comentarios");
                                            resultado.add(carta);
                                        }
                                        adaptador.arreglo=resultado;
                                        adaptador.context=Feed.this;
                                        lista.setAdapter(adaptador);
                                    } else{
                                        Toast.makeText(Feed.this, "Pelicula no encontrada.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e){
                                    Toast.makeText(Feed.this, "Error al obtener información.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(Feed.this, "Sin resultados en busqueda.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
        } else {
            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.get(BEConection.URL + "feed.php" ,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            if(statusCode == 200){
                                try{
                                    String x = new String(responseBody);
                                    if( !x.equals("0")){
                                        JSONArray peliculas = new JSONArray(new String(responseBody));
                                        ArrayList<Card> resultado = new ArrayList<Card>();
                                        for (int i=0;i<peliculas.length();i++){
                                            Card carta=new Card();
                                            carta.id=peliculas.getJSONObject(i).getInt("id");
                                            carta.nombre=peliculas.getJSONObject(i).getString("nombre");
                                            carta.imagen=peliculas.getJSONObject(i).getString("imagen");
                                            carta.duracion=peliculas.getJSONObject(i).getString("duracion");
                                            carta.anio=peliculas.getJSONObject(i).getString("fecha_estreno");
                                            carta.premios=peliculas.getJSONObject(i).getString("premios");
                                            carta.comentarios=peliculas.getJSONObject(i).getString("comentarios");
                                            resultado.add(carta);
                                        }
                                        adaptador.arreglo=resultado;
                                        adaptador.context=Feed.this;
                                        lista.setAdapter(adaptador);
                                    } else{
                                        Toast.makeText(Feed.this, "Pelicula no encontrada.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e){
                                    Toast.makeText(Feed.this, "Error al obtener información.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(Feed.this, "Sin resultados en busqueda.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
        }
    }

    public void buscarPelicula(View view){
        Intent feed = new Intent(this,Feed.class);
        feed.putExtra("peliculaNombre",etBusqueda.getText().toString());
        startActivity(feed);
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu){
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.perfil:
                Intent perfil = new Intent(this,Perfil.class);
                startActivity(perfil);
                break;
            case R.id.cerrarSesion:
                cerrarSesion();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cerrarSesion(){
        SharedPreferences preferencias = getSharedPreferences("user.dat",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.clear();
        editor.apply();
        Intent logout = new Intent(this,Login.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logout);
        finish();
    }
}