package com.example.healthcareapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthcareTheme {
                MainScreen(
                    onMemberClick = { nim, nama, jurusan, angkatan, deskripsi, github ->
                        openProfile(nim, nama, jurusan, angkatan, deskripsi, github)
                    },
                    onDoctorListClick = {
                        val intent = Intent(this, DoctorListActivity::class.java)
                        startActivity(intent)
                    },
                    onAppointmentClick = {
                        val intent = Intent(this, AppointmentActivity::class.java)
                        startActivity(intent)
                    },
                    onFindHospitalClick = {
                        openMaps()
                    },
                    onEmergencyCallClick = {
                        makeEmergencyCall()
                    }
                )
            }
        }
    }

    private fun openMaps() {
        val query = "rumah sakit terdekat"
        val geoUri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            val browserUri = Uri.parse("https://www.google.com/maps/search/${Uri.encode(query)}")
            startActivity(Intent(Intent.ACTION_VIEW, browserUri))
        }
    }

    private fun makeEmergencyCall() {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:119")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Aplikasi telepon tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openProfile(
        nim: String,
        nama: String,
        jurusan: String,
        angkatan: String,
        deskripsi: String,
        github: String
    ) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra("NIM", nim)
            putExtra("NAMA", nama)
            putExtra("JURUSAN", jurusan)
            putExtra("ANGKATAN", angkatan)
            putExtra("DESKRIPSI", deskripsi)
            putExtra("GITHUB", github)
        }
        startActivity(intent)
    }
}
