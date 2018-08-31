package com.ae.apps.messagecounter.fragments

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.listeners.IgnoreContactListener
import com.ae.apps.messagecounter.data.models.IgnoredNumber
import kotlinx.android.synthetic.main.fragment_add_ignored_contact_dialog.*

class IgnoreDialogFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance() = IgnoreDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_ignored_contact_dialog, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onStart() {
        super.onStart()
        if (null != dialog.window) {
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        btnCancel.setOnClickListener {
            dismiss()
        }
        btnIgnoreContact.setOnClickListener {
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
        if (TextUtils.isEmpty(txtIgnoreNumber.text)) {
            throw IgnoreDialogValidationException("Number is required")
        }
        val ignoredName = if (TextUtils.isEmpty(txtIgnoreName.text)) {
            txtIgnoreNumber.text.toString()
        } else {
            txtIgnoreName.text.toString()
        }
        return IgnoredNumber(ignoredName, txtIgnoreNumber.text.toString())
    }

    /**
     * Validation class for IgnoreDialog
     */
    private inner class IgnoreDialogValidationException internal constructor(message: String) : Throwable(message)
}