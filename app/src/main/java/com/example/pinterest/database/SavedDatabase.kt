package com.example.pinterest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pinterest.database.dao.SavedDao

@Database(entities = [Saved::class], version = 1)
abstract class SavedDatabase : RoomDatabase() {

    abstract fun savedDao(): SavedDao

    companion object {
        private var instance: SavedDatabase? = null

        @Synchronized
        fun getInstance(context: Context): SavedDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, SavedDatabase::class.java, "saved.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}