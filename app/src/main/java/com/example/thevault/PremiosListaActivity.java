package com.example.thevault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PremiosListaActivity extends AppCompatActivity {

    private LinearLayout layoutPremios;
    private int peliculaID;
    private Button btnAgregarPremio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premios_lista);

        layoutPremios=(LinearLayout) findViewById(R.id.layoutPremioLista);
        btnAgregarPremio=(Button) findViewById(R.id.btnListaAgregarPremios);
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
                                    JSONArray awardsArray = new JSONArray(movie.getString("premios"));

                                    for (int i = 0; i < awardsArray.length(); ++i) {
                                        final JSONObject currentAward = awardsArray.getJSONObject(i);

                                        View premio = getLayoutInflater().inflate(R.layout.premio_vista, null);
                                        TextView txtAcademia= (TextView) premio.findViewById(R.id.txtPremioCardAcademia);
                                        txtAcademia.setText(currentAward.getString("academia"));
                                        TextView txtCategoria = (TextView) premio.findViewById(R.id.txtPremioCardCategoria);
                                        txtCategoria.setText(currentAward.getString("categoria"));
                                        TextView txtLugar = (TextView) premio.findViewById(R.id.txtPremioCardLugar);
                                        txtLugar.setText(currentAward.getString("lugar"));
                                        TextView txtFecha= (TextView) premio.findViewById(R.id.txtPremioCardFecha);
                                        txtFecha.setText(currentAward.getString("fecha"));

                                        premio.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                Intent intent = new Intent(PremiosListaActivity.this, AdministradorPremiosActivity.class);
                                                try {
                                                    intent.putExtra("premioID", currentAward.getInt("id"));
                                                    intent.putExtra("peliculaID", peliculaID);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                intent.putExtra("accion",1);
                                                startActivity(intent);

                                            }
                                        });
                                        layoutPremios.addView(premio);
                                    }

                                } else {
                                    Toast.makeText(PremiosListaActivity.this, "Película no encontrada", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(PremiosListaActivity.this, "Error al obtener información: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(PremiosListaActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void agregarPremio(View view){
        Intent intent = new Intent(PremiosListaActivity.this, AdministradorPremiosActivity.class);
        intent.putExtra("peliculaID", peliculaID);
        intent.putExtra("accion",0);
        startActivity(intent);
    }
}