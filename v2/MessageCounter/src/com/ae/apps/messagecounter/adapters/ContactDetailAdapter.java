/*
 * Copyright 2013 Midhun Harikumar
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

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.ae.apps.messagecounter.activities.R;
import com.ae.apps.messagecounter.vo.ContactMessageVo;

/**
 * Custom Adapter for showing the contact details
 * 
 * @author Midhun
 * 
 */
public class ContactDetailAdapter extends BaseAdapter {

	private Context					context;
	private List<ContactMessageVo>	list;

	public ContactDetailAdapter(Context context, List<ContactMessageVo> objects) {
		this.context = context;
		this.list = objects;
	}

	/**
	 * Update the list
	 * 
	 * @param objects
	 */
	public void updateList(List<ContactMessageVo> objects) {
		this.list = objects;
		notifyDataSetInvalidated();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.contact_detail_row, null);
			holder = new ViewHolder();
			// holder.photo = (ImageView) convertView.findViewById(R.id.contactImage);
			holder.badge = (QuickContactBadge) convertView.findViewById(R.id.badge_medium);
			holder.contactName = (TextView) convertView.findViewById(R.id.contactNameText);
			holder.timesContacted = (TextView) convertView.findViewById(R.id.contactMessageCountText);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String contactId = "";
		ContactMessageVo contactMessageVo = (ContactMessageVo) getItem(position);
		if (contactMessageVo != null && contactMessageVo.getContactVo() != null) {
			contactId = contactMessageVo.getContactVo().getId();
			String name = contactMessageVo.getContactVo().getName();
			if (name == null) {
				holder.contactName.setText("-noname-");
			} else {
				holder.contactName.setText(name);
			}
			String count = String.valueOf(contactMessageVo.getMessageCount());
			if (count == null) {
				holder.timesContacted.setText("0");
			} else {
				holder.timesContacted.setText(count);
			}

			try {
				// Set an image for the contact picture from the contact
				Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
				holder.badge.assignContactUri(contactUri);
				Bitmap bitmap = contactMessageVo.getPhoto();
				if (bitmap != null) {
					holder.badge.setImageBitmap(bitmap);
				} else {
					// If not able to get the profile icon, show a default one
					holder.badge.setImageResource(R.drawable.default_profile_image);
				}

			} catch (Exception e) {
				Log.e("ContactDetailAdapter", e.getMessage());
			}
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		QuickContactBadge	badge;
		TextView			contactName;
		TextView			timesContacted;
	}

}