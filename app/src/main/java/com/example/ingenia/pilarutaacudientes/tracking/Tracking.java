package com.example.ingenia.pilarutaacudientes.tracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import com.example.ingenia.pilarutaacudientes.R;
import com.example.ingenia.pilarutaacudientes.RutaMonitorasActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



import static com.google.android.gms.location.LocationServices.FusedLocationApi;


public class Tracking extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    LocationManager locationManager ;
    Boolean GpsStatus = true;
    private static final String TAG = "prueba";
    GoogleApiClient mLocationClient;
    String tag = "tracking";
    LocationRequest mLocationRequest = new LocationRequest();

    Handler handleCheckStatus;
    double lat,lng;
    ArrayList<String> Longitud = new ArrayList();
    ArrayList<String> Latitud = new ArrayList();
    ArrayList<String> Observaciones = new ArrayList();
    ArrayList<String> Fecha = new ArrayList();
    Context ctx;
    String ticker="";
    LocalDate dat11;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";


    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    double latitud, longitud;
    AppLocationService appLocationService;
    PowerManager.WakeLock wakeLock;
    SharedPreferences sharedpreferences;
    String MyPREFERENCES = "RUTA";

    PendingIntent resultPendingIntent;
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {

        // TODO Auto-generated method stub
        super.onCreate();

        System.out.println("se esta ejecutando el servicio de localizacion satelital");

        sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if(sharedpreferences.getString("contador","").equals(""))
        {
            SharedPreferences.Editor sh = sharedpreferences.edit();
            sh.putString("contador","1");
            sh.commit();
        }


        String tag = "com.desoft.servehi:LOCK";

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && Build.MANUFACTURER.equals("Huawei"))
        {
            tag = "LocationManagerService";


        PowerManager.WakeLock wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(1, tag);
        wakeLock.acquire();
    }
        else
        {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");
        }




        //

        Log.e("Google", "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ctx = getApplicationContext();






        startGoogleApiClient();


        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d(TAG, "== Error On onConnected() Permission not granted");
            //Permission not granted by user so cancel the further execution.

            return;
        }

        if (mLocationClient.isConnected()) {
            FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
        }


        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");


        if (location != null) {
            Log.d(TAG, "== location != null");

            latitud = location.getLatitude();
            longitud = location.getLongitude();
            //Send result to activities

        }
    }

    private void sendMessageToUI(String lat, String lng) {

        Log.d(TAG, "Sending info...");
        if (isConnectingToInternet(getApplicationContext())) {

            try {


                latitud = Double.parseDouble(lat);
                longitud = Double.parseDouble(lng);




                    SharedPreferences.Editor ed = sharedpreferences.edit();
                    ed.putString("latitud", Double.toString(latitud));
                    ed.putString("longitud", Double.toString(longitud));
                    ed.commit();






RutasMonitoresTask rutasMonitoresTask = new RutasMonitoresTask(sharedpreferences.getString("id_monitor",""), sharedpreferences.getString("nombre_monitor",""));
                rutasMonitoresTask.execute();




                //
            } catch (Exception e)
            {
                System.out.println("error al convertir "+e.getMessage());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println("no hay internet");
            //Toast.makeText(getApplicationContext(),"no hay internet",Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public void
    onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (wakeLock.isHeld())
            wakeLock.release();
    }

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) for (int i = 0; i < info.length; i++)
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
        }
        return false;
    }





    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    protected void onHandleIntent(@Nullable Intent intent) {
        Log.v(TAG, "GpsTrackerIntentService ran!");
        startGoogleApiClient();

    }

    public void startGoogleApiClient()
    {

        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        System.out.println("el tiempo sera de 10");
        mLocationRequest.setInterval(10 * 1000);


        mLocationRequest.setFastestInterval(5 * 1000);
        System.out.println("intervalo 1" +mLocationRequest.getInterval());





        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes

        handleCheckStatus = new Handler();
        mLocationRequest.setPriority(priority);
        mLocationClient.connect();


            handleCheckStatus.postDelayed(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {




                        System.out.println("me esta llamando");
                        sendMessageToUI(String.valueOf(latitud), String.valueOf(longitud));
                        handleCheckStatus.postDelayed(this, 20000);





                }
            }, 10000);



    }

    public class RutasMonitoresTask extends AsyncTask<Void, Void, Boolean> {


        private final String IdMonitor;
        private final String NombreMonitor;


        RutasMonitoresTask( String idMonitor, String nombreMonitor) {

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

            try{
                if (success) {
                    appLocationService = new AppLocationService(
                            getApplicationContext());
                    Location nwLocation = appLocationService
                            .getLocation(LocationManager.NETWORK_PROVIDER);

                    if (nwLocation != null) {
                        latitud = nwLocation.getLatitude();
                        longitud = nwLocation.getLongitude();

                    }
                    Location locationA = new Location("punto A");

                    locationA.setLatitude(lat);
                    locationA.setLongitude(lng);

                    Location locationB = new Location("punto B");

                    locationB.setLatitude(latitud);
                    locationB.setLongitude(longitud);

                    double distance = locationA.distanceTo(locationB);
                    System.out.println("distancia entre puntos " + distance);
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    if(distance>=800 && distance <= 1000)
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




//
//
                }

            }catch (Exception e){


            }
        }

        @Override
        protected void onCancelled() {

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
        }

//        return  true;
        return auxLogin;

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

}
