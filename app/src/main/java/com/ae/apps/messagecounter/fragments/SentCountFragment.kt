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

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import com.ae.apps.common.utils.CommonUtils
import com.ae.apps.messagecounter.AppController
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.AppDatabase
import com.ae.apps.messagecounter.data.models.SentCountDetails
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.data.repositories.CounterRepository
import com.ae.apps.messagecounter.data.repositories.IgnoredNumbersRepository
import com.ae.apps.messagecounter.data.viewmodels.CounterViewModel
import com.ae.apps.messagecounter.data.viewmodels.CounterViewModelFactory
import com.ae.apps.messagecounter.getWidgetUpdateIntent
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_sent_count.*

/**
 * Fragment that represents the SentCount state
 *
 */
class SentCountFragment : Fragment() {

    companion object {
        fun newInstance() = SentCountFragment()
        private const val PROGRESS_ANIMATION_DURATION = 800L
        // private const val PROGRESS_ANIMATION_DELAY = 400L
        private const val PROGRESS_PROPERTY_NAME = "progress"
    }

    private lateinit var mViewModel: CounterViewModel
    private lateinit var mAppController: AppController
    private lateinit var mPreferenceRepository: PreferenceRepository

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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mAppController = activity as AppController
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFindViewByIdCache()
        mViewModel.getSentCountDetails().removeObservers(this)
    }

    private fun initViewModel() {
        mPreferenceRepository = PreferenceRepository.newInstance(PreferenceManager.getDefaultSharedPreferences(requireContext()))
        val counterRepository = CounterRepository.getInstance(AppDatabase.getInstance(requireContext()).counterDao())
        val ignoreNumbersRepository = IgnoredNumbersRepository.getInstance(AppDatabase.getInstance(requireContext()).ignoredNumbersDao())
        val factory = CounterViewModelFactory(counterRepository, ignoreNumbersRepository, mPreferenceRepository)
        mViewModel = ViewModelProviders.of(requireActivity(), factory).get(CounterViewModel::class.java)
    }

    private fun initUI() {
        mViewModel.getSentCountDetails().observe(this,
                Observer { details: SentCountDetails? ->
                    run {
                        heroSentTodayText.text = details?.sentToday.toString()
                        heroSentInCycleText.text = details?.sentCycle.toString()
                        countProgressText.text = details?.sentCycle.toString()
                        cycleDurationText.text = details?.cycleDuration
                        countSentTodayText.text = details?.sentToday.toString()
                        countSentThisWeekText.text = details?.sentInWeek.toString()
                        countSentThisYearText.text = details?.startYearCount.toString()
                        prevCycleSentCountText.text = details?.sentLastCycle.toString()
                        prevCycleDurationText.text = details?.prevCycleDuration

                        setProgressInfo(countProgressBar, countProgressText, details!!.sentCycle, details.cycleLimit )
                        setProgressInfo(prevCountProgressBar, prevCycleSentCountText, details.sentLastCycle, details.cycleLimit)

                        // Update all the widgets
                        requireContext().sendBroadcast( getWidgetUpdateIntent(requireContext()) )
                    }
                }
        )

        // Fetch the data that is already in the database
        mViewModel.readSentCountDataFromRepository()

        // Index messages that were sent before the app was installed
        // Or sent during the time a background service was not running
        mViewModel.indexMessages(requireContext())

        manageInfoCard()
    }

    private fun manageInfoCard() {
        if (showInfoCard()) {
            info_card.visibility = View.VISIBLE
            info_card.setOnClickListener {
                if (reviewSettingsText.visibility == View.VISIBLE) {
                    mAppController.navigateTo(R.id.action_settings)
                    mPreferenceRepository.setSettingsHintReviewed()
                }
                info_card.visibility = View.GONE
            }
        }
    }

    private fun showInfoCard(): Boolean {
        if (CommonUtils.isFirstInstall(requireContext())
                && !mPreferenceRepository.getSettingsHintReviewed()) {
            // show an info message
            return true
        }
        return false
    }

    @SuppressLint("SetTextI18n")
    private fun setProgressInfo(progressBar: ProgressBar, progressText: TextView, count: Int, limit: Int,
                                animateProgress:Boolean = false, animationDelay: Long = 0) {
        if (limit > 0) {
            progressBar.max = limit
            progressBar.progress = 0
            var progress = count
            if (count >= limit) progress = limit
            progressText.text = "$count / $limit"

            if(animateProgress) {
                val animator = ObjectAnimator.ofInt(progressBar, PROGRESS_PROPERTY_NAME, 0, progress)
                animator.interpolator = DecelerateInterpolator()
                animator.duration = PROGRESS_ANIMATION_DURATION
                animator.startDelay = animationDelay
                animator.start()
            } else {
                progressBar.progress = progress
            }
        }
    }

}
