package com.abhijith.runtrackerfitness.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abhijith.runtrackerfitness.db.converters.Converters
import com.abhijith.runtrackerfitness.db.dao.RunDao
import com.abhijith.runtrackerfitness.db.models.Run
import com.abhijith.runtrackerfitness.helpers.Constants

@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase() {

    abstract fun getRunDao(): RunDao

    companion object {

        private lateinit var rb: RunningDatabase
        operator fun invoke(app: Application): RunningDatabase {
            return if (!Companion::rb.isInitialized) {
                synchronized(Any()) {
                    if (!Companion::rb.isInitialized) {
                        Room
                            .databaseBuilder(
                                app,
                                RunningDatabase::class.java,
                                Constants.DATABASE_NAME
                            )
                            .fallbackToDestructiveMigration()
                            .build()
                    } else {
                        rb
                    }
                }
            } else {
                 rb
            }
        }
    }
}