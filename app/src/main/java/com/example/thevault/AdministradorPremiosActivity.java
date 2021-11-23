package com.example.thevault;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class AdministradorPremiosActivity extends AppCompatActivity {

    private TextView accionTitulo, fecha;
    private EditText categoria, lugar, academia;
    private Button accion, eliminar;
    private int accionRecibida, premioID, peliculaID;

    private int sAnio=2000,sMes=00,sDia=01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_premios);

        accionTitulo = (TextView) findViewById(R.id.txtAdminPremioAccion);
        fecha = (TextView) findViewById(R.id.txtAdminPremioFecha);
        categoria = (EditText) findViewById(R.id.txtAdminPremioCategoria);
        lugar = (EditText) findViewById(R.id.txtAdminPremioLugar);
        academia = (EditText) findViewById(R.id.txtAdminPremioAdademia);
        accion = (Button) findViewById(R.id.btnAdminPremioAccion);
        eliminar = (Button) findViewById(R.id.btnAdminPremioEliminar);

        accionRecibida = (int) getIntent().getIntExtra("accion", 0);
        peliculaID = (int) getIntent().getIntExtra("peliculaID", 0);

        if (accionRecibida == 0) {
            eliminar.setVisibility(View.GONE);
            premioID = 0;

        } else {
            accionTitulo.setText(R.string.modificarPremio);
            accion.setText(R.string.modificarPremio);
            premioID = (int) getIntent().getIntExtra("premioID", 0);
            putInfo();
        }

        fecha.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                DatePickerDialog dpd=new DatePickerDialog(AdministradorPremiosActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        fecha.setText(i+"-"+(i1+1)+"-"+i2);
                    }
                },sAnio,sMes,sDia);
                dpd.show();
            }
        });
    }

    private void putInfo() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(BEConection.URL + "consultarPremio.php?id=" + premioID,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            try {
                                String x = new String(responseBody);
                                if (!x.equals("0")) {
                                    JSONObject premio = new JSONObject(new String(responseBody));

                                    //Se establecen los vaores de la imagen y de los textview en la vista pricipal
                                    categoria.setText(premio.getString("categoria"));
                                    lugar.setText(premio.getString("lugar"));
                                    academia.setText(premio.getString("academia"));
                                    fecha.setText(premio.getString("fecha"));

                                } else {
                                    Toast.makeText(AdministradorPremiosActivity.this, "Película no encontrada", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(AdministradorPremiosActivity.this, "Error al obtener información: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(AdministradorPremiosActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void ejecutarWebService(String url, final String msg, int method) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("categoria", categoria.getText().toString());
            jsonBody.put("lugar", lugar.getText().toString());
            jsonBody.put("academia", academia.getText().toString());
            jsonBody.put("id", premioID);
            jsonBody.put("fecha", fecha.getText().toString());
            if (getIntent().hasExtra("peliculaID")) {
                jsonBody.put("id_pelicula", getIntent().getIntExtra("peliculaID", 0));
            }

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(AdministradorPremiosActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AdministradorPremiosActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
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

    public void accionPremio(View view) {
        if (accionRecibida == 0) {
            ejecutarWebService(BEConection.URL+ "insertarPremio.php",
                    "Premio agregado", Request.Method.POST);
            limpiarCampos();
        } else {
            ejecutarWebService(BEConection.URL+ "actualizarPremio.php",
                    "Premio actualizado", Request.Method.PUT);
        }
    }

    public void eliminarPremio(View view) {
        AlertDialog.Builder myBuild = new AlertDialog.Builder(this);
        myBuild.setMessage("¿Seguro que quieres eliminar el premio?");
        myBuild.setMessage("Eliminar premio");
        myBuild.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ejecutarWebService(BEConection.URL + "eliminarPremio.php?id=" + premioID , "Premio eliminado", Request.Method.GET);
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

    private void limpiarCampos() {
        categoria.setText("");
        lugar.setText("");
        academia.setText("");
        fecha.setText("");
    }
}