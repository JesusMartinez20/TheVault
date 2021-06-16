package com.example.thevault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

public class Perfil extends AppCompatActivity {

    private String usuario;
    private TextView txtUsuario,txtNombre,txtCorreo,txtFecha_nac,txtFecha_reg;
    private Button btnEditar,btnEliminar;
    private ImageView imgAvatar;
    private ListView lvComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        imgAvatar=(ImageView)findViewById(R.id.imgAvatar);
        txtUsuario=(TextView)findViewById(R.id.txtUsuario);
        txtNombre=(TextView)findViewById(R.id.txtNombre);
        txtCorreo=(TextView)findViewById(R.id.txtCorreo);
        txtFecha_nac=(TextView)findViewById(R.id.txtFechaNac);
        txtFecha_reg=(TextView)findViewById(R.id.txtFechaReg);
        btnEditar=(Button)findViewById(R.id.btnEditar);
        btnEliminar=(Button)findViewById(R.id.btnEliminar);
        lvComentarios=(ListView)findViewById(R.id.lvComentarios);

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

                                    txtUsuario.setText(contacto.getJSONObject(0).getString("username"));
                                    txtNombre.setText(contacto.getJSONObject(0).getString("nombre"));
                                    txtCorreo.setText(contacto.getJSONObject(0).getString("correo"));
                                    txtFecha_nac.setText("Nacimiento: "+contacto.getJSONObject(0).getString("fecha_nacimiento"));
                                    txtFecha_reg.setText("Registro: "+contacto.getJSONObject(0).getString("fecha_registro"));

                                    if(contacto.getJSONObject(0).getString("avatar").equals("1")){
                                        imgAvatar.setImageResource(R.mipmap.alien1);
                                    }
                                    if(contacto.getJSONObject(0).getString("avatar").equals("2")){
                                        imgAvatar.setImageResource(R.mipmap.alien2);
                                    }
                                }else{
                                    Toast.makeText(Perfil.this, "Error con la sesión", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e){
                                Toast.makeText(Perfil.this, "Error al obtener información.", Toast.LENGTH_SHORT).show();
                            }
                        }else{

                            Toast.makeText(Perfil.this, "Error con la sesión", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override

                    public void onFailure(int statusCode, Header[] headers, byte[]
                            responseBody, Throwable error) {
                    }
                });
    }

    public void editar(View view){
        Intent intent=new Intent(Perfil.this,RegistroUsuario.class);
        intent.putExtra("tipo",1);
        startActivity(intent);
    }

    public void eliminar(View view){
        ejecutarWebService(BEConection.URL + "eliminar_usuario.php?username=" +usuario , "Cuenta eliminada");
        Intent intent=new Intent(Perfil.this,Login.class);
        startActivity(intent);
        finish();
    }

    private void ejecutarWebService(String url, final String msg){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Perfil.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Perfil.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void regresar(View view){
        Intent feed=new Intent(Perfil.this,Feed.class);
        startActivity(feed);
    }

}