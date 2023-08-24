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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ae.apps.messagecounter.data.listeners.IgnoreContactListener
import com.ae.apps.messagecounter.data.models.IgnoredNumber
import com.ae.apps.messagecounter.databinding.ListItemIgnoreBinding

class IgnoredNumbersAdapter(
    private var items: List<IgnoredNumber>,
    private val listener: IgnoreContactListener
) : RecyclerView.Adapter<IgnoredNumbersAdapter.ListItemViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val binding =
            ListItemIgnoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        with(items[position]) {
            holder.bind(this)
        }
    }

    inner class ListItemViewHolder(private val binding: ListItemIgnoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ignoredNumber: IgnoredNumber) {
            binding.ignoreItemName.text = ignoredNumber.ignoreName
            binding.ignoreItemNumber.text = ignoredNumber.ignoreNumber
            binding.btnRemoveIgnoredItem.setOnClickListener {
                listener.onIgnoredContactRemoved(ignoredNumber)
            }
        }
    }

    /*
    class PaymentAdapter(private val paymentList: List<PaymentBean>) : RecyclerView.Adapter<PaymentAdapter.PaymentHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder {
        val itemBinding = RowPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PaymentHolder, position: Int) {
        val paymentBean: PaymentBean = paymentList[position]
        holder.bind(paymentBean)
    }

    override fun getItemCount(): Int = paymentList.size

    class PaymentHolder(private val itemBinding: RowPaymentBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(paymentBean: PaymentBean) {
            itemBinding.tvPaymentInvoiceNumber.text = paymentBean.invoiceNumber
            itemBinding.tvPaymentAmount.text = paymentBean.totalAmount
        }
    }
}
     */
}