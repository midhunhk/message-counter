/*
 * Copyright 2014 Midhun Harikumar
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

package com.ae.apps.messagecounter.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.vo.NavDrawerItem;

import java.util.List;

/**
 * Adapter for the Navigation Drawer. 
 * Handles the display of the Navigation drawer 
 * 
 * @author MidhunHK
 *
 */
public class NavDrawerListAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<NavDrawerItem> navDrawerItems;
	
	public NavDrawerListAdapter(Context context, List<NavDrawerItem> navItems){
		mContext = context;
		navDrawerItems = navItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int pos) {
		return navDrawerItems.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		if(null == convertView){
			// Create the view first time
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.drawer_list_item, null);
		}
		
		// Find the elements and set the values
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		TextView title = (TextView) convertView.findViewById(R.id.title);
		
		// Find the model object for this position
		NavDrawerItem drawerItem = (NavDrawerItem) getItem(pos);
		
		icon.setImageResource(drawerItem.getImageRes());
		title.setText(drawerItem.getTitle());
		
		return convertView;
	}

}
