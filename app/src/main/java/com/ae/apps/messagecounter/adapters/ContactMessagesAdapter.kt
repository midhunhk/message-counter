package com.ae.apps.messagecounter.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ae.apps.common.vo.ContactMessageVo
import com.ae.apps.messagecounter.R
import kotlinx.android.synthetic.main.list_item_contact_message.view.*
import java.util.*

class ContactMessagesAdapter(private val context: Context) :
        RecyclerView.Adapter<ContactMessagesAdapter.ViewHolder>() {

    private var mItems: List<ContactMessageVo> = Collections.emptyList()

    fun setItems(items: List<ContactMessageVo>) {
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
        val item = mItems[position]
        if (null == item.photo) {
            holder.userProfile.setImageResource(R.drawable.profile_icon_4)
        } else {
            holder.userProfile.setImageBitmap(item.photo)
        }
        holder.contactName.text = item.contactVo.name
        holder.messageCount.text = item.messageCount.toString()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userProfile = view.userProfileImage
        val contactName = view.contactNameText
        val messageCount = view.contactMessageCountText
    }
}