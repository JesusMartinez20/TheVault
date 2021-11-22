package com.example.thevault;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Foro#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Foro extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText etBusqueda;
    private ImageButton btnBusqueda;
    private String nombrePelicula;

    public Foro() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Foro.
     */
    // TODO: Rename and change types and number of parameters
    public static Foro newInstance(String param1, String param2) {
        Foro fragment = new Foro();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foro, container, false);;
        etBusqueda=(EditText) view.findViewById(R.id.etBusqueda);
        btnBusqueda=(ImageButton) view.findViewById(R.id.btnBusqueda);

        final ListView lista;
        final Adapter adaptador=new Adapter();
        lista=(ListView) view.findViewById(R.id.lista);
        final JSONArray[] pel = {new JSONArray()};

        btnBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Card> resultado = new ArrayList<Card>();
                for (int i = 0; pel[0].length() > i; i++){
                    Card carta=new Card();
                    try{
                        carta.id=pel[0].getJSONObject(i).getInt("id");
                        carta.nombre= pel[0].getJSONObject(i).getString("nombre");
                        carta.imagen= pel[0].getJSONObject(i).getString("imagen");
                        carta.duracion= pel[0].getJSONObject(i).getString("duracion");
                        carta.anio= pel[0].getJSONObject(i).getString("fecha_estreno");
                        carta.premios= pel[0].getJSONObject(i).getString("premios");
                        carta.comentarios= pel[0].getJSONObject(i).getString("comentarios");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(carta.nombre.toLowerCase().contains(etBusqueda.getText().toString().toLowerCase())) {
                        resultado.add(carta);
                    }
                }
                adaptador.arreglo=resultado;
                adaptador.context=getContext();
                lista.setAdapter(adaptador);
            }
        });

        etBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Card> resultado = new ArrayList<Card>();
                for (int i = 0; pel[0].length() > i; i++){
                    Card carta=new Card();
                    try{
                        carta.id=pel[0].getJSONObject(i).getInt("id");
                        carta.nombre= pel[0].getJSONObject(i).getString("nombre");
                        carta.imagen= pel[0].getJSONObject(i).getString("imagen");
                        carta.duracion= pel[0].getJSONObject(i).getString("duracion");
                        carta.anio= pel[0].getJSONObject(i).getString("fecha_estreno");
                        carta.premios= pel[0].getJSONObject(i).getString("premios");
                        carta.comentarios= pel[0].getJSONObject(i).getString("comentarios");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(carta.nombre.toLowerCase().contains(etBusqueda.getText().toString().toLowerCase())) {
                        resultado.add(carta);
                    }
                }
                adaptador.arreglo=resultado;
                adaptador.context=getContext();
                lista.setAdapter(adaptador);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(BEConection.URL + "foro.php" ,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        if(statusCode == 200){
                            try{
                                String x = new String(responseBody);
                                if( !x.equals("0")){
                                    JSONArray peliculas = new JSONArray(new String(responseBody));
                                    pel[0] =peliculas;
                                    ArrayList<Card> resultado = new ArrayList<Card>();
                                    for (int i=0;i<peliculas.length();i++){
                                        Card carta=new Card();
                                        carta.id=peliculas.getJSONObject(i).getInt("id");
                                        carta.nombre=peliculas.getJSONObject(i).getString("nombre");
                                        carta.imagen=peliculas.getJSONObject(i).getString("imagen");
                                        carta.duracion=peliculas.getJSONObject(i).getString("duracion");
                                        carta.anio=peliculas.getJSONObject(i).getString("fecha_estreno");
                                        carta.premios=peliculas.getJSONObject(i).getString("premios");
                                        carta.comentarios=peliculas.getJSONObject(i).getString("comentarios");
                                        resultado.add(carta);
                                    }
                                    adaptador.arreglo=resultado;
                                    adaptador.context=getActivity();
                                    lista.setAdapter(adaptador);
                                } else{
                                    Toast.makeText(getContext(), "Pelicula no encontrada.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e){
                                Toast.makeText(getActivity(), "Error al obtener información.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), "Sin resultados en busqueda.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
        return view;
    }

}