package com.ae.apps.messagecounter.utils

import com.ae.apps.messagecounter.data.models.Counter
import java.util.*

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
            val random = Random(10)
            for (i in 0..size) {
                list.add(Counter("20180716", random.nextInt()))
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