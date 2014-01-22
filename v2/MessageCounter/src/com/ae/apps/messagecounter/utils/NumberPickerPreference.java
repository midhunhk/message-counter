package com.ae.apps.messagecounter.utils;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {

	NumberPicker numberPicker;
	Integer initialValue;
	
	public NumberPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
	}

}
