/*
 * Copyright 2015 Midhun Harikumar
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

package com.ae.apps.messagecounter.vo;

/**
 * Represents a Navigation Drawer item
 * 
 * @author MidhunHk
 *
 */
public class NavDrawerItem {

	private int		imageRes;
	private int		itemId;
	private String	title;

	/**
	 * Create a Nav Drawer Item
	 * 
	 * @param imageRes
	 * @param titleRes
	 */
	public NavDrawerItem(int imageRes, String title) {
		this.imageRes = imageRes;
		this.title = title;
	}
	
	public NavDrawerItem(int imageRes, String title, int itemId) {
		this.imageRes = imageRes;
		this.title = title;
		this.itemId = itemId;
	}

	public int getImageRes() {
		return imageRes;
	}

	public void setImageRes(int imageRes) {
		this.imageRes = imageRes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

}
