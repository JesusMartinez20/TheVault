package com.example.thevault;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class RegistroUsuario extends AppCompatActivity {

    private String usuario;
    private int sAnio=2000,sMes=00,sDia=01;
    private int tipo,avatar;
    private TextView txtFecha;
    private EditText etNombre,etUsuario,etCorreo,etPassword;
    private Button btnAccion;
    private ImageButton avatar1, avatar2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        txtFecha=(TextView)findViewById(R.id.txtFecha);
        etNombre=(EditText)findViewById(R.id.etNombre);
        etUsuario=(EditText)findViewById(R.id.etUsuario);
        etCorreo=(EditText)findViewById(R.id.etCorreo);
        etPassword=(EditText)findViewById(R.id.etPassword);
        btnAccion=(Button)findViewById(R.id.btnAccion);
        avatar1=(ImageButton)findViewById(R.id.btnAvatar1);
        avatar2=(ImageButton)findViewById(R.id.btnAvatar2);

        tipo = getIntent().getIntExtra("tipo", -1);

        avatar=1;
        avatar1.setBackgroundColor(this.getColor(R.color.secundario_oscuro));
        avatar2.setBackgroundColor(this.getColor(R.color.white));

        txtFecha.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                DatePickerDialog dpd=new DatePickerDialog(RegistroUsuario.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        txtFecha.setText(i+"-"+(i1+1)+"-"+i2);
                    }
                },sAnio,sMes,sDia);
                dpd.show();
            }
        });

        if(tipo==0){
            btnAccion.setText("Registrarse");
        }

        if(tipo==1){
            btnAccion.setText("Actualizar Perfil");
            etUsuario.setEnabled(false);
            SharedPreferences preferencias=getSharedPreferences("user.dat",MODE_PRIVATE);
            usuario=preferencias.getString("usuario","usuario");

            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.get(BEConection.URL + "consulta_usuario.php?username="+usuario,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode == 200){
                                try{
                                    String x = new String(responseBody);
                                    if( !x.equals("0")){
                                        JSONArray contacto = new JSONArray(new String(responseBody));

                                        etUsuario.setText(contacto.getJSONObject(0).getString("username"));
                                        etNombre.setText(contacto.getJSONObject(0).getString("nombre"));
                                        etCorreo.setText(contacto.getJSONObject(0).getString("correo"));
                                        etPassword.setText(contacto.getJSONObject(0).getString("contrasena"));
                                        txtFecha.setText(contacto.getJSONObject(0).getString("fecha_nacimiento"));

                                        if(contacto.getJSONObject(0).getString("avatar").equals("1")){
                                            avatar=1;
                                            avatar1.setBackgroundColor(getApplicationContext().getColor(R.color.secundario_oscuro));
                                            avatar2.setBackgroundColor(getApplicationContext().getColor(R.color.white));
                                        }
                                        if(contacto.getJSONObject(0).getString("avatar").equals("2")){
                                            avatar=2;
                                            avatar2.setBackgroundColor(getApplicationContext().getColor(R.color.secundario_oscuro));
                                            avatar1.setBackgroundColor(getApplicationContext().getColor(R.color.white));
                                        }
                                    }else{
                                        Toast.makeText(RegistroUsuario.this, "Error con la sesión", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e){
                                    Toast.makeText(RegistroUsuario.this, "Error al obtener información.", Toast.LENGTH_SHORT).show();
                                }
                            }else{

                                Toast.makeText(RegistroUsuario.this, "Error con la sesión", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override

                        public void onFailure(int statusCode, Header[] headers, byte[]
                                responseBody, Throwable error) {
                        }
                    });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setAvatar1(View view){
        avatar=1;
        avatar1.setBackgroundColor(this.getColor(R.color.secundario_oscuro));
        avatar2.setBackgroundColor(this.getColor(R.color.white));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setAvatar2(View view){
        avatar=2;
        avatar2.setBackgroundColor(this.getColor(R.color.secundario_oscuro));
        avatar1.setBackgroundColor(this.getColor(R.color.white));
    }

    public void registrar(View view){
        if(tipo==0){
            AsyncHttpClient cliente = new AsyncHttpClient();
            cliente.get(BEConection.URL + "login.php?username="+etUsuario.getText().toString(),
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode == 200){
                                String x = new String(responseBody);
                                if( !x.equals("0")){
                                    Toast.makeText(RegistroUsuario.this, "El usuario ya existe, por favor escriba otro", Toast.LENGTH_SHORT).show();
                                }else{
                                    if(!etUsuario.getText().toString().equals("")&&!etNombre.getText().toString().equals("")&&!etPassword.getText().toString().equals("")&&!etCorreo.getText().toString().equals("")){
                                        ejecutarWebService(BEConection.URL + "registro_usuario.php?username="+etUsuario.getText().toString().trim()+
                                                        "&nombre="+ etNombre.getText().toString() +
                                                        "&contrasena=" + etPassword.getText().toString() +
                                                        "&fecha_nac=" + txtFecha.getText().toString() +
                                                        "&correo=" + etCorreo.getText().toString()+
                                                        "&avatar="+avatar,
                                                "Cuenta creada");
                                        guardarPreferencias(etUsuario.getText().toString(),true);
                                        Intent login=new Intent(RegistroUsuario.this,Feed.class);
                                        startActivity(login);
                                    }else{
                                        Toast.makeText(RegistroUsuario.this, "Por favor, llena todos los datos", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else{
                                Toast.makeText(RegistroUsuario.this, "El usuario no ha podido ser creado", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override

                        public void onFailure(int statusCode, Header[] headers, byte[]
                                responseBody, Throwable error) {
                        }
                    });

        }

        if(tipo==1){
            if(!etUsuario.getText().toString().equals("")&&!etNombre.getText().toString().equals("")&&!etPassword.getText().toString().equals("")&&!etCorreo.getText().toString().equals("")){
                ejecutarWebService(BEConection.URL + "actualizar_usuario.php?username="+etUsuario.getText().toString().trim()+
                                "&nombre="+ etNombre.getText().toString() +
                                "&contrasena=" + etPassword.getText().toString() +
                                "&fecha_nac=" + txtFecha.getText().toString() +
                                "&correo=" + etCorreo.getText().toString()+
                                "&avatar="+avatar,
                        "Perfil actualizado");


                TimerTask tarea = new TimerTask() {
                    @Override
                    public void run() {
                        Intent perfil=new Intent(RegistroUsuario.this,Perfil.class);
                        startActivity(perfil);
                        finish();
                    }//rin
                };

                Timer tiempo = new Timer();
                tiempo.schedule(tarea, 150);
            }else{
                Toast.makeText(RegistroUsuario.this, "Por favor, llena todos los datos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ejecutarWebService(String url, final String msg){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegistroUsuario.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistroUsuario.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void guardarPreferencias(String usuario,boolean isRegistrado){
        SharedPreferences preferencias=getSharedPreferences("user.dat",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("usuario",usuario);
        editor.putBoolean("registrado",isRegistrado);
        editor.apply();
    }

    public void regresar(View view){
        finish();
    }
}