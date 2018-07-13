package com.ae.apps.messagecounter.fragments


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.models.Counter
import com.ae.apps.messagecounter.data.viewmodels.CounterViewModel
import kotlinx.android.synthetic.*

import kotlinx.android.synthetic.main.fragment_sent_count.*

/**
 * A simple [Fragment] subclass.
 *
 */
class SentCountFragment : Fragment() {

    companion object {
        fun newInstance() = SentCountFragment()
    }

    private lateinit var mViewModel:CounterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(CounterViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sent_count, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI();
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFindViewByIdCache()
    }

    private fun initUI(){

    }
}
