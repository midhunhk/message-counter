/*
 * Copyright 2019 Midhun Harikumar
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

package com.ae.apps.messagecounter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ae.apps.messagecounter.core.analytics.AppAnalytics
import com.ae.apps.messagecounter.fragments.MigrateFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        AppAnalytics.newInstance(baseContext)
            .logAppStart(resources.getString(R.string.app_name))
    }

    private fun init() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MigrateFragment.newInstance())
                .commitAllowingStateLoss()
    }
}
