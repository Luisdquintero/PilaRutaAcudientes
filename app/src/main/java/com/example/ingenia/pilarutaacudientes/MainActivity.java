package com.example.ingenia.pilarutaacudientes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Bundle datos;
    String id_usuario;
    String Url_colegio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                enviarEmail();
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        int recuperamos_variable_integer = datos.getInt("variable_integer");
        id_usuario = getIntent().getStringExtra("IdUsuario");
        Url_colegio = getIntent().getStringExtra("Url_colegio");

        Log.i("Url_colegio", getIntent().getStringExtra("Url_colegio"));

        //        brayan

        HomeFragment homeFragment = new HomeFragment();
        // Creamos un nuevo Bundle
        Bundle args = new Bundle();
        // Colocamos el String
        args.putString("id_usuario", id_usuario);
        args.putString("Url_colegio", Url_colegio);

        homeFragment.setArguments(args);
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.Content_1,homeFragment,homeFragment.getTag()).commit();

//        fin brayan

//        Log.i("IdUsu_menu", id_usuario);
//        float recuperamos_variable_float = datos.getFloat("objeto_float");
//        Toast toast1 = Toast.makeText(getApplicationContext(), id_usuario, Toast.LENGTH_SHORT);
//        toast1.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Home_page) {
            // Handle the camera action
            HomeFragment homeFragment = new HomeFragment();

            Bundle args = new Bundle();
            // Colocamos el String
            args.putString("id_usuario", id_usuario);
            args.putString("Url_colegio", Url_colegio);
            homeFragment.setArguments(args);

            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.Content_1,homeFragment,homeFragment.getTag()).commit();

        } else if (id == R.id.estudiantes_page) {
            ListaEstudiantesFragment listaEstudiantesFragment = new ListaEstudiantesFragment();
            // Creamos un nuevo Bundle
            Bundle args = new Bundle();
            // Colocamos el String
            args.putString("id_usuario", id_usuario);
            args.putString("Url_colegio", Url_colegio);
//            args.putString("Url_colegio", "ajdkasjdlaksdjasdjalksajsopasdka_BRAYAN");

            listaEstudiantesFragment.setArguments(args);
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.Content_1,listaEstudiantesFragment,listaEstudiantesFragment.getTag()).commit();
        } else if (id == R.id.Monitores_page) {
            ListaMonitorasFragment listaMonitorasFragment = new ListaMonitorasFragment();

            // Creamos un nuevo Bundle
            Bundle args = new Bundle();
            // Colocamos el String
            args.putString("id_usuario", id_usuario);
            args.putString("Url_colegio", Url_colegio);
            listaMonitorasFragment.setArguments(args);

            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.Content_1,listaMonitorasFragment,listaMonitorasFragment.getTag()).commit();
        } else if (id == R.id.Ubiacion_page) {
            Intent i = new Intent(getApplicationContext(), UbicacionActualActivity.class );
            startActivity(i);
        } else if (id == R.id.salir) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class );
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void enviarEmail(){
        //Instanciamos un Intent del tipo ACTION_SEND
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        //Definimos la tipologia de datos del contenido dle Email en este caso text/html
        emailIntent.setType("text/html");
        // Indicamos con un Array de tipo String las direcciones de correo a las cuales
        //queremos enviar el texto
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"john.martinez.estusolucion@gmail.com"});
        // Definimos un titulo para el Email
        emailIntent.putExtra(android.content.Intent.EXTRA_TITLE, "Pila Ruta");
        // Definimos un Asunto para el Email
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Pila Ruta");
        // Obtenemos la referencia al texto y lo pasamos al Email Intent
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Correo desde Pila Ruta");
        try {
            //Enviamos el Correo iniciando una nueva Activity con el emailIntent.
            startActivity(Intent.createChooser(emailIntent, "Enviar E-mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "No hay ningun cliente de correo instalado.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Salir")
                    .setMessage("Estás seguro?")
                    .setNegativeButton(android.R.string.cancel, null)// sin listener
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {// un listener que al pulsar, cierre la aplicacion
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
// Salir
                            MainActivity.this.finish();
                        }
                    })
                    .show();

// Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
            return true;
        }
// para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }
}
