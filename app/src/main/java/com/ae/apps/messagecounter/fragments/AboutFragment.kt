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
package com.ae.apps.messagecounter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ae.apps.lib.common.utils.DialogUtils
import com.ae.apps.messagecounter.*

class AboutFragment : Fragment() {

    companion object {
        fun newInstance() = AboutFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    private fun initUI() {
        // TODO
        /*
        viewLicense.setOnClickListener {
            // startActivity(getOpenSourceLicenceDisplayIntent(requireContext()))
            DialogUtils.showMaterialInfoDialog(context, R.string.menu_title_license, R.string.str_license_desc,
                    android.R.string.ok)
        }

        viewSourceCode.setOnClickListener {
            startActivity(getViewSourceIntent(requireContext()))
        }
        viewFAQ.setOnClickListener {
            startActivity(getViewFaqIntent(requireContext()))
        }
        sendFeedback.setOnClickListener {
            startActivity(getFeedbackIntent(requireContext()))
        }

         */
    }


}
