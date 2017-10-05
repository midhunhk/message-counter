package com.ae.apps.messagecounter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.data.IgnoreContactListener;
import com.ae.apps.messagecounter.models.IgnoredContact;

import java.util.List;

/**
 * Adapter for showing the list of Ignored Numbers
 */
public class IgnoreListRecyclerViewAdapter extends RecyclerView.Adapter<IgnoreListRecyclerViewAdapter.ViewHolder> {

    private final List<IgnoredContact> mValues;
    private IgnoreContactListener mIgnoreContactListener;

    public IgnoreListRecyclerViewAdapter(List<IgnoredContact> items, IgnoreContactListener listener) {
        mValues = items;
        mIgnoreContactListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ignore_list_item, parent, false);
        view.setClickable(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final IgnoredContact ignoredContact = mValues.get(position);
        holder.mItem = ignoredContact;

        holder.mName.setText(ignoredContact.getName());
        holder.mNumber.setText(ignoredContact.getNumber());

        holder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIgnoreContactListener.onIgnoredContactRemoved(ignoredContact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mName;
        final TextView mNumber;
        final ImageButton mBtnDelete;
        IgnoredContact mItem;

        ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            mView = view;
            mName = (TextView) view.findViewById(R.id.ignoreItemName);
            mNumber = (TextView) view.findViewById(R.id.ignoreItemNumber);
            mBtnDelete = (ImageButton) view.findViewById(R.id.btnRemoveIgnoredItem);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNumber.getText() + "'";
        }
    }
}
