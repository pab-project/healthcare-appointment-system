package com.example.healthcareapp

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (User) -> Unit,
    onBackToLogin: () -> Unit
) {
    // Step: 0 = Data Diri, 1 = Data Akun
    var currentStep by remember { mutableStateOf(0) }

    // Step 1 fields
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Laki-laki") }
    var birthDate by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Step 2 fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val headerGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1565C0), Color(0xFF0D47A1))
    )

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = TextHint,
        focusedLabelColor = PrimaryBlue,
        focusedContainerColor = White,
        unfocusedContainerColor = White,
        errorContainerColor = Color(0xFFFFF0F0)
    )
    val fieldShape = RoundedCornerShape(14.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── HEADER ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerGradient)
                    .padding(top = 48.dp, bottom = 36.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.size(68.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("🏥", fontSize = 30.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Buat Akun Baru",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Bergabung dengan HealthCare",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // ── STEPPER INDICATOR ──
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-16).dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {

                    // Progress Stepper
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StepIndicator(
                            step = 1,
                            label = "Data Diri",
                            isActive = currentStep == 0,
                            isDone = currentStep > 0,
                            modifier = Modifier.weight(1f)
                        )
                        // Connector line
                        Box(
                            modifier = Modifier
                                .weight(0.5f)
                                .height(2.dp)
                                .background(
                                    if (currentStep > 0) PrimaryBlue
                                    else TextHint.copy(alpha = 0.4f)
                                )
                        )
                        StepIndicator(
                            step = 2,
                            label = "Data Akun",
                            isActive = currentStep == 1,
                            isDone = false,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Error Banner
                    AnimatedVisibility(visible = errorMessage != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚠️", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = AccentRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // ── STEP 1: DATA DIRI ──
                    AnimatedVisibility(
                        visible = currentStep == 0,
                        enter = fadeIn() + slideInHorizontally { -40 },
                        exit = fadeOut() + slideOutHorizontally { -40 }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it; errorMessage = null },
                                label = { Text("Nama Lengkap") },
                                placeholder = { Text("Masukkan nama lengkap", color = TextHint) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = fieldColors,
                                shape = fieldShape
                            )

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it; errorMessage = null },
                                label = { Text("No. HP / WhatsApp") },
                                placeholder = { Text("08xxxxxxxxxx", color = TextHint) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = fieldColors,
                                shape = fieldShape
                            )

                            // Gender Selector
                            Column {
                                Text(
                                    "Jenis Kelamin",
                                    fontSize = 12.sp,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    GenderChip(
                                        label = "👨 Laki-laki",
                                        selected = gender == "Laki-laki",
                                        onClick = { gender = "Laki-laki" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    GenderChip(
                                        label = "👩 Perempuan",
                                        selected = gender == "Perempuan",
                                        onClick = { gender = "Perempuan" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = birthDate,
                                onValueChange = { birthDate = it; errorMessage = null },
                                label = { Text("Tanggal Lahir") },
                                placeholder = { Text("YYYY-MM-DD", color = TextHint) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = fieldColors,
                                shape = fieldShape
                            )

                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it; errorMessage = null },
                                label = { Text("Alamat Tinggal") },
                                placeholder = { Text("Kota, Provinsi", color = TextHint) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                colors = fieldColors,
                                shape = fieldShape
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    if (fullName.isBlank() || phone.isBlank() || birthDate.isBlank()) {
                                        errorMessage = "Nama, No. HP, dan tanggal lahir wajib diisi"
                                    } else {
                                        errorMessage = null
                                        currentStep = 1
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    "Lanjut →",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    // ── STEP 2: DATA AKUN ──
                    AnimatedVisibility(
                        visible = currentStep == 1,
                        enter = fadeIn() + slideInHorizontally { 40 },
                        exit = fadeOut() + slideOutHorizontally { 40 }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it; errorMessage = null },
                                label = { Text("Email") },
                                placeholder = { Text("contoh@email.com", color = TextHint) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = fieldColors,
                                shape = fieldShape
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it; errorMessage = null },
                                label = { Text("Password") },
                                placeholder = { Text("Minimal 6 karakter", color = TextHint) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation = if (passwordVisible)
                                    VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible)
                                                Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                            contentDescription = null,
                                            tint = if (passwordVisible) PrimaryBlue else TextHint
                                        )
                                    }
                                },
                                colors = fieldColors,
                                shape = fieldShape
                            )

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it; errorMessage = null },
                                label = { Text("Konfirmasi Password") },
                                placeholder = { Text("Ulangi password", color = TextHint) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation = if (confirmPasswordVisible)
                                    VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(
                                            imageVector = if (confirmPasswordVisible)
                                                Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                            contentDescription = null,
                                            tint = if (confirmPasswordVisible) PrimaryBlue else TextHint
                                        )
                                    }
                                },
                                isError = confirmPassword.isNotBlank() && password != confirmPassword,
                                colors = fieldColors,
                                shape = fieldShape
                            )

                            // Password match indicator
                            if (confirmPassword.isNotBlank()) {
                                val match = password == confirmPassword
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (match) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(if (match) "✅" else "❌", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        if (match) "Password cocok" else "Password tidak cocok",
                                        fontSize = 12.sp,
                                        color = if (match) AccentGreen else AccentRed,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Password strength hint
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFEEF4FF),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("ℹ️", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Password default untuk akun baru adalah yang Anda masukkan. Simpan dengan baik.",
                                        fontSize = 11.sp,
                                        color = PrimaryBlue,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    when {
                                        email.isBlank() -> errorMessage = "Email wajib diisi"
                                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                                            errorMessage = "Format email tidak valid"
                                        password.length < 6 -> errorMessage = "Password minimal 6 karakter"
                                        password != confirmPassword -> errorMessage = "Password tidak cocok"
                                        DataManager.users.any {
                                            it.email.equals(email, ignoreCase = true)
                                        } -> errorMessage = "Email sudah terdaftar"
                                        else -> {
                                            DataManager.addPatient(
                                                name = fullName,
                                                email = email,
                                                phone = phone,
                                                gender = gender,
                                                birthDate = birthDate,
                                                address = address,
                                                password = password,
                                                onComplete = {
                                                    val newUser = DataManager.authenticate(email, password)
                                                    if (newUser != null) {
                                                        onRegisterSuccess(newUser)
                                                    } else {
                                                        errorMessage = "Gagal masuk otomatis. Silakan masuk manual."
                                                    }
                                                }
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    "Daftar Sekarang",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            OutlinedButton(
                                onClick = { currentStep = 0; errorMessage = null },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp, TextHint
                                )
                            ) {
                                Text("← Kembali", color = TextSecondary, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // ── LINK KE LOGIN ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sudah punya akun? ", color = TextSecondary, fontSize = 14.sp)
                TextButton(onClick = onBackToLogin) {
                    Text(
                        "Masuk di sini",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StepIndicator(
    step: Int,
    label: String,
    isActive: Boolean,
    isDone: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = when {
                isDone -> AccentGreen
                isActive -> PrimaryBlue
                else -> TextHint.copy(alpha = 0.3f)
            },
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = if (isDone) "✓" else step.toString(),
                    color = if (isDone || isActive) White else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isActive) PrimaryBlue else TextSecondary,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun GenderChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFFE3F2FD) else White,
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) PrimaryBlue else TextHint
        )
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = if (selected) PrimaryBlue else TextSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    HealthcareTheme {
        RegisterScreen(onRegisterSuccess = {}, onBackToLogin = {})
    }
}