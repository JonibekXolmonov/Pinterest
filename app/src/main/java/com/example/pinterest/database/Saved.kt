package com.example.pinterest.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Saved")
data class Saved(


    @PrimaryKey()
    val savedID: String,

    val url: String,

    val description: String
)