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