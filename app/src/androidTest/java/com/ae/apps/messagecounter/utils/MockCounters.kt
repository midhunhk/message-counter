package com.ae.apps.messagecounter.utils

import com.ae.apps.messagecounter.data.models.Counter
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class MockCounters {

    companion object {
        fun getSimpleCounter(): Counter {
            return Counter("20180715", 1)
        }

        fun getCounterWithCount(count: Int): Counter {
            return Counter("20180718", count)
        }

        fun getCounters(size: Int): List<Counter> {
            val list: MutableList<Counter> = ArrayList(size)
            for (i in 0..size) {
                val randomNumber = ThreadLocalRandom.current().nextInt(0, 11)
                list.add(Counter("20180716", randomNumber))
            }
            return list
        }

        fun getSentCount(list: List<Counter>): Int {
            var sum = 0
            list.forEach {
                sum += it.sentCount
            }
            return sum
        }
    }
}