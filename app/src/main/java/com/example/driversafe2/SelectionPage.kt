package com.example.driversafe2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.driversafe2.databinding.ActivitySelectionPageBinding

class SelectionPage : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectionPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.adddocs.setOnClickListener {
            startActivity(Intent(this,AddDocuments::class.java))
            finish()
        }
        binding.viewdocs.setOnClickListener {
            startActivity(Intent(this,ViewDocuments::class.java))
            finish()
        }
        binding.pastjourney.setOnClickListener {
            startActivity(Intent(this,History::class.java))
            finish()
        }
        binding.viewprofile.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
            finish()
        }
        binding.startbutton.setOnClickListener {
            startActivity(Intent(this,StartJouney::class.java))
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}