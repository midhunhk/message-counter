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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.databinding.FragmentMigrateBinding

/**
 * Migration Fragment
 */
class MigrateFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = MigrateFragment()
    }

    private lateinit var binding: FragmentMigrateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMigrateBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initUI() {
        binding.btnViewAlternates.setOnClickListener {
            val alternatesUrl = requireContext().getString(R.string.app_migrate_alternates)
            startActivity(createIntentForURI(alternatesUrl))
        }

        binding.btnMoreDetails.setOnClickListener {
            val alternatesUrl = requireContext().getString(R.string.app_migrate_more_details)
            startActivity(createIntentForURI(alternatesUrl))
        }

        binding.btnViewSource.setOnClickListener {
            val viewSourceUrl = requireContext().getString(R.string.app_github_source_url)
            startActivity(createIntentForURI(viewSourceUrl))
        }
    }

    private fun createIntentForURI(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        return intent
    }
}
