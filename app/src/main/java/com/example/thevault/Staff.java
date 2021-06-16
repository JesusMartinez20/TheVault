package com.example.thevault;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

public class Staff extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        final TextView nombre, nacionalidad, sexo, nacimiento;

        nombre=(TextView)findViewById(R.id.nombre);
        nacionalidad=(TextView)findViewById(R.id.nacionalidad);
        nacimiento=(TextView)findViewById(R.id.nacimiento);
        sexo=(TextView)findViewById(R.id.sexo);


        int id = getIntent().getIntExtra("staffID", -1);

        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(BEConection.URL + "staff.php?id=" +id,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        if(statusCode == 200){
                            try{
                                String x = new String(responseBody);
                                if( !x.equals("0")){
                                    JSONArray contacto = new JSONArray(new
                                            String(responseBody));
                                    nombre.setText(contacto.getJSONObject(0).getString("nombre"));
                                    nacionalidad.setText("País de origen: "+ contacto.getJSONObject(0).getString("nacinalidad"));
                                    nacimiento.setText("Fecha de nacimiento: "+contacto.getJSONObject(0).getString("fecha_nacimiento"));
                                    sexo.setText("Sexo: "+contacto.getJSONObject(0).getString("sexo"));
                                } else{
                                    Toast.makeText(Staff.this, "Persona no encontrada.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e){
                                Toast.makeText(Staff.this, "Error al obtener información.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(Staff.this, "Sin resultados en busqueda.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
    }
}