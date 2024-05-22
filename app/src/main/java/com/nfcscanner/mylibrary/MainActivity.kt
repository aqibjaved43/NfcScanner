package com.nfcscanner.mylibrary

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nfcscanner.android.nfcscanner.NFCReader
import com.nfcscanner.mylibrary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnStartScanning.setOnClickListener {
            NFCReader.create(this).startScanning().setOnCompleteListener {
                Toast.makeText(this, "Complete", Toast.LENGTH_SHORT).show()

            }.setOnFailureListener {
                Toast.makeText(this, it.message ?: "Failure", Toast.LENGTH_SHORT).show()
            }
        }
    }
}