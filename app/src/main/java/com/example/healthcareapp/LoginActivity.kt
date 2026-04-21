package com.example.healthcareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthcareTheme {
                LoginScreen(
                    onLoginClick = { email, password ->
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Harap isi email dan password", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onRegisterClick = {
                        Toast.makeText(this, "Fitur Daftar belum tersedia", Toast.LENGTH_SHORT).show()
                    },
                    onForgotPasswordClick = {
                        Toast.makeText(this, "Fitur Lupa Password belum tersedia", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
