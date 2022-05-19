package com.example.ingenia.pilarutaacudientes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListaMonitorasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListaMonitorasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaMonitorasFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<String> Monitoras = new ArrayList();
    ArrayList<String> IdMonitoras = new ArrayList();

    ListView milista;
    String Url_colegio;

    public ListaMonitorasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaMonitorasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaMonitorasFragment newInstance(String param1, String param2) {
        ListaMonitorasFragment fragment = new ListaMonitorasFragment();
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
        // Inflate the layout for this fragment
//        String[] values = new String[]{"Ironman","Capitan America","Hulk","Thor","Black Widow","Ant man","Spider man"};

        View view = inflater.inflate(R.layout.fragment_lista_monitoras, container, false);
        milista = (ListView) view.findViewById(R.id.listaMonitoras);


        getActivity().setTitle("Conductor");

        Log.i("id_usuario", getArguments().getString("id_usuario"));
        Url_colegio = getArguments().getString("Url_colegio");
        Log.i("Url_colegio", Url_colegio);

        ListaMonitorasTask listaMonitorasTask = new ListaMonitorasTask(getArguments().getString("id_usuario"));
        listaMonitorasTask.execute((Void) null);

        milista.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//                int item = position;
//                String itemval = (String) milista.getItemAtPosition(position);
//                Toast.makeText(getContext(), "Position: "+ item+" - Valor: "+itemval, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getContext(), RutaMonitorasActivity.class );
                i.putExtra("IdMonitor", IdMonitoras.get(position).toString());
                i.putExtra("NombreMonitor", Monitoras.get(position).toString());
//                i.putExtra("IdMonitor", "71");
                startActivity(i);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class ListaMonitorasTask extends AsyncTask<Void, Void, Boolean> {

        private final String IdUsuario;


        ListaMonitorasTask( String idUsuario) {

            IdUsuario = idUsuario;
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

            return ConnectionDataBaseListaMonitoras(IdUsuario);


            // TODO: register the new account here.


        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Toast toast1 = Toast.makeText(getActivity().getBaseContext(), "Consulta Exitosa", Toast.LENGTH_SHORT);
                toast1.show();

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, Monitoras);
                milista.setAdapter(adapter);
            } else {
                Toast toast1 = Toast.makeText(getActivity().getBaseContext(), "Consulta fallida", Toast.LENGTH_SHORT);
                toast1.show();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public boolean ConnectionDataBaseListaMonitoras(String IdUsuario){
        String SOAP_ACTION2 = "http://tempuri.org/ListaDeMonitoresPapa"; //nome da açao
        String METHOD_NAME2 = "ListaDeMonitoresPapa";// nome do método a ser envocado
        String NAMESPACE2 =   "http://tempuri.org/";//NOME DO WEBSERVICE
        String URL2 = Url_colegio;

        boolean auxLogin = false;

        try{
            SoapObject Request = new SoapObject(NAMESPACE2, METHOD_NAME2);
            Request.addProperty("intIdUsuario", IdUsuario);

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

//            Cursor cursor = sqlite.ConsultarPuntosAtencion();
//            int longitudTabla2=cursor.getCount();

//            if(emp.getPropertyCount()!= longitudTabla2){
//                sqlite.borrar();
            for(int i=0;i<emp.getPropertyCount();i++){
                SoapObject emp1 = (SoapObject) emp.getProperty(i);
                Log.i("emp1", emp1.toString());



//                    Cursor cursor2 = sqLiteMunicipios.consultarIdMunicipios2(emp1.getProperty("ID_MUNICIPIO").toString());
//                    cursor2.moveToFirst();

//                    IdEstudiantes[i] = emp1.getProperty(0).toString();


                IdMonitoras.add(emp1.getProperty(2).toString());
                Log.i("IdMonitoras", emp1.getProperty(2).toString());

                Monitoras.add(emp1.getProperty(0).toString() + " " + emp1.getProperty(1).toString());
                Log.i("Monitoras", emp1.getProperty(0).toString() + " " + emp1.getProperty(1).toString());



//                IdMonitoras.add(emp1.getProperty(0).toString());
//                Log.i("IdMonitoras", emp1.getProperty(0).toString());
//
//                Monitoras.add(emp1.getProperty(1).toString() + " " + emp1.getProperty(2).toString());
//                Log.i("Monitoras", emp1.getProperty(1).toString() + " " + emp1.getProperty(2).toString());

//                    if(cursor2.getString(0)!=null){
//                        falta consulta base de datos municipios y llenar campos faltants para agregar punto de atencion
//                        sqlite.agregarPuontoAtenccion(Integer.parseInt(emp1.getProperty("ID_PUNTO").toString()),emp1.getProperty("DIRECCION").toString()
//                                ,emp1.getProperty("HORARIO").toString(),emp1.getProperty("LATITUD").toString(),emp1.getProperty("LONGITUD").toString()
//                                ,Integer.parseInt(emp1.getProperty("ID_MUNICIPIO").toString()),cursor2.getString(0),cursor2.getInt(2));
//                    }

            }
//            }

            auxLogin = true;
        } catch (Exception e){
            Log.i("Consulta List Mon", "ERROR en la consulta: " + e.getMessage());
            auxLogin=false;
        }

//        return  true;
        return auxLogin;

    }
}
