package com.example.medicina.model

import androidx.room.Room
import com.example.medicina.database.MedicinaDatabase
import android.content.Context

class Data(context: Context) {
    val db = Room.databaseBuilder(
        context.applicationContext,
        MedicinaDatabase::class.java,
        "medicina_database"
    )
        .fallbackToDestructiveMigration(false)
        .build()
}
