package com.example.healthcareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DoctorDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("NAME") ?: "Unknown"
        val specialization = intent.getStringExtra("SPECIALIZATION") ?: "-"
        val description = intent.getStringExtra("DESCRIPTION") ?: "-"
        val schedule = intent.getStringArrayListExtra("SCHEDULE") ?: arrayListOf("-")

        setContent {
            DoctorDetailScreen(
                name,
                specialization,
                description,
                schedule,
                onBack = { finish() }
            )
        }
    }
}

@Composable
fun DoctorDetailScreen(
    name: String,
    specialization: String,
    description: String,
    schedule: List<String>,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = name,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = specialization,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Divider()

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = description,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = schedule.joinToString("\n"),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onBack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kembali")
                }
            }
        }
    }
}