package com.stocksentry.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stocksentry.app.data.local.UserDao
import com.stocksentry.app.data.local.UserEntity
import com.stocksentry.app.data.remote.StockSentryApi
import com.stocksentry.app.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val userDao: UserDao,
    private val api: StockSentryApi
) : ViewModel() {

    fun createUser(name: String, email: String, phoneNumber: String, password: String, onComplete: (String?) -> Unit) {
        viewModelScope.launch {
            val normalizedEmail = email.trim().lowercase()
            val normalizedPassword = password.trim()
            val normalizedPhone = phoneNumber.trim()
            try {
                Log.d("WelcomeViewModel", "Creating user: $name, $normalizedEmail, $normalizedPhone")

                val userPayload = User(
                    name = name,
                    email = normalizedEmail,
                    phoneNumber = normalizedPhone
                )

                // Network: get backend UUID (text/plain)
                val backendUserId = withContext(Dispatchers.IO) { api.createUser(userPayload) }
                Log.d("WelcomeViewModel", "Backend createUser returned: $backendUserId")

                // Try to persist locally by upserting the user; do not clear watchlists
                try {
                    withContext(Dispatchers.IO) {
                        val userEntity = UserEntity(
                            email = normalizedEmail,
                            userId = backendUserId,
                            name = name,
                            phoneNumber = normalizedPhone,
                            password = normalizedPassword
                        )
                        userDao.insertUser(userEntity)
                        val count = userDao.getAllUsers().size
                        Log.d("WelcomeViewModel", "Stored backend user locally. Users in DB=$count")
                    }
                } catch (dbError: Exception) {
                    Log.e("WelcomeViewModel", "Failed to store backend user locally", dbError)
                }

                // Always return backend ID to UI so it can proceed
                onComplete(backendUserId)
            } catch (networkError: Exception) {
                Log.e("WelcomeViewModel", "Signup network failed, storing offline user", networkError)
                val offlineUserId = "offline_${System.currentTimeMillis()}"
                try {
                    withContext(Dispatchers.IO) {
                        val userEntity = UserEntity(
                            email = normalizedEmail,
                            userId = offlineUserId,
                            name = name,
                            phoneNumber = normalizedPhone,
                            password = normalizedPassword
                        )
                        userDao.insertUser(userEntity)
                        val count = userDao.getAllUsers().size
                        Log.d("WelcomeViewModel", "Stored offline user locally. Users in DB=$count")
                    }
                } catch (localError: Exception) {
                    Log.e("WelcomeViewModel", "Failed to store offline user locally", localError)
                }
                // Still return offline ID so UI can proceed to local mode
                onComplete(offlineUserId)
            }
        }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val normalizedEmail = email.trim().lowercase()
                val normalizedPassword = password.trim()
                val localUser = withContext(Dispatchers.IO) {
                    val users = userDao.getAllUsers()
                    Log.d("WelcomeViewModel", "Login check: users in DB=${users.size}")
                    userDao.getUserByEmail(normalizedEmail)
                }
                if (localUser != null && localUser.password == normalizedPassword) {
                    try {
                        val payload = User(
                            name = localUser.name,
                            email = localUser.email,
                            phoneNumber = localUser.phoneNumber
                        )
                        val backendUserId = withContext(Dispatchers.IO) { api.createUser(payload) }
                        val updatedUser = localUser.copy(userId = backendUserId)
                        try {
                            withContext(Dispatchers.IO) { userDao.updateUser(updatedUser) }
                        } catch (dbError: Exception) {
                            Log.e("WelcomeViewModel", "Failed to update local user on login", dbError)
                        }
                    } catch (backendError: Exception) {
                        Log.e("WelcomeViewModel", "Backend sync during login failed", backendError)
                    }
                    onComplete(true)
                    return@launch
                }
                onComplete(false)
            } catch (e: Exception) {
                Log.e("WelcomeViewModel", "Error during login", e)
                onComplete(false)
            }
        }
    }

    fun testApiConnection(callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = api.healthCheck()
                callback(true, result)
            } catch (e: Exception) {
                callback(false, e.message ?: "error")
            }
        }
    }

    fun debugGetAllUsers() {
        viewModelScope.launch {
            val users = userDao.getAllUsers()
            Log.d("WelcomeViewModel", "Users in DB: ${users.size}")
            users.forEach { Log.d("WelcomeViewModel", "user=${it.email}, id=${it.userId}") }
        }
    }

    // No longer clearing user or watchlist data on user creation
}
