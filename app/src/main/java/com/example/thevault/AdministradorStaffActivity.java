package com.example.thevault;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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

public class AdministradorStaffActivity extends AppCompatActivity {

    private TextView accionTitulo, fecha;
    private EditText nombre, nacionalidad, imagen;
    private Spinner spinner;
    private RadioButton hombre, mujer;
    private Button accion, eliminar;
    private int accionRecibida, staffID, participacionID, peliculaID;

    private int sAnio=2000,sMes=00,sDia=01;

    private final String[] rolOpciones = { "Director", "Actor", "Actriz", "Guionista", "Productor","Director de fotografía" };
    private String eleccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_staff);

        accionTitulo = (TextView) findViewById(R.id.txtAdminStaffAccion);
        fecha = (TextView) findViewById(R.id.txtAdminStaffFecha);
        nombre = (EditText) findViewById(R.id.txtAdminStaffNombre);
        nacionalidad = (EditText) findViewById(R.id.txtAdminStaffNacionalidad);
        imagen = (EditText) findViewById(R.id.txtAdminStaffImagen);
        hombre = (RadioButton) findViewById(R.id.rbtAdminStaffHombre);
        mujer = (RadioButton) findViewById(R.id.rbtAdminStaffMujer);
        accion = (Button) findViewById(R.id.btnAdminStaffAccion);
        eliminar = (Button) findViewById(R.id.btnAdminStaffEliminar);

        spinner = (Spinner) findViewById(R.id.spnAdminStaffRol);

        accionRecibida = (int) getIntent().getIntExtra("accion", 0);
        peliculaID = (int) getIntent().getIntExtra("peliculaID", 0);

        if (accionRecibida == 0) {
            eliminar.setVisibility(View.GONE);
            staffID = 0;
            participacionID = 0;
        } else {
            accionTitulo.setText(R.string.modificarStaff);
            accion.setText(R.string.modificarStaff);
            staffID = (int) getIntent().getIntExtra("staffID", 0);
            putInfo();
        }

        fecha.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                DatePickerDialog dpd=new DatePickerDialog(AdministradorStaffActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        fecha.setText(i+"-"+(i1+1)+"-"+i2);
                    }
                },sAnio,sMes,sDia);
                dpd.show();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.staff_rol_layout, rolOpciones);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eleccion = rolOpciones[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                eleccion = rolOpciones[0];
            }
        });
    }

    private void putInfo() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(BEConection.URL + "consultarStaff.php?id=" + staffID,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            try {
                                String x = new String(responseBody);
                                if (!x.equals("0")) {
                                    JSONObject staff = new JSONObject(new String(responseBody));

                                    nombre.setText(staff.getString("nombre"));
                                    nacionalidad.setText(staff.getString("nacinalidad"));
                                    imagen.setText(staff.getString("imagen"));
                                    fecha.setText(staff.getString("fecha_nacimiento"));
                                    participacionID = staff.getInt("participacion_id");
                                    peliculaID = staff.getInt("id_pelicula");
                                    eleccion = staff.getString("rol");
                                    if (staff.getString("sexo").equals("M")) {
                                        hombre.setChecked(true);
                                    } else {
                                        mujer.setChecked(true);
                                    }

                                } else {
                                    Toast.makeText(AdministradorStaffActivity.this, "Película no encontrada", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(AdministradorStaffActivity.this, "Error al obtener información: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(AdministradorStaffActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void ejecutarWebService(String url, final String msg, int method) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("nombre", nombre.getText().toString());
            jsonBody.put("nacionalidad", nacionalidad.getText().toString());
            jsonBody.put("fecha_nacimiento", fecha.getText().toString());
            jsonBody.put("imagen", imagen.getText().toString());
            jsonBody.put("id_staff", staffID);
            jsonBody.put("rol", eleccion);
            jsonBody.put("id_pelicula", peliculaID);
            jsonBody.put("id_participacion", participacionID);

            jsonBody.put("sexo", (hombre.isChecked() ? "M" : "F"));

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(AdministradorStaffActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AdministradorStaffActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
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

    public void accionStaff(View view) {
        if (accionRecibida == 0) {
            ejecutarWebService(BEConection.URL+ "insertarStaff.php",
                    "Staff agregado", Request.Method.POST);
            limpiarCampos();
        } else {
            ejecutarWebService(BEConection.URL+ "actualizarStaff.php",
                    "Staff actualizado", Request.Method.PUT);
        }
    }

    public void eliminarStaff(View view) {
        AlertDialog.Builder myBuild = new AlertDialog.Builder(this);
        myBuild.setMessage("¿Seguro que quieres eliminar el staff?");
        myBuild.setMessage("Eliminar staff");
        myBuild.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ejecutarWebService(BEConection.URL + "eliminarStaff.php?id=" + participacionID , "Staff eliminada", Request.Method.GET);
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
        nombre.setText("");;
        nacionalidad.setText("");;
        imagen.setText("");;
        fecha.setText("");;
    }
}