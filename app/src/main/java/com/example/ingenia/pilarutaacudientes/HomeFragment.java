package com.example.ingenia.pilarutaacudientes;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    String Url_colegio;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button estudiantes = (Button) view.findViewById(R.id.button);
        Button monitores = (Button) view.findViewById(R.id.button2);

        getActivity().setTitle("Inicio");

        Log.i("id_usuario", getArguments().getString("id_usuario"));
        Url_colegio = getArguments().getString("Url_colegio");
        Log.i("Url_colegio", Url_colegio);

        estudiantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListaEstudiantesFragment listaEstudiantesFragment = new ListaEstudiantesFragment();
                // Creamos un nuevo Bundle
                Bundle args = new Bundle();
                // Colocamos el String
                args.putString("id_usuario", getArguments().getString("id_usuario"));
                args.putString("Url_colegio", Url_colegio);

                listaEstudiantesFragment.setArguments(args);
                android.support.v4.app.FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.Content_1,listaEstudiantesFragment,listaEstudiantesFragment.getTag()).commit();
            }
        });

        monitores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListaMonitorasFragment listaMonitorasFragment = new ListaMonitorasFragment();
                // Creamos un nuevo Bundle
                Bundle args = new Bundle();
                // Colocamos el String
                args.putString("id_usuario", getArguments().getString("id_usuario"));
                args.putString("Url_colegio", Url_colegio);

                listaMonitorasFragment.setArguments(args);
                android.support.v4.app.FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.Content_1,listaMonitorasFragment,listaMonitorasFragment.getTag()).commit();
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
}
