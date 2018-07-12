package com.ae.apps.messagecounter.data.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tbl_sms_counter")
data class Counter (
        @PrimaryKey
        @ColumnInfo(name = "date_index")
        val dateIndex : String,

        @ColumnInfo(name = "sent_count")
        val sentCount : Int
)