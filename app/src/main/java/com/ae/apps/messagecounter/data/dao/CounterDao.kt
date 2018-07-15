package com.ae.apps.messagecounter.data.dao

import android.arch.persistence.room.*
import com.ae.apps.messagecounter.data.models.Counter

@Dao
abstract class CounterDao {

    @Query("SELECT * from tbl_sms_counter WHERE date_index = :dateIndex")
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
        if(id.equals(-1)){
            val currentCount: Int = getCount(counter.dateIndex)
            val updatedCount = counter.sentCount + currentCount
            val updatedCounter = Counter(counter.dateIndex, updatedCount)
            update(updatedCounter)
        }
    }

}