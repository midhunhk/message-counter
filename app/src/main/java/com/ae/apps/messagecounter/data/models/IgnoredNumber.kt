package com.ae.apps.messagecounter.data.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tbl_ignore_list")
data class IgnoredNumber(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        var id: Long = 0,

        @ColumnInfo(name = "ignore_name")
        val ignoreName : String,

        @ColumnInfo(name = "ignore_number")
        val ignoreNumber : String,

        @ColumnInfo(name = "ignore_custom_col01")
        val ignoreCustom : String
){
    @Ignore
    constructor(ignoreName:String, ignoreNumber:String) : this(0,ignoreName, ignoreNumber, "")
}
