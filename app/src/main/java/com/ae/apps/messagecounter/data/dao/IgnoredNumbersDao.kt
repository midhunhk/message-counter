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

    @Insert
    fun insertIgnoreNumber(ignoredNumber: IgnoredNumber): Long

    @Delete
    fun deleteIgnoredNumber(ignoredNumber: IgnoredNumber): Long

}