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
package com.ae.apps.messagecounter.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ae.apps.messagecounter.R
import com.ae.apps.messagecounter.data.listeners.IgnoreContactListener
import com.ae.apps.messagecounter.data.models.IgnoredNumber
import kotlinx.android.synthetic.main.list_item_ignore.view.*

class IgnoredNumbersAdapter(var mItems: List<IgnoredNumber>,
                            private val context: Context,
                            private val listener: IgnoreContactListener)
    : RecyclerView.Adapter<IgnoredNumbersAdapter.ViewHolder>() {

    fun setItems(items: List<IgnoredNumber>) {
        mItems = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.list_item_ignore, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ignoredNumber = mItems[position]
        holder.ignoredName.text = ignoredNumber.ignoreName
        holder.ignoredNumber.text = ignoredNumber.ignoreNumber
        holder.deleteIgnoredNumber.setOnClickListener {
            listener.onIgnoredContactRemoved(ignoredNumber)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ignoredNumber = view.ignoreItemNumber!!
        val ignoredName = view.ignoreItemName!!
        val deleteIgnoredNumber = view.btnRemoveIgnoredItem!!
    }
}