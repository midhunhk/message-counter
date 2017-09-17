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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.models.IgnoredContact;

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
                Toast.makeText(getActivity(), "Show dialog to add a contact", Toast.LENGTH_SHORT).show();
                showSelectIgnoreContactDialog();
            }
        });
    }

    private void showSelectIgnoreContactDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        IgnoreDialogFragment dialogFragment = IgnoreDialogFragment.newInstance();
        dialogFragment.setTargetFragment(IgnoreFragment.this, 300);
        dialogFragment.show(fragmentManager, "fragment_ignore_contact");
    }

    @Override
    public void onContactSelected(IgnoredContact ignoredContact) {

    }
}
