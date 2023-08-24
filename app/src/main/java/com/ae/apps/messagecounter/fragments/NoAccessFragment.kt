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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import com.ae.apps.messagecounter.AppRequestPermission
import com.ae.apps.messagecounter.databinding.FragmentNoAccessBinding

class NoAccessFragment : Fragment(), DefaultLifecycleObserver {

    companion object {
        fun newInstance(): NoAccessFragment {
            return NoAccessFragment()
        }
    }

    private lateinit var permissionsAwareContext: AppRequestPermission
    private lateinit var binding: FragmentNoAccessBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoAccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            permissionsAwareContext = activity as AppRequestPermission
        } catch (ex: ClassCastException) {
            throw IllegalAccessException("Parent ${activity.toString()} must implement AppRequestPermission interface")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Fragment>.onCreate(savedInstanceState)

        binding.btnRequestPermissions.setOnClickListener {
            permissionsAwareContext.invokeRequestPermissions()
        }
    }

}
