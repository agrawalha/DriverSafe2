package com.example.driversafe2

import Driver
import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.driversafe2.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var prefs: SharedPreferences
    private val camerapermissionlauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                binding.CameraPermissionbutton.text = "Permission Granted"
                binding.CameraPermissionbutton.isEnabled = false
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
                checkallPermission()
            } else {
                Toast.makeText(this, "Camera Permission Not Granted", Toast.LENGTH_SHORT).show()
            }
        }
    private val GpsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                binding.GpsPermissionButton.text = "Permission Granted"
                binding.GpsPermissionButton.isEnabled = false
                Toast.makeText(this, "GPS Permission Granted", Toast.LENGTH_SHORT).show()
                checkallPermission()
            } else {
                Toast.makeText(this, "GPS Permission Not Granted", Toast.LENGTH_SHORT).show()
            }
        }
    private fun checkallPermission() {
        val cameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        val GpsGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        binding.nextbutton.isEnabled = cameraGranted && GpsGranted
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("DriverSafePrefs", MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().reference

        val isSetupDone = prefs.getBoolean("setup_done", false)
        if (isSetupDone) {
            startActivity(Intent(this, SelectionPage::class.java))
            finish()
            return
        }
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.CameraPermissionbutton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                binding.CameraPermissionbutton.text = "Permission Granted"
                binding.CameraPermissionbutton.isEnabled = false
            } else {
                camerapermissionlauncher.launch(Manifest.permission.CAMERA)
            }
            checkallPermission()
        }
        binding.GpsPermissionButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                binding.GpsPermissionButton.text = "GPS Permission Granted"
                binding.GpsPermissionButton.isEnabled = false
            } else {
                GpsPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            checkallPermission()
        }
        binding.nextbutton.setOnClickListener {
            val name = binding.nameentry.text.toString().trim()
            val vehicle = binding.vehiclenumber.text.toString().trim()
            val permissionsGranted = binding.nextbutton.isEnabled
            if (name.isBlank()) {
                binding.nameentry.error = "Name is required"
                return@setOnClickListener
            }
            if (vehicle.isBlank()) {
                binding.vehiclenumber.error = "Vehicle number is required"
                return@setOnClickListener
            }
            if (!permissionsGranted) {
                Toast.makeText(this, "Please grant all permissions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveDriverData(name, vehicle)
            prefs.edit()
                .putBoolean("setup_done", true)
                .putString("username", name)
                .apply()
            startActivity(Intent(this, SelectionPage::class.java))
            finish()
        }
        checkallPermission()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun saveDriverData(name: String, vehicleNumber: String) {
        val driver = Driver(name, vehicleNumber)
        database.child("drivers").child(name).setValue(driver)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Save failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}