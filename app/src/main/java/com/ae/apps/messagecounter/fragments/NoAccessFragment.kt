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
