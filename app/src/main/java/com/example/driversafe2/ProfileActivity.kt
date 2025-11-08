package com.example.driversafe2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.driversafe2.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var username: String
    private lateinit var adminreference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminreference = database.reference.child("drivers")
        binding.phonenumber.isEnabled = false
        binding.profilename.isEnabled = false
        binding.vehiclenumber.isEnabled = false
        var isEnabled = false
        binding.editbutton.setOnClickListener {
            isEnabled = !isEnabled
            binding.phonenumber.isEnabled = isEnabled
            binding.profilename.isEnabled = isEnabled
            binding.vehiclenumber.isEnabled = isEnabled
        }
        val prefs = getSharedPreferences("DriverSafePrefs", MODE_PRIVATE)
        val tempUsername = prefs.getString("username", null)
        if (tempUsername == null) {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        username = tempUsername
        userRef = database.reference.child("drivers").child(username)
        loadUserProfile()
        binding.saveinfobutton.setOnClickListener {
            updateuserprofile()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun loadUserProfile() {
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val name = snapshot.child("name").getValue(String::class.java)
                val vehicle = snapshot.child("vehivle").getValue(String::class.java) // <-- Use the correct spelling

                val phone = snapshot.child("phonenumber").getValue(String::class.java)
                binding.profilename.setText(name)
                binding.vehiclenumber.setText(vehicle)
                binding.phonenumber.setText(phone)

            } else {
                Toast.makeText(this, "Profile data not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateuserprofile() {
        var updatednumber = binding.phonenumber.text.toString().trim()
        var updateprofile = binding.profilename.text.toString().trim()
        var updatedvehiclenymber = binding.vehiclenumber.text.toString().trim()
        if(updateprofile.isEmpty() && updatednumber.isEmpty()) {
            Toast.makeText(this, "Fields Cannot Be Left Empty", Toast.LENGTH_SHORT).show()
            return
        }
        val updates = mapOf(
            "name" to updateprofile,
            "vehivle" to updatedvehiclenymber,
            "phonenumber" to updatednumber
        )
        userRef.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                binding.editbutton.text = "Edit"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update Failed. Try again.", Toast.LENGTH_SHORT).show()
            }
    }
}