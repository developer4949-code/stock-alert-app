package com.stocksentry.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val userId: String,
    val name: String,
    val phoneNumber: String,
    val password: String // Encrypt this in production using EncryptedSharedPreferences or SQLCipher
)

