package com.example.thevault;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class AdministradorStaffActivity extends AppCompatActivity {

    private TextView accionTitulo, fecha;
    private EditText nombre, nacionalidad, imagen;
    private Spinner spinner;
    private RadioButton hombre, mujer;
    private Button accion, eliminar;
    private int accionRecibida, staffID, peliculaID;

    private final String[] listadoMeses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };
    private String eleccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_staff);
    }
}