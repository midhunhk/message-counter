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
package com.ae.apps.messagecounter.data.dao

import android.arch.persistence.room.*
import com.ae.apps.messagecounter.data.models.Counter

@Dao
abstract class CounterDao {

    @Query("SELECT sent_count from tbl_sms_counter WHERE date_index = :dateIndex")
    abstract fun getCount(dateIndex:String): Int

    @Query("SELECT SUM(sent_count) FROM tbl_sms_counter WHERE date_index >= :dateIndex")
    abstract fun getTotalCountSince(dateIndex: String): Int

    @Query("SELECT SUM(sent_count) FROM tbl_sms_counter WHERE date_index BETWEEN :startIndex AND :endIndex")
    abstract fun getTotalCountBetween(startIndex: String, endIndex: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(counter: Counter): Long

    @Update
    abstract fun update(counter: Counter): Int

    /**
     * Inserts a new row for this date index if this is the first count logged,
     * else updates the current count with the new value
     */
   fun insertOrUpdate(counter: Counter){
        val id: Long = insert(counter)
        if(id == -1L){
            val currentCount: Int = getCount(counter.dateIndex)
            val updatedCount = counter.sentCount + currentCount
            val updatedCounter = Counter(counter.dateIndex, updatedCount)
            update(updatedCounter)
        }
    }

}