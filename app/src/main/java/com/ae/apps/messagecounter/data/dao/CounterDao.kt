package com.ae.apps.messagecounter.data.dao

import android.arch.persistence.room.*
import com.ae.apps.messagecounter.data.models.Counter

@Dao
interface CounterDao {

    @Query("SELECT * from tbl_sms_counter WHERE date_index = :dateIndex")
    fun getCount(dateIndex:Long): Int

    @Query("SELECT SUM(sent_count) FROM tbl_sms_counter WHERE date_index >= :dateIndex")
    fun getTotalCountSince(dateIndex: Long): Int

    @Query("SELECT SUM(sent_count) FROM tbl_sms_counter WHERE date_index BETWEEN :startIndex AND :endIndex")
    fun getTotalCountBetween(startIndex: Long, endIndex: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(counter: Counter): Long

    @Update
    fun update(counter: Counter): Int

    /**
     * Inserts a new row for this date index if this is the first count logged,
     * else updates the current count with the new value
     */
    fun insertOrUpdate(counter: Counter){
        val id: Long = insert(counter)
        if(id.equals(-1)){
            val dateIndex = java.lang.Long.parseLong(counter.dateIndex)
            val currentCount: Int = getCount(dateIndex)
            val updatedCount = counter.sentCount + currentCount
            val updatedCounter = Counter(counter.dateIndex, updatedCount)
            update(updatedCounter)
        }
    }

}