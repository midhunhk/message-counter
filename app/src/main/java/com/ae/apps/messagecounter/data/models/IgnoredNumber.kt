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
