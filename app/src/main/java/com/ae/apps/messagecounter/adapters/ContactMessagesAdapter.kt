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

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.lib.common.models.MessageInfo
import com.ae.apps.messagecounter.R
import java.util.*

class ContactMessagesAdapter(private val context: Context) :
        RecyclerView.Adapter<ContactMessagesAdapter.ViewHolder>() {

    private var mItems: List<MessageInfo> = Collections.emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<MessageInfo>) {
        mItems = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactMessagesAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_item_contact_message, parent, false))
    }

    override fun onBindViewHolder(holder: ContactMessagesAdapter.ViewHolder, position: Int) {
        // TODO
        /*
        val item = mItems[position]
        if (null == item.photo) {
            holder.userProfile.setImageResource(R.drawable.profile_icon_4)
        } else {
            holder.userProfile.setImageBitmap(item.photo)
        }
        holder.contactName.text = item.contactVo.name
        holder.messageCount.text = item.messageCount.toString()

         */
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // TODO
        /*
        val userProfile = view.userProfileImage
        val contactName = view.contactNameText
        val messageCount = view.contactMessageCountText

         */
    }
}