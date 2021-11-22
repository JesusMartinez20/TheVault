package com.example.thevault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.thevault.ui.main.SectionsPagerAdapter;
import com.example.thevault.databinding.ActivityFeed2Binding;

public class Feed2 extends AppCompatActivity {

    private ActivityFeed2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFeed2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

    }

    public boolean onCreateOptionsMenu(android.view.Menu menu){
        SharedPreferences preferencias = getSharedPreferences("user.dat", MODE_PRIVATE);
        if(preferencias.getBoolean("admin", false)){
            getMenuInflater().inflate(R.menu.menu_admin, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_principal, menu);
        }
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.perfil:
                Intent perfil = new Intent(this,Perfil.class);
                startActivity(perfil);
                break;
            case R.id.menu_agregar_pelicula:
                Intent agregarPelicula = new Intent(this, AdministradorPeliculaActivity.class);
                agregarPelicula.putExtra("accion", 0); //0 para agregar pelicula.
                startActivity(agregarPelicula);
                break;
            case R.id.cerrarSesion:
                cerrarSesion();
                break;
            case R.id.editar_funciones:
                Intent editar = new Intent(this,Perfil.class);
                startActivity(editar);
                break;
            case R.id.leer_boletos:
                Intent leer = new Intent(this,LectorQR.class);
                startActivity(leer);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cerrarSesion(){
        SharedPreferences preferencias = getSharedPreferences("user.dat", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.clear();
        editor.apply();
        Intent logout = new Intent(this,Login.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logout);
        finish();
    }
}