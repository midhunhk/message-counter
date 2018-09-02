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

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ae.apps.messagecounter.R
import com.ae.apps.common.permissions.PermissionsAwareComponent
import kotlinx.android.synthetic.main.fragment_no_access.*

/**
 * A simple [Fragment] subclass.
 *
 */
class NoAccessFragment : Fragment() {

    companion object {
        fun newInstance(): NoAccessFragment {
            return NoAccessFragment()
        }
    }

    private lateinit var permissionsAwareContext: PermissionsAwareComponent

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_no_access, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            permissionsAwareContext = activity as PermissionsAwareComponent
        } catch (ex: ClassCastException) {
            throw IllegalAccessException("Parent ${activity.toString()} must implement PermissionsAwareComponent interface")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnRequestPermissions.setOnClickListener {
            permissionsAwareContext.requestForPermissions()
        }
    }

}
