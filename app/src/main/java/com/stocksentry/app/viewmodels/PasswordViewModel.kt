package com.stocksentry.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stocksentry.app.data.local.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import android.util.Log
import com.stocksentry.app.data.local.UserEntity
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(private val userDao: UserDao) : ViewModel() {

    fun validatePassword(input: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("PasswordViewModel", "Validating password: $input")
                
                // Get the first user from the database
                val user = userDao.getUser()
                Log.d("PasswordViewModel", "Found user: ${user?.email}, stored password: ${user?.password}")
                
                if (user != null) {
                    val isValid = user.password == input
                    Log.d("PasswordViewModel", "Password validation result: $isValid")
                    onResult(isValid)
                } else {
                    Log.e("PasswordViewModel", "No user found in database")
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("PasswordViewModel", "Error validating password", e)
                onResult(false)
            }
        }
    }

    // Alternative method to validate password for a specific email
    fun validatePasswordForEmail(email: String, input: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("PasswordViewModel", "Validating password for email: $email")
                
                val user = userDao.getUserByEmail(email)
                Log.d("PasswordViewModel", "Found user: ${user?.email}, stored password: ${user?.password}")
                
                if (user != null) {
                    val isValid = user.password == input
                    Log.d("PasswordViewModel", "Password validation result: $isValid")
                    onResult(isValid)
                } else {
                    Log.e("PasswordViewModel", "No user found for email: $email")
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("PasswordViewModel", "Error validating password for email", e)
                onResult(false)
            }
        }
    }

    // Debug method to check all users in database
    fun debugGetAllUsers() {
        viewModelScope.launch {
            try {
                val users = userDao.getAllUsers()
                Log.d("PasswordViewModel", "All users in database: ${users.size}")
                users.forEach { user ->
                    Log.d("PasswordViewModel", "User: ${user.email}, Password: ${user.password}, ID: ${user.userId}")
                }
            } catch (e: Exception) {
                Log.e("PasswordViewModel", "Error getting all users", e)
            }
        }
    }

    // Test method to create a test user
    fun createTestUser() {
        viewModelScope.launch {
            try {
                val testUser = UserEntity(
                    email = "test@example.com",
                    userId = "test_user_${System.currentTimeMillis()}",
                    name = "Test User",
                    phoneNumber = "1234567890",
                    password = "password123"
                )
                userDao.insertUser(testUser)
                Log.d("PasswordViewModel", "Test user created successfully")
                debugGetAllUsers()
            } catch (e: Exception) {
                Log.e("PasswordViewModel", "Error creating test user", e)
            }
        }
    }
}


