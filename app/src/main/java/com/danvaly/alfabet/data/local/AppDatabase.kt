package com.danvaly.alfabet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.danvaly.alfabet.data.model.AppStats
import com.danvaly.alfabet.data.model.LetterProgress

@Database(
    entities = [LetterProgress::class, AppStats::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun progressDao(): ProgressDao

    companion object {
        const val DEFAULT_NAME = "alfabet_magic.db"

        /** One open database per file name (one file per child profile). */
        private val instances = mutableMapOf<String, AppDatabase>()

        fun getInstance(context: Context, dbName: String = DEFAULT_NAME): AppDatabase =
            synchronized(instances) {
                instances.getOrPut(dbName) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        dbName
                    ).fallbackToDestructiveMigration()
                        .build()
                }
            }

        /** Closes and forgets the database with this name (used before its file is deleted). */
        fun closeInstance(dbName: String) {
            val db = synchronized(instances) { instances.remove(dbName) }
            try {
                db?.close()
            } catch (e: Exception) {
            }
        }
    }
}
