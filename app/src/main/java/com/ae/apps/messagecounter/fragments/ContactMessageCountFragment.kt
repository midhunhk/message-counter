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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ae.apps.common.views.EmptyRecyclerView
import com.ae.apps.common.vo.ContactMessageVo
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.adapters.ContactMessagesAdapter
import com.ae.apps.messagecounter.data.viewmodels.ContactMessageViewModel
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_contact_message_count.*

/**
 * A simple [Fragment] subclass.
 *
 */
class ContactMessageCountFragment : Fragment() {

    companion object {
        fun newInstance() = ContactMessageCountFragment()
    }

    private lateinit var mViewModel: ContactMessageViewModel
    private lateinit var mAdapter: ContactMessagesAdapter
    private lateinit var mSentMessagesCount: List<ContactMessageVo>
    private lateinit var mReceivedMessagesCount: List<ContactMessageVo>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact_message_count, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.getReceivedMessageContacts().removeObservers(this)
        mViewModel.getSentMessageContacts().removeObservers(this)
        clearFindViewByIdCache()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ContactMessageViewModel::class.java)
        mAdapter = ContactMessagesAdapter(requireContext())
    }

    private fun initUI() {
        val recyclerView = list as EmptyRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = mAdapter
        recyclerView.setEmptyView(empty)

        mViewModel.getReceivedMessageContacts().observe(this, Observer {
            run {
                mReceivedMessagesCount = it!!
                btnReceivedList.isEnabled = true
            }
        })

        mViewModel.getSentMessageContacts().observe(this, Observer {
            run {
                mSentMessagesCount = it!!
                updateAdapter(mSentMessagesCount)
                btnSentList.isEnabled = true
            }
        })

        btnSentList.isEnabled = false
        btnReceivedList.isEnabled = false

        btnSentList.setOnClickListener { updateAdapter(mSentMessagesCount) }
        btnReceivedList.setOnClickListener { updateAdapter(mReceivedMessagesCount) }

        // Start to read the data
        mViewModel.getContactMessageData(requireContext())
    }

    private fun updateAdapter(list: List<ContactMessageVo>) {
        if (list.isEmpty()) {
            emptyListText.setText(R.string.empty_list)
        } else {
            mAdapter.setItems(list)
        }
    }
}
