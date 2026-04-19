package com.example.healthcareapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtons()
    }

    private fun setupButtons() {
        // Tombol buat buka form Janji Temu (Appointment)
        findViewById<Button>(R.id.btnMakeAppointment).setOnClickListener {
            val intent = Intent(this, AppointmentActivity::class.java)
            startActivity(intent)
        }
        // =============================================
        // EXPLICIT INTENT: Pindah ke ProfileActivity
        // =============================================
        // =========================
        // MEMBER 1
        // =========================
        findViewById<Button>(R.id.btnMember1).setOnClickListener {
            openProfile(
                "L0124045",
                "Berly Marcellino Suprapto",
                "Informatika",
                "2024",
                "Pangeran Mars yang jatuh ke bumi lalu memulai perjalanannya sebagai Raja Iblis",
                "https://github.com/IMars-kun"
            )
        }
    1
        // =========================
        // MEMBER 2
        // =========================
        findViewById<Button>(R.id.btnMember2).setOnClickListener {
            openProfile(
                "L0124041",
                "Ardina Vidya S",
                "Informatika",
                "2024",
                "Fokus di backend dan database.",
                "https://github.com/ardinavidya"
            )
        }

        // =========================
        // MEMBER 3
        // =========================
        findViewById<Button>(R.id.btnMember3).setOnClickListener {
            openProfile(
                "L0124085",
                "Aisyah Nurul S",
                "Informatika",
                "2024",
                "Suka UI/UX design.",
                "https://github.com/ArulxSho"
            )
        }

        // =========================
        // MEMBER 4
        // =========================
        findViewById<Button>(R.id.btnMember4).setOnClickListener {
            openProfile(
                "L0124092",
                "Calista Salsabila",
                "Informatika",
                "2024",
                "Tertarik AI dan machine learning.",
                "https://github.com/calistasalsabila"
            )
        }

        // =========================
        // MEMBER 5
        // =========================
        findViewById<Button>(R.id.btnMember5).setOnClickListener {
            openProfile(
                "L0124054",
                "Farrel Azhar Vahrezi",
                "Informatika",
                "2024",
                "Fokus ke cybersecurity.",
                "https://github.com/faleryn"
            )
        }

        // =============================================
        // IMPLICIT INTENT 3: Buka Maps untuk lokasi RS
        // =============================================
        findViewById<Button>(R.id.btnFindHospital).setOnClickListener {
            val query = "rumah sakit terdekat"
            val geoUri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                // Fallback: buka Google Maps via browser
                val browserUri = Uri.parse("https://www.google.com/maps/search/${Uri.encode(query)}")
                startActivity(Intent(Intent.ACTION_VIEW, browserUri))
            }
        }

        // =============================================
        // IMPLICIT INTENT 4: Hubungi via telepon (Darurat)
        // =============================================
        findViewById<Button>(R.id.btnEmergency).setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:119")
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Aplikasi telepon tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }

    }
    //HELPER
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
