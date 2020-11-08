package com.example.pokemongo

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        loadPokemon()
    }

    private var ACCESSLOCATION = 123
    private fun checkPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESSLOCATION
                )
                return
            }
        }
        getUserLocation()
    }

    private fun getUserLocation() {
        Toast.makeText(this, "User access location on", Toast.LENGTH_LONG).show()

        val myLocation = MyLocationListener()

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, myLocation)
        MyThread().start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            ACCESSLOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getUserLocation()
                else
                    Toast.makeText(this, "We cannot access your location", Toast.LENGTH_LONG).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    var location: Location? = null

    inner class MyLocationListener() : LocationListener {

        init {
            location = Location("Start")
            location!!.longitude = 0.0
            location!!.latitude = 0.0
        }

        override fun onLocationChanged(p0: Location) {
            location = p0
        }

    }

    var oldLocation: Location? = null

    inner class MyThread() : Thread() {

        init {
            oldLocation = Location("Start")
            oldLocation!!.longitude = 0.0
            oldLocation!!.latitude = 0.0
        }

        override fun run() {
            while (true) {
                try {

                    if (oldLocation!!.distanceTo(location) == 0f)
                        continue

                    oldLocation = location

                    runOnUiThread {
                        mMap.clear()
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet("Here is my Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                        for (i in 0 until listPokemon.size) {

                            val newPokemon = listPokemon[i]

                            if (newPokemon.isCatch == false) {

                                val pokemonLoc =
                                    LatLng(
                                        newPokemon.location!!.latitude,
                                        newPokemon.location!!.longitude
                                    )
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(pokemonLoc)
                                        .title(newPokemon.name!!)
                                        .snippet(newPokemon.des!! + " Power: ${newPokemon.power}")
                                        .icon(BitmapDescriptorFactory.fromResource(newPokemon.image!!))
                                )

                                if (location!!.distanceTo(newPokemon.location) < 2) {
                                    newPokemon.isCatch = true
                                    listPokemon[i] = newPokemon
                                    playerPower += newPokemon.power!!
                                    Toast.makeText(
                                        applicationContext,
                                        "You have cached a new Pokemon and your power is $playerPower",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }

                        }


                    }
                    sleep(100000)
                } catch (e: Exception) {
                }
            }
        }
    }

    var listPokemon = ArrayList<Pokemon>()
    var playerPower: Double = 0.0
    private fun loadPokemon() {
        listPokemon.add(
            Pokemon(
                "Charmander",
                "Here is fro japan",
                R.drawable.charmander,
                55.0,
                37.7789994893035,
                -122.401846647263
            )
        )
        listPokemon.add(
            Pokemon(
                "Bulbasaur",
                "Bulbasaur living in usa",
                R.drawable.bulbasaur,
                90.5,
                37.7949568502667,
                -122.410494089127
            )
        )
        listPokemon.add(
            Pokemon(
                "Squirtle",
                "Squirtle living in iraq",
                R.drawable.squirtle,
                55.0,
                37.7816621152613,
                -122.41225361824
            )
        )
    }

}