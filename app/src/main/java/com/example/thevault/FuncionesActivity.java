package com.example.thevault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FuncionesActivity extends AppCompatActivity {

    private TextView nombre, director, duracion, anio, pais;
    private ImageView imagen;
    private LinearLayout layoutFunciones;
    private int movieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funciones);

        imagen = (ImageView) findViewById(R.id.imgPosterFunciones);
        nombre= (TextView) findViewById(R.id.txtPeliculaFunciones);
        director = (TextView) findViewById(R.id.txtFuncionesDirector);
        duracion = (TextView) findViewById(R.id.txtFuncionesDuracion);
        anio = (TextView) findViewById(R.id.txtFuncionesAnio);
        pais= (TextView) findViewById(R.id.txtFuncionesPais);

        layoutFunciones = (LinearLayout) findViewById(R.id.layoutFunciones);

        movieID = (int) getIntent().getIntExtra("peliculaID", 0);

        putInfo();
        getFunciones();
    }

    private void putInfo() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(BEConection.URL + "consultarPelicula.php?id=" + movieID,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            try {
                                String x = new String(responseBody);
                                if (!x.equals("0")) {
                                    JSONObject movie = new JSONObject(new String(responseBody));

                                    //Se establecen los vaores de la imagen y de los textview en la vista pricipal
                                    Picasso.get().load(movie.getString("imagen")).into(imagen);
                                    nombre.setText(movie.getString("nombre"));
                                    duracion.setText(movie.getString("duracion") + " min.");
                                    anio.setText(movie.getString("fecha"));
                                    pais.setText(movie.getString("pais"));

                                    JSONArray staffArray = new JSONArray(movie.getString("staff"));

                                    //Se establece el valor de director
                                    for (int i = 0; i < staffArray.length(); ++i) {
                                        if (staffArray.getJSONObject(i).getString("rol").equals("Director")) {
                                            director.setText(staffArray.getJSONObject(i).getString("nombre"));
                                        }
                                    }

                                } else {
                                    Toast.makeText(FuncionesActivity.this, "Película no encontrada", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(FuncionesActivity.this, "Error al obtener información: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(FuncionesActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getFunciones() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(BEConection.URL + "consultarFuncionesPelicula.php?id=" + movieID,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            String x = new String(responseBody);
                            if (!x.equals("0")) {
                                try {
                                    JSONArray jsonArray = new JSONArray(new String(responseBody));

                                    for (int i = 0; i < jsonArray.length(); ++i) {
                                        View funcion = getLayoutInflater().inflate(R.layout.funciones_vista, null);
                                        TextView horario = (TextView) funcion.findViewById(R.id.txtFuncionHorario);
                                        horario.setText(jsonArray.getJSONObject(i).getString("horario"));
                                        TextView sala = (TextView) funcion.findViewById(R.id.txtFuncionSala);
                                        sala.setText(jsonArray.getJSONObject(i).getString("id_sala"));
                                        TextView asientosOcupados= (TextView) funcion.findViewById(R.id.txtFuncionAsientos);
                                        asientosOcupados.setText(jsonArray.getJSONObject(i).getString("asientos_ocupados")+"/"+jsonArray.getJSONObject(i).getString("capacidad"));
                                        final int id_funcion=Integer.valueOf(jsonArray.getJSONObject(i).getString("id"));

                                        funcion.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent=new Intent(getApplicationContext(),FuncionesActivity.class);
                                                intent.putExtra("funcionID",id_funcion);

                                                startActivity(intent);
                                            }
                                        });

                                        layoutFunciones.addView(funcion);
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(FuncionesActivity.this, "Error al obtener información: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(FuncionesActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}