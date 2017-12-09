package com.tekhne.and401_fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public Fragment filtroFragment;
    public Fragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        if(filtroFragment==null) {
            filtroFragment = new FiltroFragment();
            fm.beginTransaction().replace(R.id.filtroFragment, filtroFragment).commit();
        }

        if(mapFragment==null){
            mapFragment = new MapFragment();
            fm.beginTransaction().replace(R.id.mapFragment, mapFragment).commit();
        }
    }
}

