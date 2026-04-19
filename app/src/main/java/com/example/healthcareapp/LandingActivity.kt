package com.example.healthcareapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class LandingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LandingPage(
                onLoginClick = {
                    // Navigasi ke Login jika ada
                },
                onRegisterClick = {
                    // Navigasi ke Register jika ada
                },
                onDoctorListClick = {
                    // Pindah ke MainActivity (Daftar Member Kelompok/XML)
                    startActivity(Intent(this, MainActivity::class.java))
                },

                // --- INI BAGIAN JANJI TEMU KAMU ---
                onAppointmentClick = {
                    // Membuka AppointmentActivity (Form Compose yang kita buat)
                    val intent = Intent(this, AppointmentActivity::class.java)
                    startActivity(intent)
                },

                onScheduleClick = {
                    // Navigasi ke jadwal jika ada
                },
                onAdminClick = {
                    // Navigasi ke dashboard admin
                },

                // Fitur Maps dari kodingan kelompok
                onFindHospital = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=rumah+sakit+terdekat"))
                    startActivity(intent)
                },

                // Fitur Emergency Call
                onEmergencyCall = {
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:119")))
                },
                isAdmin = true // Aktifkan supaya tombol admin muncul
            )
        }
    }
}