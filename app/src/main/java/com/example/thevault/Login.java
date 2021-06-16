package com.example.thevault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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

import cz.msebera.android.httpclient.Header;

public class Login extends AppCompatActivity {

    private TextView irRegistro;
    private EditText etUsername, etPassword;
    private CheckBox guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername=(EditText)findViewById(R.id.etUsuario);
        etPassword=(EditText)findViewById(R.id.etPassword);
        guardar=(CheckBox)findViewById(R.id.cbGuardar);
        irRegistro=(TextView)findViewById(R.id.txtRegistro);

        irRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,RegistroUsuario.class);
                startActivity(intent);
            }
        });
    }

    public void ingresarFeed(View view){
        iniciarSesion(etUsername.getText().toString().trim(),etPassword.getText().toString().trim());
    }

    private void guardarPreferencias(String usuario,boolean isRegistrado){
        SharedPreferences preferencias=getSharedPreferences("user.dat",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("usuario",usuario);
        editor.putBoolean("registrado",isRegistrado);
        editor.apply();
    }

    public void salirApp(View view){
        finish();
    }

    private void ejecutarWebService(String url, final String msg){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void iniciarSesion(final String username, final String password){
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(BEConection.URL + "login.php?username="+username,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(statusCode == 200){
                            try{
                                String x = new String(responseBody);
                                if( !x.equals("0")){
                                    JSONArray contacto = new JSONArray(new String(responseBody));
                                     if(password.equals(contacto.getJSONObject(0).getString("contrasena"))){
                                         guardarPreferencias(username,guardar.isChecked());
                                         Intent login=new Intent(Login.this,Feed.class);
                                         startActivity(login);
                                     }else{
                                         Toast.makeText(Login.this, "La contraseña es incorrecta", Toast.LENGTH_SHORT).show();
                                         etPassword.setText("");
                                     }
                                }else{
                                    Toast.makeText(Login.this, "El usuario no exite", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e){
                                Toast.makeText(Login.this, "Error al obtener información.", Toast.LENGTH_SHORT).show();
                            }
                        }else{

                            Toast.makeText(Login.this, "El usuario no ha sido encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override

                    public void onFailure(int statusCode, Header[] headers, byte[]
                            responseBody, Throwable error) {
                    }
                });
    }
}