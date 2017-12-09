package com.tekhne.and401_fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Android-Instructor on 8/12/2017.
 */

public class MapFragment extends Fragment {
    private Context context;
    private MainActivity activity;

    private MapView mapView;
    private GoogleMap map;
    private boolean isDraggable = true;

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
        View view = inflater.inflate(R.layout.fragment_map,container,false);
        mapView = view.findViewById(R.id.mapView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setCompassEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        agregarMarcador("Marcador dinamico",latLng);
                        ((FiltroFragment)activity.filtroFragment).llenarLatLon(latLng);
                    }
                });

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(marker.getPosition())      // Sets the center of the map to Mountain View
                                .zoom(18)                   // Sets the zoom
                                .build();                   // Creates a CameraPosition from the builder
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        return false;
                    }
                });

                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);

                    }
                });

                map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                        ((FiltroFragment)activity.filtroFragment).llenarLatLon(marker.getPosition());
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        ((FiltroFragment)activity.filtroFragment).llenarLatLon(marker.getPosition());
                        new BusquedaInversa().execute(marker.getPosition());
                    }
                });
            }
        });
    }

    public void agregarMarcador(String titulo, LatLng location){
        map.addMarker(new MarkerOptions()
                .position(location)
                .title(titulo)
                .draggable(isDraggable));
    }

    public void clear(){
        map.clear();
    }

    public void buscarLugar(String texto){
        new Busqueda().execute(texto);
    }

    class Busqueda extends AsyncTask<String,List<Address>,Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Buscando lugares");
            progressDialog.show();

            clear();
        }

        @Override
        protected Void doInBackground(String... strings) {
            Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocationName(strings[0], 5); //5 = cantidad de resultados
                if (!isCancelled()) {
                    if (!addresses.isEmpty()) {
                        publishProgress(addresses);
                    } else {
                        publishProgress(new ArrayList<Address>());
                    }
                }

                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(List<Address>... values) {
            super.onProgressUpdate(values);
            if(!values[0].isEmpty()){
                for(Address address : values[0]){
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    agregarMarcador(address.getFeatureName(),latLng);
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }

    class BusquedaInversa extends AsyncTask<LatLng,List<Address>,Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Buscando lugares");
            progressDialog.show();

            clear();
        }

        @Override
        protected Void doInBackground(LatLng... location) {
            Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(location[0].latitude, location[0].longitude,5); //5 = cantidad de resultados
                for (Address address : addresses){
                    Log.d("Aca.feature",address.getFeatureName());
                    Log.d("Aca.direccion",address.getAddressLine(0));
                }
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }

    public void setDraggable(boolean draggable) {
        isDraggable = draggable;
    }
}
