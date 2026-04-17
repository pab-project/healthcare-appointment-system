package com.example.healthcareapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Ambil data dari Intent
        val nim = intent.getStringExtra("NIM") ?: "N/A"
        val nama = intent.getStringExtra("NAMA") ?: "N/A"
        val jurusan = intent.getStringExtra("JURUSAN") ?: "N/A"
        val angkatan = intent.getStringExtra("ANGKATAN") ?: "N/A"
        val deskripsi = intent.getStringExtra("DESKRIPSI") ?: "N/A"
        val github = intent.getStringExtra("GITHUB") ?: ""

        // Tampilkan data ke UI
        findViewById<TextView>(R.id.tvProfileNim).text = nim
        findViewById<TextView>(R.id.tvProfileNama).text = nama
        findViewById<TextView>(R.id.tvProfileJurusan).text = jurusan
        findViewById<TextView>(R.id.tvProfileAngkatan).text = angkatan
        findViewById<TextView>(R.id.tvProfileDeskripsi).text = deskripsi

        // Tombol menuju History (Explicit Intent)
        findViewById<Button>(R.id.btnGoToHistory).setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // Tombol kembali
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // IMPLICIT INTENT: Kirim Email
        findViewById<Button>(R.id.btnSendEmail).setOnClickListener {
            val emailBody = "Halo $nama,\n\nSaya ingin menghubungi Anda..."
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("$nim@mail.ugm.ac.id"))
                putExtra(Intent.EXTRA_SUBJECT, "Healthcare Appointment - $nama")
                putExtra(Intent.EXTRA_TEXT, emailBody)
            }
            startActivity(Intent.createChooser(emailIntent, "Kirim email via..."))
        }

        // IMPLICIT INTENT: Share
        findViewById<Button>(R.id.btnShare).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Profil Mahasiswa: $nama ($nim)")
            }
            startActivity(Intent.createChooser(shareIntent, "Bagikan profil via..."))
        }

        // IMPLICIT INTENT: Buka GitHub
        findViewById<Button>(R.id.btnGithub).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(github))
            startActivity(intent)
        }
    }
}
