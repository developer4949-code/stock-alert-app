package com.stocksentry.app.data.local

import com.stocksentry.app.data.local.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [UserEntity::class, WatchlistEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun watchlistDao(): WatchlistDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE watchlists ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE watchlists ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new users table with email as primary key
                database.execSQL("""
                    CREATE TABLE users_new (
                        email TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        phoneNumber TEXT NOT NULL,
                        password TEXT NOT NULL
                    )
                """)
                
                // Copy data from old table to new table
                database.execSQL("""
                    INSERT INTO users_new (email, userId, name, phoneNumber, password)
                    SELECT email, userId, name, phoneNumber, password FROM users
                """)
                
                // Drop old table
                database.execSQL("DROP TABLE users")
                
                // Rename new table to users
                database.execSQL("ALTER TABLE users_new RENAME TO users")
            }
        }
    }
}
