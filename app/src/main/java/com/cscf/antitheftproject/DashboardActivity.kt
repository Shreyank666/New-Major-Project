package com.cscf.antitheftproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.text.InputType

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        try {
            setupProfile()
            setupDashboardButtons()
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error in onCreate: ${e.message}")
            Toast.makeText(this, "Error initializing dashboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupProfile() {
        val currentUser = auth.currentUser
        val userEmailView = findViewById<TextView>(R.id.tvUserEmail)
        val changePasswordButton = findViewById<Button>(R.id.btnChangePassword)
        val signOutButton = findViewById<Button>(R.id.btnSignOut)

        // Display user email
        userEmailView.text = currentUser?.email ?: "No email"

        // Handle change password
        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        // Handle sign out
        signOutButton.setOnClickListener {
            auth.signOut()
            // Navigate back to login screen
            val intent = Intent(this, LoginRegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change Password")

        // Set up the input
        val input = android.widget.EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        input.hint = "Enter new password"
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK") { _, _ ->
            val newPassword = input.text.toString()
            if (newPassword.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            
            auth.currentUser?.updatePassword(newPassword)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update password: ${task.exception?.message}", 
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
        
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun setupDashboardButtons() {
        val findMyPhoneButton = findViewById<Button>(R.id.btnFindMyPhone)
        val remoteCommandsButton = findViewById<Button>(R.id.btnRemoteCommands)
        val securityAlertsButton = findViewById<Button>(R.id.btnSecurityAlerts)

        findMyPhoneButton.setOnClickListener {
            startActivity(Intent(this, LocationTrackingActivity::class.java))
        }

        remoteCommandsButton.setOnClickListener {
            startActivity(Intent(this, RemoteCommandsActivity::class.java))
        }

        securityAlertsButton.setOnClickListener {
            Toast.makeText(this, "Security Alerts coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}