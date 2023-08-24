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

import androidx.room.*
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