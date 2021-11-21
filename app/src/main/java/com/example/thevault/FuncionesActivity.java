package com.example.thevault;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
    private String username;
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

        SharedPreferences preferences = getSharedPreferences("user.dat", MODE_PRIVATE);
        username = preferences.getString("usuario", null);

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
                                    final JSONArray jsonArray = new JSONArray(new String(responseBody));

                                    for (int i = 0; i < jsonArray.length(); ++i) {
                                        View funcion = getLayoutInflater().inflate(R.layout.funciones_vista, null);
                                        TextView horario = (TextView) funcion.findViewById(R.id.txtFuncionHorario);
                                        horario.setText(jsonArray.getJSONObject(i).getString("horario"));
                                        TextView sala = (TextView) funcion.findViewById(R.id.txtFuncionSala);
                                        sala.setText(jsonArray.getJSONObject(i).getString("id_sala"));
                                        TextView asientosOcupados= (TextView) funcion.findViewById(R.id.txtFuncionAsientos);
                                        asientosOcupados.setText(jsonArray.getJSONObject(i).getString("asientos_ocupados")+"/"+jsonArray.getJSONObject(i).getString("capacidad"));
                                        final int capacidad=Integer.valueOf(jsonArray.getJSONObject(i).getString("capacidad"));
                                        final int ocupados=Integer.valueOf(jsonArray.getJSONObject(i).getString("asientos_ocupados"));
                                        final int id_funcion=Integer.valueOf(jsonArray.getJSONObject(i).getString("id"));

                                        funcion.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                if(ocupados<capacidad){
                                                    final NumberPicker numberPicker = new NumberPicker(getApplicationContext());
                                                    if(capacidad-ocupados>=10){
                                                        numberPicker.setMaxValue(10);
                                                    }else{
                                                        numberPicker.setMaxValue(capacidad-ocupados);
                                                    }
                                                    numberPicker.setMinValue(1);

                                                    AlertDialog.Builder myBuild=new AlertDialog.Builder(FuncionesActivity.this);
                                                    myBuild.setView(numberPicker);

                                                    myBuild.setPositiveButton("Comprar",new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            ejecutarWebService(BEConection.URL + "insertarBoleto.php?id_username="+username+"&asientos=+"+String.valueOf(numberPicker.getValue())+"&id_funcion="+id_funcion, String.valueOf(numberPicker.getValue())+" Boleto(s) comprado(s) exitosamente");
                                                            Intent intent=new Intent(FuncionesActivity.this,Feed2.class);
                                                            startActivity(intent);
                                                            finish();
                                                            //Toast.makeText(FuncionesActivity.this, "Comprar "+String.valueOf(numberPicker.getValue())+" boletos", Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                                    myBuild.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.cancel();
                                                        }
                                                    });

                                                    AlertDialog dialog=myBuild.create();
                                                    dialog.show();
                                                }else{
                                                    Toast.makeText(FuncionesActivity.this, "Todos los asientos han sido ocupados", Toast.LENGTH_LONG).show();
                                                }

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

    private void ejecutarWebService(String url, final String msg){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(FuncionesActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FuncionesActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}