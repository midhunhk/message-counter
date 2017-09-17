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


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.models.IgnoredContact;

/**
 * Dialog to select a contact or enter information to ignore for message counter
 */
public class IgnoreDialogFragment extends AppCompatDialogFragment {

    private final int CONTACT_PICKER_RESULT = 1001;

    public IgnoreDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment
     *
     * @return A new instance of fragment IgnoreDialogFragment.
     */
    public static IgnoreDialogFragment newInstance() {
        return new IgnoreDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ignore_dialog, container, false);

        setupViews(view);

        return view;
    }

    private void setupViews(View view) {
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnIgnore = (Button) view.findViewById(R.id.btnIgnoreContact);
        btnIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IgnoredContact ignoredContact = validateIgnoredContact();

                    updateWithSelectedContact(ignoredContact);

                    dismiss();
                } catch (IgnoreDialogValidationException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private IgnoredContact validateIgnoredContact() throws IgnoreDialogValidationException {
        // Do Validation
        IgnoredContact ignoredContact = new IgnoredContact();

        ignoredContact.setName("Test Name");
        ignoredContact.setNumber("234546567");

        return ignoredContact;
    }

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICKER_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == CONTACT_PICKER_RESULT) {
            // Handle Contact pick
            Uri result = data.getData();
            String contactId = result.getLastPathSegment();
        }
    }

    private void updateWithSelectedContact(IgnoredContact ignoredContact) {
        if (getTargetFragment() instanceof IgnoreDialogListener) {
            ((IgnoreDialogListener) getTargetFragment()).onContactSelected(ignoredContact);
        } else {
            throw new RuntimeException("must implement the interface IgnoreDialogListener");
        }
    }

    /**
     * Validation class for IgnoreDialog
     */
    class IgnoreDialogValidationException extends Throwable {
        IgnoreDialogValidationException(String message) {
            super(message);
        }
    }

    /**
     * Activity/Fragment that invokes this Dialog must implement this interface
     * to get callback when data is ready
     */
    interface IgnoreDialogListener {
        /**
         * Invoked when a contact has been selected for blocking
         *
         * @param ignoredContact ignored contact
         */
        void onContactSelected(IgnoredContact ignoredContact);
    }
}
