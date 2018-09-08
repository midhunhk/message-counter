/*
 * Copyright 2018 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ae.apps.messagecounter.data

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.ae.apps.messagecounter.data.dao.CounterDao
import com.ae.apps.messagecounter.data.dao.IgnoredNumbersDao
import com.ae.apps.messagecounter.data.models.Counter
import com.ae.apps.messagecounter.data.models.IgnoredNumber

/**
 * The database for this app
 */
@Database(entities = [Counter::class, IgnoredNumber::class], version = 3)
abstract class AppDatabase : RoomDatabase(){

    abstract fun counterDao():CounterDao

    abstract fun ignoredNumbersDao():IgnoredNumbersDao

    companion object {
        // To hold the singleton
        @Volatile private var instance:AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this){
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                TODO("Add Ignore List Table") //To change body of created functions use File | Settings | File Templates.
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Empty Migration plan
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "db_message_counter")
                    .addMigrations(MIGRATION_2_3)
                    .build()
        }
    }
}