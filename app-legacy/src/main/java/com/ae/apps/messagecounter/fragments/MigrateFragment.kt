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
package com.ae.apps.messagecounter.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ae.apps.messagecounter.R
import kotlinx.android.synthetic.main.fragment_migrate.*

/**
 * Migration Fragment
 */
class MigrateFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = MigrateFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_migrate, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUI()
    }

    private fun initUI() {

        btnViewAlternates.setOnClickListener {
            val alternatesUrl = requireContext().getString(R.string.app_migrate_alternates)
            startActivity(createIntentForURI(alternatesUrl))
        }

        btnMoreDetails.setOnClickListener {
            val alternatesUrl = requireContext().getString(R.string.app_migrate_more_details)
            startActivity(createIntentForURI(alternatesUrl))
        }
    }

    private fun createIntentForURI(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        return intent
    }
}
