package com.example.healthcareapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    patient: Patient,
    onBackClick: () -> Unit,
    onSaveClick: (Patient) -> Unit
) {
    var name by remember { mutableStateOf(patient.name) }
    var email by remember { mutableStateOf(patient.email) }
    var phone by remember { mutableStateOf(patient.phone) }
    var gender by remember { mutableStateOf(patient.gender) }
    var birthDate by remember { mutableStateOf(patient.birthDate) }
    var address by remember { mutableStateOf(patient.address) }

    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Laki-laki", "Perempuan")

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = TextHint,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
        focusedContainerColor = White,
        unfocusedContainerColor = White,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Edit Profil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            IconButton(onClick = onBackClick) {
                Text("✕", fontSize = 20.sp, color = TextSecondary)
            }
        }
        
        Text(
            text = "Perbarui informasi pribadi Anda di bawah ini.",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // CARD UTAMA
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // NAMA LENGKAP
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                // EMAIL
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                // NO TELEPON
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("No HP / WhatsApp") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                // DROPDOWN GENDER
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jenis Kelamin") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = option
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                // TANGGAL LAHIR
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    label = { Text("Tanggal Lahir (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                // ALAMAT
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Alamat Tinggal") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // TOMBOL AKSI
        Button(
            onClick = {
                if (name.isBlank()) {
                    errorMessage = "Nama tidak boleh kosong."
                    showErrorDialog = true
                } else if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage = "Masukkan alamat email yang valid."
                    showErrorDialog = true
                } else if (phone.isBlank()) {
                    errorMessage = "Nomor HP tidak boleh kosong."
                    showErrorDialog = true
                } else {
                    onSaveClick(
                        patient.copy(
                            name = name,
                            email = email,
                            phone = phone,
                            gender = gender,
                            birthDate = birthDate,
                            address = address
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Simpan Perubahan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Batal", color = TextSecondary, fontSize = 16.sp)
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Input Tidak Valid", color = AccentRed, fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    val dummy = Patient(1, "Berly Marcellino", "berly@healthcare.com", "08123456789", "Laki-laki", "2004-01-01", "Klaten, Indonesia")
    EditProfileScreen(patient = dummy, onBackClick = {}, onSaveClick = {})
}
