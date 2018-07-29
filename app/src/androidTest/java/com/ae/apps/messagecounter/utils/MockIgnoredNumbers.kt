package com.ae.apps.messagecounter.utils

import com.ae.apps.messagecounter.data.models.IgnoredNumber

class MockIgnoredNumbers {

    companion object {

        fun getIgnoredNumber():IgnoredNumber{
            return IgnoredNumber("John Doe", "+14377812256")
        }

        fun getIgnoredNumbers(size: Int): List<IgnoredNumber> {
            val list:MutableList<IgnoredNumber> = ArrayList(size)
            for(i in 1..size){
                list.add(IgnoredNumber("Jane Doe", "+134567"))
            }
            return list
        }

    }
}