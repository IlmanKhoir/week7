package com.example.week7

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

import com.example.week7.databinding.ActivityMapsBinding

import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker


class MapsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding
    //This is the variable through which we will launch the permission request and track user responses
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    // important: set osmdroid user agent before loading tiles
    // Use app-specific SharedPreferences instead of androidx.preference.PreferenceManager
    Configuration.getInstance().load(applicationContext, applicationContext.getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // configure map view
        val map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // ini titik lokasi Griya Parungpanjang
        val startPoint = GeoPoint(-6.3, 106.5)
        val mapController = map.controller
        mapController.setZoom(10.0)
        mapController.setCenter(startPoint)

        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Marker in Sydney"
        map.overlays.add(marker)

        // register permission launcher
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    //If granted by the user, execute the necessary function
                    getLastLocation()
                } else {
                    //If not granted, show a rationale dialog
                    showPermissionRationale {
                        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    //This is used to check if the user already has the permission granted
    private fun hasLocationPermission() =
        ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    //This is used to bring up a rationale dialog which will be used to ask the user for permission again
    //A rationale dialog is used for a warning to the user that the app will now work without the required permission
    //Usually it's brought up when the user denies the needed permission in the previous permission request
    private fun showPermissionRationale(positiveAction: () -> Unit) {
        //Create a pop up alert dialog that's used to ask for the required permission again to the user
        AlertDialog.Builder(this)
            .setTitle("Location permission")
            .setMessage("This app will not work without knowing your current location")
            .setPositiveButton(android.R.string.ok) { _, _ -> positiveAction() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create().show()
    }

    private fun getLastLocation() {
        Log.d("MapsActivity", "getLastLocation() called.")
        // TODO: integrate location provider (FusedLocationProviderClient or Android LocationManager)
    }
}