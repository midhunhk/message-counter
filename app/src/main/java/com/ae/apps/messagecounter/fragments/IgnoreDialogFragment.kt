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
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.listeners.IgnoreContactListener
import com.ae.apps.messagecounter.data.models.IgnoredNumber
import com.ae.apps.messagecounter.databinding.FragmentAddIgnoredContactDialogBinding

class IgnoreDialogFragment : AppCompatDialogFragment(R.layout.fragment_add_ignored_contact_dialog) {

    companion object {
        fun newInstance() = IgnoreDialogFragment()
    }

    private lateinit var binding: FragmentAddIgnoredContactDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddIgnoredContactDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStyle(STYLE_NO_TITLE, 0)
        initUI()
    }

    override fun onStart() {
        super.onStart()
        /*
        if (null != dialog.window) {
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

         */
    }

    private fun initUI() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnIgnoreContact.setOnClickListener {
            try {
                val ignoredNumber = validateIgnoredNumber()

                updateWithSelectedContact(ignoredNumber)

                dismiss()
            } catch (e: IgnoreDialogValidationException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun updateWithSelectedContact(ignoredNumber: IgnoredNumber) {
        if (targetFragment is IgnoreContactListener) {
            (targetFragment as IgnoreContactListener).onContactSelected(ignoredNumber)
        } else {
            throw RuntimeException("must implement the interface IgnoreDialogListener")
        }
    }

    private fun validateIgnoredNumber(): IgnoredNumber {
        if (TextUtils.isEmpty(binding.txtIgnoreNumber.text)) {
            throw IgnoreDialogValidationException("Number is required")
        }
        val ignoredName = if (TextUtils.isEmpty(binding.txtIgnoreName.text)) {
            binding.txtIgnoreNumber.text.toString()
        } else {
            binding.txtIgnoreName.text.toString()
        }
        return IgnoredNumber(ignoredName, binding.txtIgnoreNumber.text.toString())
    }

    /**
     * Validation class for IgnoreDialog
     */
    private inner class IgnoreDialogValidationException internal constructor(message: String) :
        Throwable(message)
}