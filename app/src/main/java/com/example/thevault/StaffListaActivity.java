package com.example.thevault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class StaffListaActivity extends AppCompatActivity {

    private LinearLayout layoutStaff;
    private int peliculaID;
    private Button btnAgregarStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_lista);

        layoutStaff=(LinearLayout) findViewById(R.id.layoutStaffLista);
        btnAgregarStaff=(Button) findViewById(R.id.btnStaffListaAgregar);
        peliculaID = (int) getIntent().getIntExtra("peliculaID", 0);
        putInfo();
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

                                    //Se establecen los valores para premios
                                    JSONArray staffArray = new JSONArray(movie.getString("staff"));

                                    for (int i = 0; i < staffArray.length(); ++i) {
                                        final JSONObject currentStaff = staffArray.getJSONObject(i);

                                        View staff = getLayoutInflater().inflate(R.layout.staff_vista, null);
                                        TextView txtNombre= (TextView) staff.findViewById(R.id.txtStaffCardNombre);
                                        txtNombre.setText(currentStaff.getString("nombre"));
                                        TextView txtNacionalidad= (TextView) staff.findViewById(R.id.txtStaffCardNacionalidad);
                                        txtNacionalidad.setText(currentStaff.getString("nacinalidad"));
                                        TextView txtFecha = (TextView) staff.findViewById(R.id.txtStaffCardFecha);
                                        txtFecha.setText(currentStaff.getString("fecha_nacimiento"));
                                        TextView txtSexo= (TextView) staff.findViewById(R.id.txtStaffCardSexo);
                                        txtSexo.setText(currentStaff.getString("sexo"));
                                        TextView txtRol= (TextView) staff.findViewById(R.id.txtStaffCardRol);
                                        txtRol.setText(currentStaff.getString("rol"));

                                        staff.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                Intent intent = new Intent(StaffListaActivity.this, AdministradorStaffActivity.class);
                                                try {
                                                    intent.putExtra("staffID", currentStaff.getInt("staff_id"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                intent.putExtra("accion",1);
                                                startActivity(intent);

                                            }
                                        });
                                        layoutStaff.addView(staff);
                                    }

                                } else {
                                    Toast.makeText(StaffListaActivity.this, "Película no encontrada", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(StaffListaActivity.this, "Error al obtener información: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(StaffListaActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void agregarStaff(View view){
        Intent intent = new Intent(StaffListaActivity.this, AdministradorStaffActivity.class);
        intent.putExtra("peliculaID", peliculaID);
        intent.putExtra("accion",0);
    }
}