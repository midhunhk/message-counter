package com.ae.apps.messagecounter.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.ae.apps.messagecounter.data.models.IgnoredNumber

@Dao
interface IgnoredNumbersDao {

    @Query("SELECT * from tbl_ignore_list")
    fun getAllIgnoredNumbers(): List<IgnoredNumber>

    @Query("SELECT EXISTS(SELECT 1 from tbl_ignore_list WHERE ignore_number = :numberToCheck LIMIT 1)")
    fun checkIfNumberIsIgnored(numberToCheck:String):Int

    @Insert
    fun insertIgnoreNumber(ignoredNumber: IgnoredNumber): Long

    @Delete
    fun deleteIgnoredNumber(ignoredNumber: IgnoredNumber)

}