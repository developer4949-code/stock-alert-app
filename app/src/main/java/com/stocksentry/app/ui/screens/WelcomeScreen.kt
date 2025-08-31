package com.stocksentry.app.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.stocksentry.app.viewmodels.WelcomeViewModel

@Composable
fun WelcomeScreen(navController: NavController) {
    val viewModel: WelcomeViewModel = hiltViewModel()
    var isLoginMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .verticalScroll(rememberScrollState())
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Welcome to StockSentry",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            // Debug buttons (remove in production)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = { viewModel.debugGetAllUsers() }
                ) {
                    Text(
                        text = "Debug: Check DB",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
                
                TextButton(
                    onClick = { 
                        viewModel.testApiConnection { success, message ->
                            Log.d("WelcomeScreen", "API test result: success=$success, message=$message")
                        }
                    }
                ) {
                    Text(
                        text = "Debug: Test API",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
            
            Text(
                text = if (isLoginMode) "Sign in to your account" else "Create your account to get started",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )

            // Error message display
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (!isLoginMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
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
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
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

            if (!isLoginMode) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
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
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
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

            Button(
                onClick = {
                    // Clear any previous error messages
                    errorMessage = null
                    
                    if (isLoginMode) {
                        // Handle login
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            Log.d("WelcomeScreen", "Attempting login for email: $email")
                            viewModel.loginUser(email, password) { success ->
                                isLoading = false
                                Log.d("WelcomeScreen", "Login result: $success")
                                if (success) {
                                    Log.d("WelcomeScreen", "Login successful, navigating to watchlist...")
                                    navController.navigate(Routes.MAIN_WATCHLIST) {
                                        popUpTo(Routes.WELCOME) { inclusive = true }
                                    }
                                    Log.d("WelcomeScreen", "Navigation to watchlist completed")
                                } else {
                                    Log.e("WelcomeScreen", "Login failed")
                                    errorMessage = "Invalid email or password. Please try again."
                                }
                            }
                        } else {
                            errorMessage = "Please fill in all required fields."
                        }
                    } else {
                        // Handle registration
                        if (name.isNotBlank() && email.isNotBlank() && phoneNumber.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            Log.d("WelcomeScreen", "Attempting registration for email: $email")
                            viewModel.createUser(name, email, phoneNumber, password) { userId ->
                                isLoading = false
                                Log.d("WelcomeScreen", "Registration result: userId = $userId")
                                if (userId != null) {
                                    Log.d("WelcomeScreen", "Registration successful, navigating to watchlist...")
                                    navController.navigate(Routes.MAIN_WATCHLIST) {
                                        popUpTo(Routes.WELCOME) { inclusive = true }
                                    }
                                    Log.d("WelcomeScreen", "Navigation to watchlist completed")
                                } else {
                                    Log.e("WelcomeScreen", "Registration failed")
                                    errorMessage = "Registration failed. Please try again."
                                }
                            }
                        } else {
                            errorMessage = "Please fill in all required fields."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = if (isLoginMode) {
                    email.isNotBlank() && password.isNotBlank() && !isLoading
                } else {
                    name.isNotBlank() && email.isNotBlank() && phoneNumber.isNotBlank() && password.isNotBlank() && !isLoading
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isLoginMode) "Sign In" else "Create Account")
                }
            }

            TextButton(
                onClick = { 
                    isLoginMode = !isLoginMode
                    errorMessage = null // Clear error message when switching modes
                }
            ) {
                Text(
                    text = if (isLoginMode) "Don't have an account? Sign up" else "Already have an account? Sign in",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

