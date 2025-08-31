package com.stocksentry.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.stocksentry.app.navigation.Routes
import com.stocksentry.app.ui.theme.GradientBrush
import com.stocksentry.app.viewmodels.PasswordViewModel

@Composable
fun PasswordLockScreen(navController: NavController) {
    val viewModel: PasswordViewModel = hiltViewModel()
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Debug: Check what users are in the database when screen loads
    LaunchedEffect(Unit) {
        viewModel.debugGetAllUsers()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Enter Password to Unlock",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            // Test button to create a test user (remove this in production)
            TextButton(
                onClick = { viewModel.createTestUser() }
            ) {
                Text(
                    text = "Create Test User (password: password123)",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    if (error) error = false
                },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            )

            if (error) {
                Text(
                    text = "Incorrect Password",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    if (password.isNotBlank()) {
                        isLoading = true
                        android.util.Log.d("PasswordLockScreen", "Attempting to validate password: $password")
                        viewModel.validatePassword(password) { isValid ->
                            isLoading = false
                            android.util.Log.d("PasswordLockScreen", "Password validation result: $isValid")
                            if (isValid) {
                                navController.navigate(Routes.MAIN_WATCHLIST) {
                                    popUpTo(Routes.PASSWORD_LOCK) { inclusive = true }
                                }
                            } else {
                                error = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = password.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Unlock")
                }
            }
        }
    }
}

