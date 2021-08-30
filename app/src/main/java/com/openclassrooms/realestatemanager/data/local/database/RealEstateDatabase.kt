package com.openclassrooms.realestatemanager.data.local.database

import android.content.Context
import androidx.room.Database
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.data.local.dao.SinglePropertyDao
import com.openclassrooms.realestatemanager.data.local.dao.ImageOfPropertyDao
import kotlin.jvm.Volatile
import androidx.room.Room

@Database(entities = [SingleProperty::class, ImageOfProperty::class], version = 1)
abstract class RealEstateDatabase : RoomDatabase() {
    abstract fun singlePropertyDao(): SinglePropertyDao?
    abstract fun imageOfPropertyDao(): ImageOfPropertyDao?

    companion object {
        @Volatile
        private var INSTANCE: RealEstateDatabase? = null
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): RealEstateDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    RealEstateDatabase::class.java, "real_estate_manager_database"
                )
                    .fallbackToDestructiveMigration().allowMainThreadQueries().build()
            }
            return INSTANCE
        }
    }
}