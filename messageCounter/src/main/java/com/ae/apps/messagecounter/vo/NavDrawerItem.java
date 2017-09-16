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

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Represents a Navigation Drawer item
 *
 * @author MidhunHk
 */
public class NavDrawerItem {

    @DrawableRes
    private int imageRes;

    @StringRes
    private int titleRes;

    private long itemId;

    /**
     * Create a Nav Drawer Item
     *
     * @param imageRes Image resource for the menu item
     * @param titleRes    Title for the menu item
     * @param itemId   Item id for the menu
     */
    public NavDrawerItem(@DrawableRes int imageRes, @StringRes int titleRes, long itemId) {
        this.imageRes = imageRes;
        this.titleRes = titleRes;
        this.itemId = itemId;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public void setTitleRes(int titleRes) {
        this.titleRes = titleRes;
    }
}
