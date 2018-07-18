package com.ae.apps.messagecounter.data.repositories

import com.ae.apps.messagecounter.data.dao.IgnoredNumbersDao
import com.ae.apps.messagecounter.data.models.IgnoredNumber

class IgnoredNumbersRepository private constructor(private val dao: IgnoredNumbersDao) {

    companion object {
        @Volatile
        private var instance: IgnoredNumbersRepository? = null

        fun getInstance(dao: IgnoredNumbersDao) =
                instance ?: synchronized(this) {
                    instance ?: IgnoredNumbersRepository(dao).also { instance = it }
                }
    }

    fun getAllIgnoredNumbers() = dao.getAllIgnoredNumbers()

    fun checkIfNumberIsIgnored(numberToCheck: String): Boolean {
        return dao.checkIfNumberIsIgnored(numberToCheck) == 1
    }

    fun ignoreNumber(numberToIgnore:IgnoredNumber) {
        dao.insertIgnoreNumber(numberToIgnore)
    }

    fun unIgnoreNumber(ignoredNumber:IgnoredNumber) = dao.deleteIgnoredNumber(ignoredNumber)
}