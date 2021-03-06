package com.example.thevault;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class AdministradorPeliculaActivity extends AppCompatActivity {

    private TextView accionTitulo;
    private EditText titulo, duracion, estreno, origen, imagen;
    private CheckBox proyeccion;
    private Button accion, staff, premios, eliminar;
    private int accionRecibida, peliculaID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_pelicula);

        accionTitulo = (TextView) findViewById(R.id.txtAdminPeliculaAccion);
        titulo = (EditText) findViewById(R.id.txtAdminPeliculaTitulo);
        duracion = (EditText) findViewById(R.id.txtAdminPeliculaDuracion);
        estreno = (EditText) findViewById(R.id.txtAdminPeliculaEstreno);
        origen = (EditText) findViewById(R.id.txtAdminPeliculaPais);
        imagen = (EditText) findViewById((R.id.txtAdminPeliculaImagen));
        proyeccion = (CheckBox) findViewById(R.id.chkAdminPeliculaProyeccion);
        accion = (Button) findViewById(R.id.btnAdminPeliculaAccion);
        staff = (Button) findViewById(R.id.btnAdminPeliculaStaff);
        premios = (Button) findViewById(R.id.btnAdminPeliculaPremio);
        eliminar = (Button) findViewById(R.id.btnAdminPeliculaEliminar);

        accionRecibida = (int) getIntent().getIntExtra("accion", 0);

        if (accionRecibida == 0) {
            eliminar.setVisibility(View.GONE);
            staff.setEnabled(false);
            premios.setEnabled(false);
        } else {
            accionTitulo.setText(R.string.modificarPelicula);
            accion.setText(R.string.modificarPelicula);;
            staff.setText(R.string.modificarStaff);
            premios.setText(R.string.modificarPremio);

            if(getIntent().hasExtra("peliculaID")) {
                peliculaID = (int) getIntent().getIntExtra("peliculaID", 0);
                putInfo();
            }
        }
    }

    private void putInfo() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(BEConection.URL + "consultarPelicula.php?id=" + peliculaID,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            try {
                                String x = new String(responseBody);
                                if (!x.equals("0")) {
                                    JSONObject movie = new JSONObject(new String(responseBody));

                                    //Se establecen los vaores de la imagen y de los textview en la vista pricipal
                                    titulo.setText(movie.getString("nombre"));
                                    duracion.setText(movie.getString("duracion"));
                                    estreno.setText(movie.getString("fecha"));
                                    origen.setText(movie.getString("pais"));
                                    imagen.setText(movie.getString("imagen"));

                                    proyeccion.setChecked((movie.getInt("en_proyeccion") == 1));

                                } else {
                                    Toast.makeText(AdministradorPeliculaActivity.this, "Pel??cula no encontrada", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(AdministradorPeliculaActivity.this, "Error al obtener informaci??n: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(AdministradorPeliculaActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void funcionTontaParaObtenerElIdDeLaPeliculaInsertadaPorqueNoSeUsarJSONObjectRequest() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(BEConection.URL + "getLastMovieId.php",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            try {
                                String x = new String(responseBody);
                                if (!x.equals("0")) {
                                    JSONObject pelicula = new JSONObject(new String(responseBody));

                                    peliculaID = pelicula.getInt("id");
                                    staff.setEnabled(true);
                                    premios.setEnabled(true);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(AdministradorPeliculaActivity.this, "Error al obtener informaci??n: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(AdministradorPeliculaActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void ejecutarWebService(String url, final String msg, int method) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nombre", titulo.getText().toString());
            jsonBody.put("duracion", duracion.getText().toString());
            jsonBody.put("pais", origen.getText().toString());
            jsonBody.put("imagen", imagen.getText().toString());
            jsonBody.put("fecha_estreno", estreno.getText().toString());
            jsonBody.put("en_proyeccion", (proyeccion.isChecked() ? 1 : 0));
            jsonBody.put("id", peliculaID);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(AdministradorPeliculaActivity.this, msg, Toast.LENGTH_SHORT).show();
                    funcionTontaParaObtenerElIdDeLaPeliculaInsertadaPorqueNoSeUsarJSONObjectRequest();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AdministradorPeliculaActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void accionPelicula(View view) {
        if (accionRecibida == 0) {
            ejecutarWebService(BEConection.URL+ "insertarPelicula.php",
                    "Pelicula agregada", Request.Method.POST);
            limpiarCampos();

        } else {
            ejecutarWebService(BEConection.URL+ "actualizarPelicula.php",
                    "Pelicula actualizada", Request.Method.PUT);
        }
        irAFeed();
    }

    public void eliminarPelicula(View view) {
        AlertDialog.Builder myBuild = new AlertDialog.Builder(this);
        myBuild.setMessage("??Seguro que quieres eliminar la pel??cula?");
        myBuild.setMessage("Eliminar pel??cula");
        myBuild.setPositiveButton("S??", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ejecutarWebService(BEConection.URL + "eliminarPelicula.php?id=" + peliculaID , "Pel??cula eliminada", Request.Method.GET);
                irAFeed();
            }
        });

        myBuild.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog=myBuild.create();
        dialog.show();
    }

    public void irAStaff(View view) {
        Intent staff = new Intent(this,
                (accionRecibida == 0 ? AdministradorStaffActivity.class : StaffListaActivity.class));

        staff.putExtra("accion", accionRecibida); //0 para agregar staff, 1 para ir a lista.
        staff.putExtra("peliculaID", peliculaID);
        startActivity(staff);
    }

    public void irAPremios(View view) {
        Intent staff = new Intent(this,
                (accionRecibida == 0 ? AdministradorPremiosActivity.class : PremiosListaActivity.class));

        staff.putExtra("accion", accionRecibida); //0 para agregar staff, 1 para ir a lista.
        staff.putExtra("peliculaID", peliculaID);
        startActivity(staff);
    }

    private void limpiarCampos() {
        titulo.setText("");
        duracion.setText("");
        estreno.setText("");
        origen.setText("");
        imagen.setText("");
        proyeccion.setChecked(false);
    }

    private void irAFeed(){
        Intent staff = new Intent(this, Feed2.class);
        startActivity(staff);
        finish();
    }
}