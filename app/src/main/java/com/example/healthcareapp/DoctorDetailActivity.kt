package com.example.healthcareapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DoctorDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_detail)

        // Ambil data dari intent
        val name = intent.getStringExtra("NAME") ?: "Unknown"
        val specialization = intent.getStringExtra("SPECIALIZATION") ?: "-"
        val description = intent.getStringExtra("DESCRIPTION") ?: "-"
        val schedule = intent.getStringArrayListExtra("SCHEDULE") ?: arrayListOf("-")

        // Set ke UI
        findViewById<TextView>(R.id.tvName).text = name
        findViewById<TextView>(R.id.tvSpecialization).text = specialization
        findViewById<TextView>(R.id.tvDescription).text = description
        findViewById<TextView>(R.id.tvSchedule).text = schedule.joinToString("\n")

        // Button kembali
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}