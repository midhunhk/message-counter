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


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ae.apps.messagecounter.adapters.IgnoredNumbersAdapter
import com.ae.apps.messagecounter.data.AppDatabase
import com.ae.apps.messagecounter.data.listeners.IgnoreContactListener
import com.ae.apps.messagecounter.data.models.IgnoredNumber
import com.ae.apps.messagecounter.data.repositories.IgnoredNumbersRepository
import com.ae.apps.messagecounter.data.viewmodels.IgnoredNumbersViewModel
import com.ae.apps.messagecounter.data.viewmodels.IgnoredNumbersViewModelFactory
import com.ae.apps.messagecounter.databinding.FragmentIgnoreNumbersBinding
import java.util.*

class IgnoreNumbersFragment : Fragment(), IgnoreContactListener {

    companion object {
        fun newInstance(): IgnoreNumbersFragment {
            return IgnoreNumbersFragment()
        }
    }

    private lateinit var viewModel: IgnoredNumbersViewModel
    private lateinit var adapter: IgnoredNumbersAdapter
    private lateinit var binding: FragmentIgnoreNumbersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIgnoreNumbersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        //viewModel.getIgnoredNumbers().removeObservers(this)
    }

    private fun initViewModel() {
        val repository = IgnoredNumbersRepository.getInstance(
            AppDatabase
                .getInstance(requireContext()).ignoredNumbersDao()
        )
        with(this){

        }
        val factory = IgnoredNumbersViewModelFactory(repository)

        // viewModel = ViewModelProviders.of(this, factory).get(IgnoredNumbersViewModel::class.java)
        adapter = IgnoredNumbersAdapter(Collections.emptyList(),this)
    }

    private fun initUI() {
        binding.btnShowIgnoreDialog.setOnClickListener { showSelectIgnoreContactDialog() }

        val recyclerView = binding.list
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setEmptyView(binding.emptyView)

        // TODO
        /*
        viewModel.getIgnoredNumbers()
                .observe(this, Observer { items ->
                    run {
                        adapter.setItems(items!!)
                    }
                })
         */
    }

    private fun showSelectIgnoreContactDialog() {
        val dialogFragment = IgnoreDialogFragment.newInstance()
        //dialogFragment.setTargetFragment(this@IgnoreNumbersFragment, 300)
        //dialogFragment.show(fragmentManager, "fragment_ignore_contact")
    }

    override fun onContactSelected(ignoredContact: IgnoredNumber) {
        viewModel.ignoreNumber(ignoredContact)
        //adapter.notifyDataSetChanged()
    }

    override fun onIgnoredContactRemoved(ignoredContact: IgnoredNumber) {
        viewModel.unIgnoreNumber(ignoredContact)
        //adapter.notifyDataSetChanged()
    }
}
