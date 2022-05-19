package com.example.ingenia.pilarutaacudientes;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

    public class RutaEstudiantesActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
        Handler handleCheckStatus;
        Runnable my_runnable;
    ArrayList<String> Longitud = new ArrayList();
    ArrayList<String> Latitud = new ArrayList();
    ArrayList<String> CodigoEstado = new ArrayList();
    ArrayList<String> Fecha = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta_estudiantes);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
//        LatLng salidaCasa = new LatLng(4.602865, -74.1624656);
//        LatLng llegadaColegio = new LatLng(4.5843061, -74.2156657);
//        LatLng salidaColegio = new LatLng(4.582091, -74.221933);
//        LatLng llegadacasa = new LatLng(4.5996198, -74.1633042);
//
//        createDashedLineida(mMap, salidaCasa, llegadaColegio, Color.GREEN);
//        createDashedLineRegreso(mMap, salidaColegio, llegadacasa, Color.YELLOW);

//        Polyline line = mMap.addPolyline(new PolylineOptions()
//                .add( salidaCasa, llegadaColegio)
//                .width(2)
//                .color(Color.GREEN).clickable(true)
//                );
//        line.setTag("Ruta al Colegio");
//        line.setJointType(JointType.BEVEL);
//        line.setGeodesic(true);
//        line.setStartCap(
//                new CustomCap(
//                        BitmapDescriptorFactory.fromResource(R.drawable.downarrowverde3), 10));
//        line.setEndCap(new CustomCap(
//                BitmapDescriptorFactory.fromResource(R.drawable.uparrowverde), 10));



//        Polyline line2 = mMap.addPolyline(new PolylineOptions()
//                .add(salidaColegio, llegadacasa)
//                .width(2)
//                .color(Color.BLUE));
//        line2.setTag("Ruta a Casa");
//        line2.setStartCap(
//                new CustomCap(
//                        BitmapDescriptorFactory.fromResource(R.drawable.downarrowamarilla3), 10));
//        line2.setEndCap(new CustomCap(
//                BitmapDescriptorFactory.fromResource(R.drawable.uparrowamarillo), 10));


//        mMap.addMarker(new MarkerOptions().position(salidaCasa).title("salidaCasa").icon(BitmapDescriptorFactory.fromResource(R.drawable.salidacasa)));
//        mMap.addMarker(new MarkerOptions().position(llegadaColegio).title("llegadaColegio").icon(BitmapDescriptorFactory.fromResource(R.drawable.llegadacolegio)));
//        mMap.addMarker(new MarkerOptions().position(salidaColegio).title("salidaColegio").icon(BitmapDescriptorFactory.fromResource(R.drawable.salidacolegio3)));
//        mMap.addMarker(new MarkerOptions().position(llegadacasa).title("llegadacasa").icon(BitmapDescriptorFactory.fromResource(R.drawable.llegadacasa)));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(salidaCasa));


//        CameraPosition PosicionCamara  = new CameraPosition.Builder()
//                .target(salidaCasa)
//                .zoom(14)
//                //.bearing(45)
//                .tilt(90)
//                .build();

//        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(PosicionCamara);
//        mMap.animateCamera(cameraUpdate);
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));




        handleCheckStatus = new Handler();

        my_runnable = new Runnable() {
            @Override
            public void run() {
                // your code here
                RutasEstudiantesTask rutasEstudiantesTask = new RutasEstudiantesTask(getIntent().getStringExtra("IdEstudiante"));
                rutasEstudiantesTask.execute();

                handleCheckStatus.postDelayed(my_runnable,10000);
            }
        };

        handleCheckStatus.post(my_runnable);
//        Toast toast1 = Toast.makeText(getApplicationContext(), getIntent().getStringExtra("IdEstudiante"), Toast.LENGTH_SHORT);
//        toast1.show();
    }

    public static void createDashedLineida(GoogleMap map, LatLng latLngOrig, LatLng latLngDest, int color){
        double difLat = latLngDest.latitude - latLngOrig.latitude;
        double difLng = latLngDest.longitude - latLngOrig.longitude;

        double zoom = map.getCameraPosition().zoom;

        double divLat = difLat/(zoom * 2);
        double divLng = difLng/(zoom * 2);

        LatLng tmpLatOri = latLngOrig;

        for(int i = 0; i < (zoom * 2); i++){
            LatLng loopLatLng = tmpLatOri;

            if(i > 0){
                loopLatLng = new LatLng(tmpLatOri.latitude + (divLat * 0.5f), tmpLatOri.longitude + (divLng * 0.5f));
            }

            Polyline polyline = map.addPolyline(new PolylineOptions()
                    .add(loopLatLng)
                    .add(new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng))
                    .color(color)
                    .width(2f));
            polyline.setStartCap(
                new CustomCap(
                        BitmapDescriptorFactory.fromResource(R.drawable.downarrowverde3), 10));
            polyline.setEndCap(new CustomCap(
                BitmapDescriptorFactory.fromResource(R.drawable.uparrowverde), 10));

            tmpLatOri = new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng);
        }
    }

    public static void createDashedLineRegreso(GoogleMap map, LatLng latLngOrig, LatLng latLngDest, int color){
        double difLat = latLngDest.latitude - latLngOrig.latitude;
        double difLng = latLngDest.longitude - latLngOrig.longitude;

        double zoom = map.getCameraPosition().zoom;

        double divLat = difLat/(zoom * 2);
        double divLng = difLng/(zoom * 2);

        LatLng tmpLatOri = latLngOrig;

        for(int i = 0; i < (zoom * 2); i++){
            LatLng loopLatLng = tmpLatOri;

            if(i > 0){
                loopLatLng = new LatLng(tmpLatOri.latitude + (divLat * 0.5f), tmpLatOri.longitude + (divLng * 0.5f));
            }

            Polyline polyline = map.addPolyline(new PolylineOptions()
                    .add(loopLatLng)
                    .add(new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng))
                    .color(color)
                    .width(2f));
            polyline.setStartCap(
                    new CustomCap(
                            BitmapDescriptorFactory.fromResource(R.drawable.downarrowamarilla3), 10));
            polyline.setEndCap(new CustomCap(
                    BitmapDescriptorFactory.fromResource(R.drawable.uparrowamarillo), 10));

            tmpLatOri = new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng);
        }
    }

    public class RutasEstudiantesTask extends AsyncTask<Void, Void, Boolean> {


        private final String IdEstudiante;


        RutasEstudiantesTask( String idEstudiante) {

            IdEstudiante = idEstudiante;
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

            return ConnectionDataBaseRutasEstudiantes(IdEstudiante);


            // TODO: register the new account here.


        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {

                mMap.clear();

                for(int i=0;i<Latitud.size();i++){
                    LatLng punto = new LatLng(Double.parseDouble(Latitud.get(i)), Double.parseDouble(Longitud.get(i)));


                    if(CodigoEstado.get(i).equals("0")){
                        mMap.addMarker(new MarkerOptions().position(punto).title("Recogido en Casa").icon(BitmapDescriptorFactory.fromResource(R.drawable.salidacasa)).snippet(Fecha.get(i)));
                    }else if(CodigoEstado.get(i).equals("10")){
                        mMap.addMarker(new MarkerOptions().position(punto).title("Entregado en Colegio").icon(BitmapDescriptorFactory.fromResource(R.drawable.llegadacolegio)).snippet(Fecha.get(i)));
//                        mMap.addMarker(new MarkerOptions().position(punto).title("Recogido en Casa").icon(BitmapDescriptorFactory.fromResource(R.drawable.salidacasa)));
                        if(i!=0){
                            createDashedLineida(mMap, new LatLng(Double.parseDouble(Latitud.get(i-1)), Double.parseDouble(Longitud.get(i-1))), new LatLng(Double.parseDouble(Latitud.get(i)), Double.parseDouble(Longitud.get(i))), Color.GREEN);
                        }
                    }
                    else if(CodigoEstado.get(i).equals("901")){
                        mMap.addMarker(new MarkerOptions().position(punto).title("Salida del Colegio").icon(BitmapDescriptorFactory.fromResource(R.drawable.salidacolegio3)).snippet(Fecha.get(i)));
//                        mMap.addMarker(new MarkerOptions().position(punto).title("Recogido en Casa").icon(BitmapDescriptorFactory.fromResource(R.drawable.salidacasa)));
                    }
                    else if(CodigoEstado.get(i).equals("903")){
                        mMap.addMarker(new MarkerOptions().position(punto).title("Entrega casa").icon(BitmapDescriptorFactory.fromResource(R.drawable.llegadacasa)).snippet(Fecha.get(i)));
//                        mMap.addMarker(new MarkerOptions().position(punto).title("Recogido en Casa").icon(BitmapDescriptorFactory.fromResource(R.drawable.salidacasa)));
                        if(i!=0){
                            createDashedLineRegreso(mMap, new LatLng(Double.parseDouble(Latitud.get(i-1)), Double.parseDouble(Longitud.get(i-1))), new LatLng(Double.parseDouble(Latitud.get(i)), Double.parseDouble(Longitud.get(i))), Color.YELLOW);
                        }


                    }
                }
                CameraPosition PosicionCamara  = new CameraPosition.Builder()
                        .target(new LatLng(Double.parseDouble(Latitud.get(0)), Double.parseDouble(Longitud.get(0))))
                        .zoom(14)
                        //.bearing(45)
                        .tilt(90)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(PosicionCamara);
                mMap.animateCamera(cameraUpdate);
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, Estudiantes);
//                milista.setAdapter(adapter);
            } else {

            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public boolean ConnectionDataBaseRutasEstudiantes(String IdEstudiante){
        String SOAP_ACTION2 = "http://tempuri.org/GeoreferenciaEstudiantes"; //nome da açao
        String METHOD_NAME2 = "GeoreferenciaEstudiantes";// nome do método a ser envocado
        String NAMESPACE2 =   "http://tempuri.org/";//NOME DO WEBSERVICE
        String URL2 = "https://siscov.net/zonacliente/Asreales/RutasWS/Servicios/Mobile/Mobile.asmx"; /// URL DO METODO

        boolean auxLogin = false;

        Log.d("estudiante ",IdEstudiante);
        try{
            SoapObject Request = new SoapObject(NAMESPACE2, METHOD_NAME2);
            Request.addProperty("IdTerceroEstudiante", IdEstudiante);

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


            for(int i=0;i<emp.getPropertyCount();i++){
                SoapObject emp1 = (SoapObject) emp.getProperty(i);
                Log.i("emp1", emp1.toString());

                CodigoEstado.add(emp1.getProperty(2).toString());
                Log.i("CodigoEstado", emp1.getProperty(2).toString());

                Fecha.add(emp1.getProperty(3).toString());
                Log.i("Fecha", emp1.getProperty(3).toString());

                Latitud.add(emp1.getProperty(4).toString());
                Log.i("Latitud", emp1.getProperty(4).toString());

                Longitud.add(emp1.getProperty(5).toString());
                Log.i("Longitud", emp1.getProperty(5).toString());
            }


            auxLogin = true;
        } catch (Exception e){
            Log.i("Consulta List Est", "ERROR en la consulta: " + e.getMessage());
            auxLogin=false;
        }

//        return  true;
        return auxLogin;

    }

        @Override
        public void onDestroy() {
            super.onDestroy();
           handleCheckStatus.removeCallbacks(my_runnable);
        }
}
