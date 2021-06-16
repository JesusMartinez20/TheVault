package com.example.thevault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class PeliculaActivity extends AppCompatActivity {

    private TextView name, director, length, premiere, country, score, awards;
    private ImageView image;
    private LinearLayout layoutComments, layoutStaff;
    private int movieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelicula);

        image = (ImageView) findViewById(R.id.imgMoviePoster);
        name = (TextView) findViewById(R.id.txtMovieTitle);
        director = (TextView) findViewById(R.id.txtMovieDirector);
        awards = (TextView) findViewById(R.id.txtMovieAwards);
        length = (TextView) findViewById(R.id.txtMovieLength);
        premiere = (TextView) findViewById(R.id.txtMoviePremiere);
        country = (TextView) findViewById(R.id.txtMovieCountry);
        score = (TextView) findViewById(R.id.txtMovieScore);

        layoutComments = (LinearLayout) findViewById(R.id.layoutMovieComments);
        layoutStaff = (LinearLayout) findViewById(R.id.layoutMovieStaff);

        movieID = (int) getIntent().getIntExtra("peliculaID", 0);

        putInfo();
        getComments();
    }

    private void putInfo() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://192.168.1.80:80/4to/thevault-be/consultarPelicula.php?id=" + movieID,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            try {
                                String x = new String(responseBody);
                                if (!x.equals("0")) {
                                    JSONObject movie = new JSONObject(new String(responseBody));

                                    //Se establecen los vaores de la imagen y de los textview en la vista pricipal
                                    Picasso.get().load(movie.getString("imagen")).into(image);
                                    name.setText(movie.getString("nombre"));
                                    length.setText(movie.getString("duracion") + " min.");
                                    premiere.setText(movie.getString("fecha"));
                                    country.setText(movie.getString("pais"));
                                    score.setText(movie.getString("calificacion"));

                                    //Se establecen los valores para premios
                                    JSONArray awardsArray = new JSONArray(movie.getString("premios"));

                                    for (int i = 0; i < awardsArray.length(); ++i) {
                                        JSONObject currentAward = awardsArray.getJSONObject(i);
                                        awards.setText(awards.getText() + currentAward.getString("categoria") + " - " + currentAward.getString("academia") + "\n");
                                    }

                                    JSONArray staffArray = new JSONArray(movie.getString("staff"));

                                    //Se establece el valor de director y las tarjetas de reparto
                                    for (int i = 0; i < staffArray.length(); ++i) {
                                        if (staffArray.getJSONObject(i).getString("rol").equals("Director")) {
                                            director.setText(staffArray.getJSONObject(i).getString("nombre"));
                                        }
                                        else {
                                            final View staffView = getLayoutInflater().inflate(R.layout.staff_tarjeta, null);
                                            TextView staffData = (TextView) staffView.findViewById(R.id.txtStaffData);
                                            staffData.setText(staffArray.getJSONObject(i).getString("nombre") + " - " + staffArray.getJSONObject(i).getString("rol"));
                                            staffView.setId(staffArray.getJSONObject(i).getInt("staff_id"));

                                            //TODO: cambiar método para que redirija a la vista de staff
                                            staffView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Toast.makeText(PeliculaActivity.this, "ID: " + staffView.getId(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            layoutStaff.addView(staffView);
                                        }
                                    }

                                } else {
                                    Toast.makeText(PeliculaActivity.this, "Película no encontrada", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(PeliculaActivity.this, "Error al obtener información: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(PeliculaActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getComments() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://192.168.1.80:80/4to/thevault-be/consultarComentariosPelicula.php?id=" + movieID,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            String x = new String(responseBody);
                            if (!x.equals("0")) {
                                try {
                                    JSONArray jsonArray = new JSONArray(new String(responseBody));

                                    for (int i = 0; i < jsonArray.length(); ++i) {
                                        View comment = getLayoutInflater().inflate(R.layout.comentario_vista, null);
                                        TextView titleComment = (TextView) comment.findViewById(R.id.txtCommentCardTitle);
                                        titleComment.setText(jsonArray.getJSONObject(i).getString("titulo"));
                                        TextView userComment = (TextView) comment.findViewById(R.id.txtCommentCardUser);
                                        userComment.setText("Usuario: " + jsonArray.getJSONObject(i).getString("usuario"));
                                        TextView scoreComment = (TextView) comment.findViewById(R.id.txtCommentCardScore);
                                        scoreComment.setText("Calificación: " + jsonArray.getJSONObject(i).getString("calificacion"));
                                        TextView opinionComment = (TextView) comment.findViewById(R.id.txtCommentCardOpinion);
                                        opinionComment.setText(jsonArray.getJSONObject(i).getString("opinion"));
                                        ImageView avatar = (ImageView) comment.findViewById(R.id.imgCommentCardAvatar);
                                        //TODO: cambiar a avatares designados.
                                        switch (jsonArray.getJSONObject(i).getInt("avatar")) {
                                            case 1:
                                                avatar.setImageResource(R.mipmap.alien1);
                                                break;
                                            case 2:
                                                avatar.setImageResource(R.mipmap.alien2);
                                                break;
                                        }

                                        layoutComments.addView(comment);
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(PeliculaActivity.this, "Error al obtener información: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(PeliculaActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void goToCommentView(View view) {
        Intent intent = new Intent(PeliculaActivity.this, AgregarComentarioActivity.class);
        intent.putExtra("peliculaID", movieID);
        startActivity(intent);
    }
}