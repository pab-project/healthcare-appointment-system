package com.example.healthcareapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambil data dari Intent
        val nim = intent.getStringExtra("NIM") ?: "N/A"
        val nama = intent.getStringExtra("NAMA") ?: "N/A"
        val jurusan = intent.getStringExtra("JURUSAN") ?: "N/A"
        val angkatan = intent.getStringExtra("ANGKATAN") ?: "N/A"
        val deskripsi = intent.getStringExtra("DESKRIPSI") ?: "N/A"
        val github = intent.getStringExtra("GITHUB") ?: ""

        setContent {
            HealthcareTheme {
                ProfileScreen(
                    nim = nim,
                    nama = nama,
                    jurusan = jurusan,
                    angkatan = angkatan,
                    deskripsi = deskripsi,
                    onHistoryClick = {
                        val intent = Intent(this, HistoryActivity::class.java)
                        startActivity(intent)
                    },
                    onShareClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Profil Mahasiswa: $nama ($nim)")
                        }
                        startActivity(Intent.createChooser(shareIntent, "Bagikan profil via..."))
                    },
                    onSendEmailClick = {
                        val emailBody = "Halo $nama,\n\nSaya ingin menghubungi Anda..."
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("$nim@mail.ugm.ac.id"))
                            putExtra(Intent.EXTRA_SUBJECT, "Healthcare Appointment - $nama")
                            putExtra(Intent.EXTRA_TEXT, emailBody)
                        }
                        startActivity(Intent.createChooser(emailIntent, "Kirim email via..."))
                    },
                    onGithubClick = {
                        if (github.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(github))
                            startActivity(intent)
                        }
                    },
                    onBackClick = { finish() }
                )
            }
        }
    }
}
