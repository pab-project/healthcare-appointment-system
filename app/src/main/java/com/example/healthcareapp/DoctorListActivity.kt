package com.example.healthcareapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class DoctorListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DoctorListScreen(
                doctors = DoctorDummy.doctors,
                onClick = { doctor ->
                    val intent = Intent(this, DoctorDetailActivity::class.java)
                    intent.putExtra("NAME", doctor.name)
                    intent.putExtra("SPECIALIZATION", doctor.specialization)
                    intent.putExtra("DESCRIPTION", doctor.description)
                    intent.putStringArrayListExtra("SCHEDULE", ArrayList(doctor.schedule))

                    startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun DoctorListScreen(
    doctors: List<Doctor>,
    onClick: (Doctor) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(doctors) { doctor ->
            DoctorItem(doctor, onClick)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DoctorItem(
    doctor: Doctor,
    onClick: (Doctor) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(doctor) },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "👨‍⚕️ ${doctor.name}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = doctor.specialization,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}