package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.OutlinedTextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorEditProfileScreen(
    doctor: Doctor,
    onSaveClick: (name: String, specialization: String, description: String, schedule: List<String>) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf(doctor.name) }
    var specialization by remember { mutableStateOf(doctor.specialization) }
    var description by remember { mutableStateOf(doctor.description) }
    


    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = TextHint,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        focusedContainerColor = White,
        unfocusedContainerColor = White,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
    )

    var nameError by remember { mutableStateOf(false) }
    var specError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil Dokter", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Lengkapi Profil Praktik Anda",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Nama Dokter
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = it.isBlank()
                },
                label = { Text("Nama Dokter") },
                isError = nameError,
                supportingText = {
                    if (nameError) Text("Nama dokter wajib diisi", color = MaterialTheme.colorScheme.error)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            // Spesialisasi
            OutlinedTextField(
                value = specialization,
                onValueChange = {
                    specialization = it
                    specError = it.isBlank()
                },
                label = { Text("Spesialisasi (Contoh: Dokter Umum, Dokter Gigi)") },
                isError = specError,
                supportingText = {
                    if (specError) Text("Spesialisasi wajib diisi", color = MaterialTheme.colorScheme.error)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            // Deskripsi / Biografi
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Biografi Singkat") },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )



            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    if (specialization.isBlank()) {
                        specError = true
                        return@Button
                    }
                    
                    onSaveClick(name, specialization, description, emptyList())
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), // Green Accent for doctor edit
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            OutlinedButton(
                onClick = onBackClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Batal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }
        }
    }
}
