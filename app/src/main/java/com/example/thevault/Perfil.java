package com.example.thevault;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

public class Perfil extends AppCompatActivity {

    private String usuario;
    private TextView txtUsuario,txtNombre,txtCorreo,txtFecha_nac,txtFecha_reg;
    private Button btnEditar,btnEliminar;
    private ImageView imgAvatar;
    private LinearLayout lyComentarios,lyBoletos;

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
        btnEditar=(Button)findViewById(R.id.btnEditarComentPerfil);
        btnEliminar=(Button)findViewById(R.id.btnEliminarComentPerfil);
        lyComentarios=(LinearLayout)findViewById(R.id.lyComentarios);
        lyBoletos=(LinearLayout)findViewById(R.id.lyBoletosComprados);

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
                                        imgAvatar.setImageResource(R.mipmap.avatar1);
                                    }
                                    if(contacto.getJSONObject(0).getString("avatar").equals("2")){
                                        imgAvatar.setImageResource(R.mipmap.avatar2);
                                    }
                                }else{
                                    Toast.makeText(Perfil.this, "Error con la sesi??n", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e){
                                Toast.makeText(Perfil.this, "Error al obtener informaci??n.", Toast.LENGTH_SHORT).show();
                            }
                        }else{

                            Toast.makeText(Perfil.this, "Error con la sesi??n", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override

                    public void onFailure(int statusCode, Header[] headers, byte[]
                            responseBody, Throwable error) {
                    }
                });

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(BEConection.URL + "consultarBoletos.php?username=" + usuario,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            String x = new String(responseBody);
                            if (!x.equals("0")) {
                                try {
                                    JSONArray jsonArray = new JSONArray(new String(responseBody));

                                    for (int i = 0; i < jsonArray.length(); ++i) {

                                        View boleto = getLayoutInflater().inflate(R.layout.boletos_vista, null);
                                        TextView txtIdBoleto=(TextView)boleto.findViewById(R.id.txtIdBoleto);
                                        txtIdBoleto.setText("#"+jsonArray.getJSONObject(i).getString("id"));
                                        TextView txtNombrePelicula=(TextView)boleto.findViewById(R.id.txtNombrePelicula);
                                        txtNombrePelicula.setText(jsonArray.getJSONObject(i).getString("nombre"));
                                        TextView txtFuncionBoleto=(TextView)boleto.findViewById(R.id.txtFuncionBoleto);
                                        txtFuncionBoleto.setText(jsonArray.getJSONObject(i).getString("horario"));
                                        TextView txtAsientosBoleto=(TextView)boleto.findViewById(R.id.txtAsientosBoleto);
                                        txtAsientosBoleto.setText(jsonArray.getJSONObject(i).getString("asientos"));

                                        ImageButton btnVerBoleto=(ImageButton)boleto.findViewById(R.id.btnVerBoleto);

                                        final int boletoID=jsonArray.getJSONObject(i).getInt("id");

                                        btnVerBoleto.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent=new Intent(Perfil.this,VistaBoleto.class);
                                                intent.putExtra("boletoID",boletoID);
                                                startActivity(intent);
                                            }
                                        });

                                        lyBoletos.addView(boleto);
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(Perfil.this, "Error al obtener informaci??n: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(Perfil.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        client = new AsyncHttpClient();

        client.get(BEConection.URL + "consulta_comentarios_perfil.php?username=" + usuario,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            String x = new String(responseBody);
                            if (!x.equals("0")) {
                                try {
                                    JSONArray jsonArray = new JSONArray(new String(responseBody));

                                    for (int i = 0; i < jsonArray.length(); ++i) {

                                        View comment = getLayoutInflater().inflate(R.layout.comentario_vista_perfil, null);
                                        TextView txtNombrePelicula=(TextView)comment.findViewById(R.id.txtPeliculaCartaPerfil);
                                        txtNombrePelicula.setText(jsonArray.getJSONObject(i).getString("nombre"));
                                        TextView txtTituloComentario=(TextView)comment.findViewById(R.id.txtTItuloComentarioPerfil);
                                        txtTituloComentario.setText(jsonArray.getJSONObject(i).getString("titulo"));
                                        TextView txtCalificacion=(TextView)comment.findViewById(R.id.txtCalificacionCartaPerfil);
                                        txtCalificacion.setText("Calificaci??n: "+jsonArray.getJSONObject(i).getString("calificacion"));
                                        TextView txtOpinion=(TextView)comment.findViewById(R.id.txtOpinionCartaPerfil);
                                        txtOpinion.setText(jsonArray.getJSONObject(i).getString("opinion"));
                                        ImageView avatar = (ImageView) comment.findViewById(R.id.imgComentarioAvatarPerfil);
                                        Picasso.get().load(jsonArray.getJSONObject(i).getString("imagen")).into(avatar);
                                        ImageButton btnEditarComentario=(ImageButton)comment.findViewById(R.id.btnEditarComentPerfil);
                                        final int commentID=jsonArray.getJSONObject(i).getInt("id");
                                        btnEditarComentario.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent=new Intent(Perfil.this,AgregarComentarioActivity.class);
                                                intent.putExtra("commentID",commentID);
                                                startActivity(intent);
                                            }
                                        });



                                        ImageButton btnEliminarComentario=(ImageButton)comment.findViewById(R.id.btnEliminarComentPerfil);
                                        btnEliminarComentario.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                AlertDialog.Builder myBuild=new AlertDialog.Builder(Perfil.this);
                                                myBuild.setMessage("??En verdad quieres eliminar el comentario?");
                                                myBuild.setMessage("Eliminar Comentario");
                                                myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        ejecutarWebService(BEConection.URL + "eliminar_comentario.php?id=" +commentID , "Comentario eliminado");
                                                        Intent intent=new Intent(Perfil.this,Perfil.class);
                                                        startActivity(intent);
                                                        finish();
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
                                        });

                                        lyComentarios.addView(comment);
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(Perfil.this, "Error al obtener informaci??n: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(Perfil.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void editar(View view){
        Intent intent=new Intent(Perfil.this,RegistroUsuario.class);
        intent.putExtra("tipo",1);
        startActivity(intent);
    }

    public void eliminar(View view){
        AlertDialog.Builder myBuild=new AlertDialog.Builder(this);
        myBuild.setMessage("??En verdad quieres eliminar tu cuenta?");
        myBuild.setMessage("Eliminar Cuenta");
        myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ejecutarWebService(BEConection.URL + "eliminar_usuario.php?username=" +usuario , "Cuenta eliminada");
                Intent intent=new Intent(Perfil.this,Login.class);
                startActivity(intent);
                finish();
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
        Intent feed=new Intent(Perfil.this,Feed2.class);
        startActivity(feed);
    }

}