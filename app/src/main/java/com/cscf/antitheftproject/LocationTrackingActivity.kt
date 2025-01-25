package com.cscf.antitheftproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class LocationTrackingActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST = 123
    private lateinit var locationTextView: TextView
    private lateinit var timestampTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_tracking)

        locationTextView = findViewById(R.id.tvLocation)
        timestampTextView = findViewById(R.id.tvTimestamp)

        checkAndRequestPermissions()
        setupLocationTracking()
        setupUI()
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )

        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                notGrantedPermissions.toTypedArray(),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            startLocationService()
        }
    }

    private fun setupLocationTracking() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val locationRef = FirebaseDatabase.getInstance().getReference("locations")
            .child(userId)
            .child("current")

        locationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val latitude = snapshot.child("latitude").getValue(Double::class.java)
                val longitude = snapshot.child("longitude").getValue(Double::class.java)
                val timestamp = snapshot.child("timestamp").getValue(Long::class.java)
                val accuracy = snapshot.child("accuracy").getValue(Float::class.java)

                updateLocationUI(latitude, longitude, timestamp, accuracy)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LocationTrackingActivity, 
                    "Failed to read location: ${error.message}", 
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateLocationUI(latitude: Double?, longitude: Double?, timestamp: Long?, accuracy: Float?) {
        if (latitude != null && longitude != null) {
            locationTextView.text = "Location:\nLatitude: $latitude\nLongitude: $longitude" +
                    (accuracy?.let { "\nAccuracy: $it meters" } ?: "")
        }

        if (timestamp != null) {
            val date = Date(timestamp)
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            timestampTextView.text = "Last Updated: ${format.format(date)}"
        }
    }

    private fun setupUI() {
        findViewById<Button>(R.id.btnStopTracking).setOnClickListener {
            stopLocationService()
            finish()
        }

        findViewById<Button>(R.id.btnViewHistory).setOnClickListener {
            startActivity(Intent(this, LocationHistoryActivity::class.java))
        }
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopLocationService() {
        stopService(Intent(this, LocationService::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startLocationService()
            } else {
                Toast.makeText(this, 
                    "Location permissions are required for tracking", 
                    Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}