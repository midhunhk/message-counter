package com.ae.apps.messagecounter.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ae.apps.messagecounter.R

/**
 * A simple [Fragment] subclass.
 *
 */
class IgnoreNumbersFragment : Fragment() {

    companion object {
        fun newInstance(): IgnoreNumbersFragment {
            return IgnoreNumbersFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ignore_numbers, container, false)
    }


}
