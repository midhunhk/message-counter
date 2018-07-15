package com.ae.apps.messagecounter.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.AppDatabase
import com.ae.apps.messagecounter.data.repositories.CounterRepository
import com.ae.apps.messagecounter.data.viewmodels.CounterViewModel
import com.ae.apps.messagecounter.data.viewmodels.CounterViewModelFactory
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_sent_count.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

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
        initViewModel()
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

    private fun initViewModel() {
        val repository = CounterRepository.getInstance(AppDatabase.getInstance(requireContext()).counterDao())
        val factory = CounterViewModelFactory(repository, PreferenceManager.getDefaultSharedPreferences(requireContext()))
        mViewModel = ViewModelProviders.of(this, factory).get(CounterViewModel::class.java)
    }

    private fun initUI() {
        doAsync {
            val details = mViewModel.getSentCountData()
            uiThread {
                heroSentTodayText.text = details.sentToday.toString()
                heroSentInCycleText.text = details.sentCycle.toString()
                countProgressText.text = details.sentCycle.toString()
                //cycleDurationText.setText(details.)
                countSentTodayText.text = details.sentToday.toString()
                countSentThisWeekText.text = details.sentInWeek.toString()
                prevCycleSentCountText.text = details.sentLastCycle.toString()

                // set progress bars
                setProgressInfo(countProgressBar, countProgressText, details.sentCycle, details.cycleLimit)
                setProgressInfo(prevCountProgressBar, prevCycleSentCountText, details.sentLastCycle, details.cycleLimit)
            }
        }
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
