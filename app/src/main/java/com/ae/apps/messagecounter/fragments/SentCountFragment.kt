package com.ae.apps.messagecounter.fragments

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
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
import com.ae.apps.messagecounter.data.models.SentCountDetails
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.data.repositories.CounterRepository
import com.ae.apps.messagecounter.data.repositories.IgnoredNumbersRepository
import com.ae.apps.messagecounter.data.viewmodels.CounterViewModel
import com.ae.apps.messagecounter.data.viewmodels.CounterViewModelFactory
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
        initViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sent_count, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFindViewByIdCache()
        mViewModel.getSentCountDetails().removeObservers(this)
    }

    private fun initViewModel() {
        val preferenceRepository = PreferenceRepository.newInstance(PreferenceManager.getDefaultSharedPreferences(requireContext()))
        val counterRepository = CounterRepository.getInstance(AppDatabase.getInstance(requireContext()).counterDao())
        val ignoreNumbersRepository = IgnoredNumbersRepository.getInstance(AppDatabase.getInstance(requireContext()).ignoredNumbersDao())
        val factory = CounterViewModelFactory(counterRepository, ignoreNumbersRepository, preferenceRepository)
        mViewModel = ViewModelProviders.of(this, factory).get(CounterViewModel::class.java)

        // Fetch the data that is already in the database
        mViewModel.getSentCountData()

        // Index messages that were sent before the app was installed
        // Or sent during the time a background service was not running
        mViewModel.indexMessages(requireContext())
    }

    private fun initUI() {
        mViewModel.getSentCountDetails().observe(this,
                Observer{details:SentCountDetails? -> run {
            heroSentTodayText.text = details?.sentToday.toString()
            heroSentInCycleText.text = details?.sentCycle.toString()
            countProgressText.text = details?.sentCycle.toString()
            cycleDurationText.text = details?.cycleDuration
            countSentTodayText.text = details?.sentToday.toString()
            countSentThisWeekText.text = details?.sentInWeek.toString()
            countSentThisYearText.text = details?.startYearCount.toString()
            prevCycleSentCountText.text = details?.sentLastCycle.toString()

            // set progress bars
            setProgressInfo(countProgressBar, countProgressText, details!!.sentCycle, details.cycleLimit)
            setProgressInfo(prevCountProgressBar, prevCycleSentCountText, details.sentLastCycle, details.cycleLimit)
        }})
    }

    @SuppressLint("SetTextI18n")
    private fun setProgressInfo(progressBar: ProgressBar, progressText: TextView, count: Int, limit: Int) {
        if (limit > 0) {
            progressBar.max = limit
            progressBar.progress = 0
            var progress = count
            if (count >= limit) progress = limit
            progressBar.progress = progress
            progressText.text = "$count / $limit"
        }
    }

}
