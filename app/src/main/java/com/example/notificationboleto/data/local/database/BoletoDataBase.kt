package com.example.notificationboleto.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notificationboleto.data.local.dao.BoletoDao
import com.example.notificationboleto.data.local.entity.BoletoEntity

@Database(
    entities = [BoletoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun boletoDao(): BoletoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notification_boleto_db"
                ).build().also { INSTANCE = it }
            }
    }
}
