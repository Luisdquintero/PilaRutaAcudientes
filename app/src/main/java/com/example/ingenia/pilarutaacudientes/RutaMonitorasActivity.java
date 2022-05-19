package com.example.ingenia.pilarutaacudientes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ingenia.pilarutaacudientes.tracking.AppLocationService;
import com.example.ingenia.pilarutaacudientes.tracking.Tracking;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class RutaMonitorasActivity extends FragmentActivity implements OnMapReadyCallback,


        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener
{
int value=0;

    public static final int REQUEST_CODE_CONFIGURACION = 4;
double latitude,longitude;
    double lat,lng;
    SharedPreferences sharedpreferences;
    String MyPREFERENCES = "RUTA";
    LocationManager locationManager ;
    PendingIntent resultPendingIntent;
    String ticker="";
    AppLocationService appLocationService;
    private GoogleMap mMap;
    Handler handleCheckStatus;
    Runnable my_runnable;
    ArrayList<String> Longitud = new ArrayList();
    ArrayList<String> Latitud = new ArrayList();

    ArrayList<String> Fecha = new ArrayList();
    Polyline polyline;
    double latitud=0.0,longitud=0.0;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String START_JOB_BROADCAST_RECEIVER = "com.example.ingenia.pilarutaacudientes.intent.action.START_JOB_FIRSTTIME";
    public static final String GET_NEW_LOCATION_BROADCAST_RECEIVER = "com.example.ingenia.pilarutaacudientes.intent.action.GET_NEW_LOCATION";
    public static final String GET_NEW_LOCATION_PARAM = "new_location";
    private static final String TAG = RutaMonitorasActivity.class.getSimpleName();

    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent( context, RutaMonitorasActivity.class );
        intent.putExtra( NOTIFICATION_MSG, msg );
        return intent;
    }

    private GoogleApiClient googleApiClient;
    private Location lastLocation;


    String IdMonitora = "";
    String NombreMonitor = "";

    Marker MarcadorInicio;
    Marker MarcadorFinal;

    Marker prueba;

    private LocationManager locManager;
    private Location loc;
    Boolean GpsStatus;

//    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta_monitoras);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(sharedpreferences.getString("contador","").equals(""))
        {
            SharedPreferences.Editor sh = sharedpreferences.edit();
            sh.putString("contador","1");
            sh.commit();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {


            if (checkPermission2()&& checkPermission3()) {
                System.out.println("ENTRO 1");
                CheckGpsStatus() ;
                appLocationService = new AppLocationService(
                        RutaMonitorasActivity.this);
                if(GpsStatus == true)
                {
                    Location nwLocation = appLocationService
                            .getLocation(LocationManager.NETWORK_PROVIDER);

                    if (nwLocation != null) {
                        latitud = nwLocation.getLatitude();
                        longitud = nwLocation.getLongitude();

                    }



                    initmap();
                    load_tracking();





                }
                else {
                    showSettingsAlert();

                }
                //do your work
            } else {
                requestPermission();
                System.out.println("ENTRO 4");
            }
        }
        else
        {
            System.out.println("entro a prender el gps");
            CheckGpsStatus() ;

            appLocationService = new AppLocationService(
                    RutaMonitorasActivity.this);
            if(GpsStatus == true) {
                Location nwLocation = appLocationService
                        .getLocation(LocationManager.NETWORK_PROVIDER);

                if (nwLocation != null) {
                    latitud = nwLocation.getLatitude();
                    longitud = nwLocation.getLongitude();
                }

                initmap();
                load_tracking();




            }
            else {
                showSettingsAlert();

            }
        }

    }


public void initmap()
{
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    // initialize GoogleMaps
//        initGMaps();

    // create GoogleApiClient

}
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        Log.d(TAG, "onMapReady()");
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);

        Log.i("Marcadores", "Crea Marcadores");

        IdMonitora = getIntent().getStringExtra("IdMonitor");
        NombreMonitor = getIntent().getStringExtra("NombreMonitor");

        SharedPreferences.Editor sh =sharedpreferences.edit();
        sh.putString("id_monitor",IdMonitora);
        sh.putString("nombre_monitor",NombreMonitor );
        sh.commit();



        handleCheckStatus = new Handler();

        my_runnable = new Runnable() {
            @Override
            public void run() {
                // your code here
                RutasMonitoresTask rutasEstudiantesTask = new RutasMonitoresTask(IdMonitora,NombreMonitor);

                rutasEstudiantesTask.execute();

                handleCheckStatus.postDelayed(my_runnable,20000);
            }
        };

        handleCheckStatus.post(my_runnable);


        setupMap();
    }









    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick("+latLng +")");
//        markerForGeofence(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }





    @Override
    public void onLocationChanged(Location location) {

    }
    public class RutasMonitoresTask extends AsyncTask<Void, Void, Boolean> {


        private final String IdMonitor;
        private final String NombreMonitor;


        RutasMonitoresTask(String idMonitor, String nombreMonitor) {

            IdMonitor = idMonitor;
            NombreMonitor = nombreMonitor;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

//            try {
//                // Simulate network access.
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                return false;
//            }

            return ConnectionDataBaseRutasMonitores(IdMonitor);


            // TODO: register the new account here.


        }

        @Override
        protected void onPostExecute(final Boolean success) {

            try {
                if (success) {
                    mMap.clear();

                    MarkerOptions b = new MarkerOptions()
                            .position(new LatLng(lat, lng)).title(NombreMonitor).icon(BitmapDescriptorFactory.fromResource(R.drawable.car));


                    Marker m = mMap.addMarker(b);



                    CameraPosition PosicionCamara = new CameraPosition.Builder()
                            .target(new LatLng(lat, lng))
                            .zoom(14)
                            //.bearing(45)
                            .tilt(90)
                            .tilt(90)
                            .build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(PosicionCamara);
                    mMap.animateCamera(cameraUpdate);


                    Log.i("Latitud-carro-1", String.valueOf(lat));
                    Log.i("Longitud-carro-1", String.valueOf(lng));
                    Log.i("Latitud-carro-2", String.valueOf(latitude));
                    Log.i("Longitud-carro-2", String.valueOf(longitude));

                    appLocationService = new AppLocationService(
                            RutaMonitorasActivity.this);
                    Location nwLocation = appLocationService
                            .getLocation(LocationManager.NETWORK_PROVIDER);

                    if (nwLocation != null) {
                        latitude = nwLocation.getLatitude();
                        longitude = nwLocation.getLongitude();

                    }
                    Location locationA = new Location("punto A");

                    locationA.setLatitude(lat);
                    locationA.setLongitude(lng);

                    Location locationB = new Location("punto B");

                    locationB.setLatitude(latitude);
                    locationB.setLongitude(longitude);

                    double distance = locationA.distanceTo(locationB);
                    System.out.println("distancia entre puntos " + distance);
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    if(distance>=500 && distance <= 3500)
                    {

                        try{
                            //sumamos al contador
                            int contador = Integer.parseInt(sharedpreferences.getString("contador",""));


                            if(contador<6)
                            {
                                contador++;
                                launch_notification();
                                SharedPreferences.Editor ed = sharedpreferences.edit();
                                ed.putString("contador",String.valueOf(contador));
                                ed.commit();
                            }
                            else
                            {
                                //deja de notificar
                                SharedPreferences.Editor ed = sharedpreferences.edit();
                                ed.putString("contador","1");
                                ed.commit();
                            }
                        }catch (Exception e)
                        {

                        }


                    }
                    else {
                        SharedPreferences.Editor ed = sharedpreferences.edit();
                        ed.putString("contador","1");
                        ed.commit();
                    }

                    Intent service = new Intent(RutaMonitorasActivity.this, Tracking.class);
                    startService(service);
                }

            } catch (Exception e) {


            }


        }
    }

    public boolean ConnectionDataBaseRutasMonitores(String IdMonitor){
        String SOAP_ACTION2 = "http://tempuri.org/GeoreferenciaMonitores"; //nome da açao
        String METHOD_NAME2 = "GeoreferenciaMonitores";// nome do método a ser envocado
        String NAMESPACE2 =   "http://tempuri.org/";//NOME DO WEBSERVICE
        String URL2 = "https://siscov.net/zonacliente/Asreales/RutasWS/Servicios/Mobile/mobile.asmx"; /// URL DO METODO

        boolean auxLogin = false;

        try{
            SoapObject Request = new SoapObject(NAMESPACE2, METHOD_NAME2);
            Request.addProperty("IdUsuario", IdMonitor);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
//            Log.i("soapEnvelope", soapEnvelope.toString());
            HttpTransportSE transport = new HttpTransportSE(URL2);

            transport.call(SOAP_ACTION2, soapEnvelope);
            Log.i("soapEnvelope", soapEnvelope.getResponse().toString());

            SoapObject response = (SoapObject) soapEnvelope.getResponse();
            SoapObject root = (SoapObject) response.getProperty(1);
            SoapObject emp = (SoapObject) root.getProperty(0);

            Fecha.clear();
            Latitud.clear();
            Latitud.clear();
            for(int i=0;i<emp.getPropertyCount();i++){
                SoapObject emp1 = (SoapObject) emp.getProperty(i);
                Log.i("emp1", emp1.toString());


                Fecha.add(emp1.getProperty(2).toString());
                Log.i("Fecha", emp1.getProperty(2).toString() );

                Latitud.add(emp1.getProperty(3).toString());
                Log.i("Latitud", emp1.getProperty(3).toString());

                Longitud.add(emp1.getProperty(4).toString());
                Log.i("Longitud", emp1.getProperty(4).toString());

                lat= Double.parseDouble(emp1.getProperty(3).toString());
                lng= Double.parseDouble(emp1.getProperty(4).toString());
//                Observaciones.add(emp1.getProperty(5).toString());
//                Log.i("Observaciones", emp1.getProperty(5).toString());
            }


            auxLogin = true;
        } catch (Exception e){
            Log.i("Consulta RutaMon", "ERROR en la consulta: " + e.getMessage());
            auxLogin=false;
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            Intent service = new Intent(RutaMonitorasActivity.this, Tracking.class);
            stopService(service);
        }

//        return  true;
        return auxLogin;

    }

    @SuppressLint("MissingPermission")
    void setupMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setTrafficEnabled(false);


        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {




                    latitude = location.getLatitude();
                    longitude = location.getLongitude();



            }
        });

    }

    public void launch_notification()
    {
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");



                 Intent resultIntent = new Intent(this, RutaMonitorasActivity.class);



        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_ONE_SHOT

                );
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);


        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();



            bigText.bigText("El conductor se esta acercando");



        bigText.setBigContentTitle("Aviso");

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Aviso");
        mBuilder.setSound(Uri.parse("android.resource://com.example.ingenia.pilarutaacudientes/" + R.raw.alert_tone));


            mBuilder.setContentText("El conductor se esta acercando");


        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setAutoCancel(true);
        mBuilder.addAction(R.mipmap.ic_launcher_round, ticker, resultPendingIntent);
        mBuilder.setVibrate(new long[] {100, 250, 100, 500});




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());


    }


    public void CheckGpsStatus(){



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        System.out.println("valor de GPS provider "+provider);
        if (provider.contains("gps") || provider.contains("network"))
        {

            GpsStatus = true;
        }
        else

        {
            GpsStatus = false;
        }

        System.out.println("valor de GPS Status "+GpsStatus);
    }


    public void load_tracking()
    {

        if(sharedpreferences.getString("configuracion","").equals(""))
        {

            LocalBroadcastManager.getInstance(RutaMonitorasActivity.this).registerReceiver(mNewLocationReceiver,
                    new IntentFilter(GET_NEW_LOCATION_BROADCAST_RECEIVER));

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(RutaMonitorasActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    sendStartJobBroadcast();
                }else{
                    ActivityCompat.requestPermissions(RutaMonitorasActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }else{
                sendStartJobBroadcast();
            }




            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                Intent intent = new Intent();
                Intent intent2 = new Intent();
                String packageName = getPackageName();
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                if(!pm.isIgnoringBatteryOptimizations(packageName))
                {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:"+packageName));
                    startActivity(intent);


                    intent2.setAction(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
                    intent2.setData(Uri.parse("package:"+packageName));
                    startActivity(intent2);


                    if(android.os.Build.MANUFACTURER.toString().equals("HUAWEI")  )
                    {



                        Intent intent3 = new Intent();
                        intent3.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                        startActivity(intent3);


                    }


                }
            }



            SharedPreferences.Editor s= sharedpreferences.edit();
            s.putString("configuracion","1");
            s.commit();
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNewLocationReceiver);
    }

    private BroadcastReceiver mNewLocationReceiver = new BroadcastReceiver(){

        @Override public void onReceive(Context context, Intent intent) {
            if(intent!=null){

            }
        }
    };

    private void sendStartJobBroadcast(){


        Intent intent = new Intent();
        intent.setAction(START_JOB_BROADCAST_RECEIVER);
        sendBroadcast(intent);
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                RutaMonitorasActivity.this);

        alertDialog.setTitle("Configuración");

        alertDialog
                .setMessage("La Geolocalización no esta activa debes activar el GPS");

        alertDialog.setPositiveButton("Configuración",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS),0);
                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        CheckGpsStatus() ;

                        if(GpsStatus == true) {
                            appLocationService = new AppLocationService(
                                    RutaMonitorasActivity.this);
                            Location nwLocation = appLocationService
                                    .getLocation(LocationManager.NETWORK_PROVIDER);

                            if (nwLocation != null) {
                                latitud = nwLocation.getLatitude();
                                longitud = nwLocation.getLongitude();

                            }

                           initmap();
                            load_tracking();



                        }
                        else {
                            showSettingsAlert();

                        }
                    }
                });




        alertDialog.show();
    }


    protected void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Habilitar permisos de ");
            alertDialogBuilder
                    .setMessage("" +
                            "\nPara que funcione debes habilitar los permisos de ubicación,escritura y teléfono")
                    .setCancelable(false)
                    .setPositiveButton("Ir a Permisos de APP", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, 1000);     // Comment 3.
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            System.out.println("ENTRO 6");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                }
                System.out.println("ENTRO 7");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
                System.out.println("ENTRO 7");
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    CheckGpsStatus() ;

                    if(GpsStatus == true) {
                        appLocationService = new AppLocationService(
                                RutaMonitorasActivity.this);
                        Location nwLocation = appLocationService
                                .getLocation(LocationManager.NETWORK_PROVIDER);

                        if (nwLocation != null) {
                            latitud = nwLocation.getLatitude();
                            longitud = nwLocation.getLongitude();


                        }

                        initmap();
                        load_tracking();




                    }
                    else {

                     Toast.makeText(RutaMonitorasActivity.this,"Active su GPS",Toast.LENGTH_LONG).show();
                        showSettingsAlert();
                    }
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Habilitar permisos ");
                    alertDialogBuilder
                            .setMessage("" +
                                    "\nPara que funcione debes habilitar los permisos de ubicación,escritura y teléfono")
                            .setCancelable(false)
                            .setPositiveButton("Ir a Permisos de APP", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, 1000);     // Comment 3.
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
                break;
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.print("ENTRO A ONACTIVITY");
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_CONFIGURACION)
        {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.

            }


        }

        if (requestCode == 0){

            switch (resultCode) {
                case RESULT_OK:
                    // Resultado correcto
                    System.out.println("VALOR 0");

                    break;

                case RESULT_CANCELED:
                    // Cancelación o cualquier situación de error
                    System.out.println("no activo gps");
                    CheckGpsStatus() ;
                    if(GpsStatus == true)
                    {


                       initmap();
                        load_tracking();


                    }
                    else
                    {
                        System.out.println("GPS DESACTIVADO");
                        showSettingsAlert();
                    }
                    break;
            }
        }


    }




    protected boolean checkPermission2() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            System.out.println("ENTRO 12");
            return false;
        }
    }

    protected boolean checkPermission3() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            System.out.println("ENTRO 12");
            return false;
        }
    }
 
}
