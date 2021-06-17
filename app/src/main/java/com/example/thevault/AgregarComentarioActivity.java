package com.example.thevault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class AgregarComentarioActivity extends AppCompatActivity {

    private EditText title, opinion;
    private ImageButton btn1Star, btn2Stars, btn3Stars, btn4Stars, btn5Stars;
    private TextView txtCommentAction;
    private Button btnCommentAction;
    private String username;
    int movieID, score, commentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_comentario);

        title = (EditText) findViewById(R.id.txtCommentViewAddTitle);
        opinion = (EditText) findViewById(R.id.txtCommentViewAddOpinion);
        btn1Star = (ImageButton) findViewById(R.id.btn1Star);
        btn2Stars = (ImageButton) findViewById(R.id.btn2Stars);
        btn3Stars = (ImageButton) findViewById(R.id.btn3Stars);
        btn4Stars = (ImageButton) findViewById(R.id.btn4Stars);
        btn5Stars = (ImageButton) findViewById(R.id.btn5Stars);

        txtCommentAction = (TextView) findViewById(R.id.txtCommentViewAction);
        btnCommentAction = (Button) findViewById(R.id.btnCommentViewAction);

        if(getIntent().hasExtra("peliculaID")) {
            verificarComentarioPrevio();
        } else {
            commentID = getIntent().getIntExtra("commentID", -1);
            llenarDatos(commentID);
        }
    }

    private void verificarComentarioPrevio() {
        AsyncHttpClient client = new AsyncHttpClient();
        SharedPreferences preferences = getSharedPreferences("user.dat", MODE_PRIVATE);
        username = preferences.getString("usuario", null);
        movieID = getIntent().getIntExtra("peliculaID", -1);

        client.get(BEConection.URL + "verificarComentarioPrevio.php?id=" + movieID + "&user=" + username,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            String x = new String(responseBody);
                            if (x.equals("0")) {
                                txtCommentAction.setText(getString(R.string.AgregarComentario_agregarComentario));
                                btnCommentAction.setText(getString(R.string.AgregarComentario_agregarComentario));
                                btnCommentAction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addComment(v);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                                    commentID = jsonObject.getInt("id");
                                    llenarDatos(commentID);
                                } catch (JSONException e) {
                                    Toast.makeText(AgregarComentarioActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                txtCommentAction.setText(getString(R.string.AgregarComentario_editarComentario));
                                btnCommentAction.setText(getString(R.string.AgregarComentario_editarComentario));
                                btnCommentAction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editComment(v);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(AgregarComentarioActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void llenarDatos(int commentId){
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(BEConection.URL + "consultarComentario.php?id="+commentId,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(statusCode == 200){
                            try{
                                String x = new String(responseBody);
                                if( !x.equals("0")){
                                    JSONArray contacto = new JSONArray(new String(responseBody));
                                    title.setText(contacto.getJSONObject(0).getString("titulo"));
                                    opinion.setText(contacto.getJSONObject(0).getString("opinion"));
                                    Drawable drawableON = getResources().getDrawable(android.R.drawable.btn_star_big_on);
                                    Drawable drawableOFF = getResources().getDrawable(android.R.drawable.btn_star_big_off);
                                    switch (contacto.getJSONObject(0).getInt("calificacion")) {
                                        case 1:
                                            score = 1;
                                            btn1Star.setImageDrawable(drawableON);
                                            btn2Stars.setImageDrawable(drawableOFF);
                                            btn3Stars.setImageDrawable(drawableOFF);
                                            btn4Stars.setImageDrawable(drawableOFF);
                                            btn5Stars.setImageDrawable(drawableOFF);
                                            break;
                                        case 2:
                                            score = 2;
                                            btn1Star.setImageDrawable(drawableON);
                                            btn2Stars.setImageDrawable(drawableON);
                                            btn3Stars.setImageDrawable(drawableOFF);
                                            btn4Stars.setImageDrawable(drawableOFF);
                                            btn5Stars.setImageDrawable(drawableOFF);
                                            break;
                                        case 3:
                                            score = 3;
                                            btn1Star.setImageDrawable(drawableON);
                                            btn2Stars.setImageDrawable(drawableON);
                                            btn3Stars.setImageDrawable(drawableON);
                                            btn4Stars.setImageDrawable(drawableOFF);
                                            btn5Stars.setImageDrawable(drawableOFF);
                                            break;
                                        case 4:
                                            score = 4;
                                            btn1Star.setImageDrawable(drawableON);
                                            btn2Stars.setImageDrawable(drawableON);
                                            btn3Stars.setImageDrawable(drawableON);
                                            btn4Stars.setImageDrawable(drawableON);
                                            btn5Stars.setImageDrawable(drawableOFF);
                                            break;
                                        case 5:
                                            score = 5;
                                            btn1Star.setImageDrawable(drawableON);
                                            btn2Stars.setImageDrawable(drawableON);
                                            btn3Stars.setImageDrawable(drawableON);
                                            btn4Stars.setImageDrawable(drawableON);
                                            btn5Stars.setImageDrawable(drawableON);
                                            break;
                                    }

                                }else{
                                    Toast.makeText(AgregarComentarioActivity.this, "Error con la consulta", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e){
                                Toast.makeText(AgregarComentarioActivity.this, "Error al obtener información.", Toast.LENGTH_SHORT).show();
                            }
                        }else{

                            Toast.makeText(AgregarComentarioActivity.this, "Error con la sesión", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override

                    public void onFailure(int statusCode, Header[] headers, byte[]
                            responseBody, Throwable error) {
                    }
                });
    }

    private void ejecuarWebService(String url, final String msg, int method) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("title", title.getText().toString());
            jsonBody.put("opinion", opinion.getText().toString());
            jsonBody.put("score", score);
            jsonBody.put("user", username);
            jsonBody.put("id_movie", movieID);
            jsonBody.put("id", commentID);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(AgregarComentarioActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AgregarComentarioActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
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

    public void addComment(View view) {
        ejecuarWebService(BEConection.URL + "insertarComentario.php",
                "Comentario registrado", Request.Method.POST);
        //clearFields();
        Intent intent=new Intent(AgregarComentarioActivity.this,Feed.class);
        startActivity(intent);
        finish();
    }

    public void editComment(View view) {
        ejecuarWebService(BEConection.URL + "editarComentario.php",
                "Comentario editado", Request.Method.PUT);
        //clearFields();
        Intent intent=new Intent(AgregarComentarioActivity.this,Feed.class);
        startActivity(intent);
        finish();
    }

    public void setScore(View v) {
        Drawable drawableON = getResources().getDrawable(android.R.drawable.btn_star_big_on);
        Drawable drawableOFF = getResources().getDrawable(android.R.drawable.btn_star_big_off);
        switch (v.getId()) {
            case R.id.btn1Star:
                score = 1;
                btn1Star.setImageDrawable(drawableON);
                btn2Stars.setImageDrawable(drawableOFF);
                btn3Stars.setImageDrawable(drawableOFF);
                btn4Stars.setImageDrawable(drawableOFF);
                btn5Stars.setImageDrawable(drawableOFF);
                break;
            case R.id.btn2Stars:
                score = 2;
                btn1Star.setImageDrawable(drawableON);
                btn2Stars.setImageDrawable(drawableON);
                btn3Stars.setImageDrawable(drawableOFF);
                btn4Stars.setImageDrawable(drawableOFF);
                btn5Stars.setImageDrawable(drawableOFF);
                break;
            case R.id.btn3Stars:
                score = 3;
                btn1Star.setImageDrawable(drawableON);
                btn2Stars.setImageDrawable(drawableON);
                btn3Stars.setImageDrawable(drawableON);
                btn4Stars.setImageDrawable(drawableOFF);
                btn5Stars.setImageDrawable(drawableOFF);
                break;
            case R.id.btn4Stars:
                score = 4;
                btn1Star.setImageDrawable(drawableON);
                btn2Stars.setImageDrawable(drawableON);
                btn3Stars.setImageDrawable(drawableON);
                btn4Stars.setImageDrawable(drawableON);
                btn5Stars.setImageDrawable(drawableOFF);
                break;
            case R.id.btn5Stars:
                score = 5;
                btn1Star.setImageDrawable(drawableON);
                btn2Stars.setImageDrawable(drawableON);
                btn3Stars.setImageDrawable(drawableON);
                btn4Stars.setImageDrawable(drawableON);
                btn5Stars.setImageDrawable(drawableON);
                break;
        }
    }

    private void clearFields() {
        title.setText("");
        opinion.setText("");
    }

}