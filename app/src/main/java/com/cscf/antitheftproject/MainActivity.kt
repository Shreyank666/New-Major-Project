package com.cscf.antitheftproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val findMyPhoneButton = findViewById<Button>(R.id.btnFindMyPhone)
        val remoteCommandsButton = findViewById<Button>(R.id.btnRemoteCommands)
        val securityAlertsButton = findViewById<Button>(R.id.btnSecurityAlerts)

        findMyPhoneButton.setOnClickListener {
            // Navigate to LocationTrackingActivity
            startActivity(Intent(this, LocationTrackingActivity::class.java))
        }

        remoteCommandsButton.setOnClickListener {
            // Navigate to RemoteCommandsActivity
            startActivity(Intent(this, RemoteCommandsActivity::class.java))
        }

        securityAlertsButton.setOnClickListener {
            // Implement security alerts functionality
        }
    }
}