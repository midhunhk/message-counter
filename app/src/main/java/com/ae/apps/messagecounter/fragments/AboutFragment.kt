package com.ae.apps.messagecounter.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ae.apps.messagecounter.*
import kotlinx.android.synthetic.main.fragment_about.*

/**
 * A simple [Fragment] subclass.
 *
 */
class AboutFragment : Fragment() {

    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        viewLicense.setOnClickListener {
            startActivity(getOpenSourceLicenceDisplayIntent(requireContext()))
        }
        viewSourceCode.setOnClickListener {
            startActivity(getViewSourceIntent(requireContext()))
        }
        viewFAQ.setOnClickListener{
            startActivity(getViewFaqIntent(requireContext()))
        }
        sendFeedback.setOnClickListener {
            startActivity(getFeedbackIntent(requireContext()))
        }
    }


}
