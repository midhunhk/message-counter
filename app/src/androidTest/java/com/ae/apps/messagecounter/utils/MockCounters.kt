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