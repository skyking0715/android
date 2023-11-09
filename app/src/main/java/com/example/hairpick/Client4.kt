package com.example.hairpick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hairpick.databinding.Client3Binding
import com.example.hairpick.databinding.Client4Binding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class Client4 : AppCompatActivity(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cli4 = Client4Binding.inflate(layoutInflater)

        setContentView(cli4.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapfragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }


}


