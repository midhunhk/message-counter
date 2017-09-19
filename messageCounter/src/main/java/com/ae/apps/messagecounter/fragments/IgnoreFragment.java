/*
 * Copyright 2017 Midhun Harikumar
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
package com.ae.apps.messagecounter.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ae.apps.common.views.EmptyRecyclerView;
import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.adapters.IgnoreListRecyclerViewAdapter;
import com.ae.apps.messagecounter.models.IgnoredContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manage Ignore lists
 */
public class IgnoreFragment extends Fragment implements IgnoreDialogFragment.IgnoreDialogListener {

    public IgnoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_ignore, container, false);

        setupViews(layout);

        return layout;
    }

    private void setupViews(View layout) {
        View btnShowIgnoreDialog = layout.findViewById(R.id.btnShowIgnoreDialog);
        btnShowIgnoreDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectIgnoreContactDialog();
            }
        });

        EmptyRecyclerView recyclerView = (EmptyRecyclerView) layout.findViewById(R.id.list);
        View emptyView = layout.findViewById(R.id.empty_view);

        List<IgnoredContact> list = new ArrayList<>();

        IgnoredContact ic = new IgnoredContact();
        ic.setName("Test");
        ic.setNumber("(987654321)");
        list.add(ic);
        IgnoreListRecyclerViewAdapter recyclerViewAdapter = new IgnoreListRecyclerViewAdapter(list);

        if (null != recyclerView) {
            Context context = layout.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.setEmptyView(emptyView);
        }
    }

    private void showSelectIgnoreContactDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        IgnoreDialogFragment dialogFragment = IgnoreDialogFragment.newInstance();
        dialogFragment.setTargetFragment(IgnoreFragment.this, 300);
        dialogFragment.show(fragmentManager, "fragment_ignore_contact");
    }

    @Override
    public void onContactSelected(IgnoredContact ignoredContact) {
        Toast.makeText(getActivity(), ignoredContact.toString(), Toast.LENGTH_SHORT).show();
    }
}
