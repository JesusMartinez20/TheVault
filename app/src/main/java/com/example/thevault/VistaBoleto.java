package com.example.thevault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class VistaBoleto extends AppCompatActivity {

    private ImageView imagenQr;
    private TextView txtTitulo, txtFuncion, txtSala,txtAsientos, txtDuracion;
    private int boletoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_boleto);

        imagenQr=(ImageView)findViewById(R.id.imgQr);
        txtTitulo=(TextView)findViewById(R.id.txtNombrePeliculaBoleto);
        txtFuncion=(TextView)findViewById(R.id.txtFuncion);
        txtSala=(TextView)findViewById(R.id.txtSala);
        txtAsientos=(TextView)findViewById(R.id.txtAsientos);
        txtDuracion=(TextView)findViewById(R.id.txtDuracion);

        boletoID = (int) getIntent().getIntExtra("boletoID", 0);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(BEConection.URL + "consultarDatosBoleto.php?id=" + boletoID,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            String x = new String(responseBody);
                            if (!x.equals("0")) {
                                try {
                                    JSONArray jsonArray = new JSONArray(new String(responseBody));

                                    for (int i = 0; i < jsonArray.length(); ++i) {

                                        txtTitulo.setText(jsonArray.getJSONObject(i).getString("nombre"));
                                        txtFuncion.setText("Función: "+jsonArray.getJSONObject(i).getString("horario"));
                                        txtSala.setText("Sala "+jsonArray.getJSONObject(i).getString("id_sala"));
                                        txtAsientos.setText("Número de asientos: "+jsonArray.getJSONObject(i).getString("asientos"));
                                        txtDuracion.setText("Duración: "+jsonArray.getJSONObject(i).getString("duracion"));

                                        String url="Película - "+jsonArray.getJSONObject(i).getString("nombre")+"\nFunción: "+jsonArray.getJSONObject(i).getString("horario")+"\nSala "+jsonArray.getJSONObject(i).getInt("id_sala")+"\nAsientos: "+jsonArray.getJSONObject(i).getInt("asientos");

                                        MultiFormatWriter writer=new MultiFormatWriter();
                                        try{
                                            BitMatrix matrix = writer.encode(url,BarcodeFormat.QR_CODE,350,350);
                                            BarcodeEncoder encoder=new BarcodeEncoder();
                                            Bitmap bitmap = encoder.createBitmap(matrix);
                                            imagenQr.setImageBitmap(bitmap);

                                            InputMethodManager manager = (InputMethodManager) getSystemService(
                                                    Context.INPUT_METHOD_SERVICE
                                            );

                                        }catch (WriterException e){
                                            Toast.makeText(VistaBoleto.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(VistaBoleto.this, "Error al obtener información: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(VistaBoleto.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void ejecutarWebService(String url, final String msg){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(VistaBoleto.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VistaBoleto.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void regresar(View view){
        finish();
    }
}