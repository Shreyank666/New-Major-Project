package com.cscf.antitheftproject

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class RemoteCommandsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_commands)

        val lockButton = findViewById<Button>(R.id.btnLockDevice)
        val wipeButton = findViewById<Button>(R.id.btnWipeData)
        val alarmButton = findViewById<Button>(R.id.btnTriggerAlarm)

        lockButton.setOnClickListener {
            // Implement lock device functionality
        }

        alarmButton.setOnClickListener {
            // Implement trigger alarm functionality
        }

        wipeButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirm Wipe")
                .setMessage("Are you sure you want to wipe all data?")
                .setPositiveButton("Yes") { _, _ ->
                    // Implement data wipe functionality
                    Toast.makeText(this, "Data wiped.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
} 