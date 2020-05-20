/*
 *  Copyright 2020 Midhun Harikumar
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ae.apps.messagecounter

import com.ae.apps.messagecounter.data.smsbackup.ensureKeyLength
import org.junit.Assert
import org.junit.Test

class EncryptDecryptTest {

    @Test
    fun ensureKeyLength_same(){
        val input = "TOBEREPLACEDLA8R"
        val result = ensureKeyLength(input)
        print(result)
        Assert.assertEquals(16, result.length)
    }

    @Test
    fun ensureKeyLength_larger(){
        val input = "TOBEREJHASGPLACEDLA8RASOIH"
        val result = ensureKeyLength(input)
        print(result)
        Assert.assertEquals(16, result.length)
    }

    @Test
    fun ensureKeyLength_smaller(){
        val input = "KAH"
        val result = ensureKeyLength(input)
        print(result)
        Assert.assertEquals(16, result.length)
    }
}