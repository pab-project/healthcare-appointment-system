package com.example.healthcareapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class DoctorListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HealthcareTheme {
                DoctorListScreen(
                    doctors = DoctorDummy.doctors,
                    onDoctorClick = { doctor ->
                        val intent = Intent(this, DoctorDetailActivity::class.java)
                        intent.putExtra("NAME", doctor.name)
                        intent.putExtra("SPECIALIZATION", doctor.specialization)
                        intent.putExtra("DESCRIPTION", doctor.description)
                        intent.putStringArrayListExtra("SCHEDULE", ArrayList(doctor.schedule))
                        startActivity(intent)
                    },
                    onBackClick = { finish() }
                )
            }
        }
    }
}