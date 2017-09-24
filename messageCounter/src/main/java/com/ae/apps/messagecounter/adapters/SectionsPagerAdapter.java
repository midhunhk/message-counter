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

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ae.apps.messagecounter.R;
import com.ae.apps.messagecounter.fragments.IgnoreFragment;
import com.ae.apps.messagecounter.fragments.MessageChartFragment;
import com.ae.apps.messagecounter.fragments.MessageListFragment;
import com.ae.apps.messagecounter.fragments.SentCountFragment;

/**
 * The Pager Adapter manages the fragments loaded into the main content area
 *
 * @author midhun
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final Context context;

    // The Fragments are lazily initialized and cached by the adapter
    private Fragment mListFragment;
    private Fragment mChartFragment;
    private Fragment mIgnoreFragment;
    private Fragment mSentCountFragment;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i) {
            case R.id.menu_counter:
                if (null == mSentCountFragment) {
                    mSentCountFragment = new SentCountFragment();
                }
                fragment = mSentCountFragment;
                break;
            case R.id.menu_ignore:
                if (null == mChartFragment) {
                    mIgnoreFragment = new IgnoreFragment();
                }
                fragment = mIgnoreFragment;
                break;
            case R.id.menu_list:
                if (null == mListFragment) {
                    mListFragment = new MessageListFragment();
                }
                fragment = mListFragment;
                break;
            case R.id.menu_chart:
                if (null == mChartFragment) {
                    mChartFragment = new MessageChartFragment();
                }
                fragment = mChartFragment;
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public String getPageTitle(int itemId) {
        switch (itemId) {
            case R.id.menu_ignore:
                return context.getString(R.string.title_ignore);
            case R.id.menu_list:
                return context.getString(R.string.title_sent_list);
            case R.id.menu_chart:
                return context.getString(R.string.title_chart);
            case R.id.menu_counter:
            default:
                return context.getString(R.string.title_counter);
        }
    }

}
