package com.ae.apps.messagecounter.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.ae.apps.messagecounter.R
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

    private lateinit var mViewModel: CounterViewModel

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
        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFindViewByIdCache()
    }

    private fun initUI() {
        val details = mViewModel.getSentCountData()
        heroSentTodayText.setText(details.sentToday)
        heroSentInCycleText.setText(details.sentCycle)
        countProgressText.setText(details.sentCycle)
        //cycleDurationText.setText(details.)
        countSentTodayText.setText(details.sentToday)
        countSentThisWeekText.setText(details.sentInWeek)
        prevCycleSentCountText.setText(details.sentLastCycle)

        // set progress bars
        setProgressInfo(countProgressBar, countProgressText, details.sentCycle, details.cycleLimit)
        setProgressInfo(prevCountProgressBar, prevCycleSentCountText, details.sentLastCycle, details.cycleLimit)
    }

    private fun setProgressInfo(progressBar: ProgressBar, progressText: TextView, count: Int, limit: Int) {
        if (limit > 0) {
            progressBar.max = limit
            progressBar.progress = 0
            var progress = count
            if (count >= limit) progress = limit
            progressBar.progress = progress
            progressText.text = count.toString() + " / " + limit
        }
    }
}
