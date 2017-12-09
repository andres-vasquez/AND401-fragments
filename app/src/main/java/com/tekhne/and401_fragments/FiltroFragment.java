package com.tekhne.and401_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

/**
 * Created by Android-Instructor on 8/12/2017.
 */

public class FiltroFragment extends Fragment{
    private Context context;
    private MainActivity activity;

    private EditText searchEditText;
    private Button searchButton;
    private TextView latTextView;
    private TextView longTextView;
    private CheckBox draggableCheckbox;
    private Button cleanButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if(context instanceof MainActivity){
            this.activity = (MainActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtro,container,false);
        searchEditText = view.findViewById(R.id.searchEditText);
        searchButton= view.findViewById(R.id.searchButton);
        latTextView= view.findViewById(R.id.latTextView);
        longTextView= view.findViewById(R.id.longTextView);
        draggableCheckbox= view.findViewById(R.id.draggableCheckbox);
        cleanButton= view.findViewById(R.id.cleanButton);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoBusqueda = searchEditText.getText().toString();
                ((MapFragment)activity.mapFragment).buscarLugar(textoBusqueda);
            }
        });

        draggableCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                ((MapFragment)activity.mapFragment).setDraggable(checked);
            }
        });
    }

    public void llenarLatLon(LatLng markerLocation){
        latTextView.setText("Latitud: "+markerLocation.latitude);
        longTextView.setText("Longitud: "+markerLocation.longitude);
    }
}
