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
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ae.apps.lib.common.utils.CommonUtils
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

/**
 * Fragment that represents the SentCount state
 *
 */
class SentCountFragment : Fragment(R.layout.fragment_sent_count) {

    companion object {
        fun newInstance() = SentCountFragment()
        private const val PROGRESS_ANIMATION_DURATION = 800L
        // private const val PROGRESS_ANIMATION_DELAY = 400L
        private const val PROGRESS_PROPERTY_NAME = "progress"
    }

    private lateinit var viewModel: CounterViewModel
    private lateinit var appController: AppController
    private lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appController = activity as AppController
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // viewModel.getSentCountDetails().removeObservers(this)
    }

    private fun initViewModel() {
        preferenceRepository = PreferenceRepository.newInstance(PreferenceManager.getDefaultSharedPreferences(requireContext()))
        val counterRepository = CounterRepository.getInstance(AppDatabase.getInstance(requireContext()).counterDao())
        val ignoreNumbersRepository = IgnoredNumbersRepository.getInstance(AppDatabase.getInstance(requireContext()).ignoredNumbersDao())
        val factory = CounterViewModelFactory(counterRepository, ignoreNumbersRepository, preferenceRepository)
        // FIXME
        // viewModel = ViewModelProviders.of(requireActivity(), factory).get(CounterViewModel::class.java)
    }

    private fun initUI() {
        // TODO
        /*
        viewModel.getSentCountDetails().observe(this,
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
         */

        // Fetch the data that is already in the database
        viewModel.readSentCountDataFromRepository()

        // Index messages that were sent before the app was installed
        // Or sent during the time a background service was not running
        viewModel.indexMessages(requireContext())

        manageInfoCard()
    }

    private fun manageInfoCard() {
        if (showInfoCard()) {
            /*
            info_card.visibility = View.VISIBLE
            info_card.setOnClickListener {
                if (reviewSettingsText.visibility == View.VISIBLE) {
                    appController.navigateTo(R.id.action_settings)
                    preferenceRepository.setSettingsHintReviewed()
                }
                info_card.visibility = View.GONE
            }

             */
        }
    }

    private fun showInfoCard(): Boolean {
        if (CommonUtils.isFirstInstall(requireContext())  && !preferenceRepository.getSettingsHintReviewed()) {
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
