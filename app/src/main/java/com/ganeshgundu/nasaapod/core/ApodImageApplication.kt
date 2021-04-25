package com.ganeshgundu.nasaapod.core

import android.app.Application
import com.ganeshgundu.nasaapod.db.ApodImageDatabase
import com.ganeshgundu.nasaapod.repository.ApodImageRepository

class ApodImageApplication : Application () {
    val database by lazy { ApodImageDatabase.getDatabase(this) }
    val repository by lazy { ApodImageRepository(database.apodImageDao(),this) }
}