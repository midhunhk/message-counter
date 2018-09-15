/*
 * Copyright 2018 Midhun Harikumar
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
package com.ae.apps.messagecounter

import android.Manifest
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ae.apps.common.permissions.PermissionsAwareComponent
import com.ae.apps.common.permissions.RuntimePermissionChecker
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.fragments.*
import com.ae.apps.messagecounter.services.CounterServiceHelper
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main Entry point to the application
 */
class MainActivity : AppCompatActivity(), PermissionsAwareComponent, AppController, AppRequestPermission {

    companion object {
        private const val PERMISSION_CHECK_REQUEST_CODE = 8000
        private val PERMISSIONS: Array<String> = arrayOf(Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS)
    }

    private var mPreviousFragment: Fragment? = null
    private var mSecondaryFragmentDisplayed = false
    private lateinit var mPermissionChecker: RuntimePermissionChecker
    private lateinit var mPreferenceRepository: PreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPreferenceRepository = PreferenceRepository.newInstance(PreferenceManager.getDefaultSharedPreferences(this))

        mPermissionChecker = RuntimePermissionChecker(this)
        mPermissionChecker.checkPermissions()
    }

    override fun requiredPermissions() = PERMISSIONS

    override fun requestForPermissions()  = showPermissionsRequiredView()

    override fun invokeRequestPermissions() = reallyRequestForPermissions()

    override fun onPermissionsGranted() {
        mPreferenceRepository.saveRuntimePermissions(true)
        showFragmentContent(SentCountFragment.newInstance(), true)
        setupBottomNavigation()
        bottom_navigation.selectedItemId = R.id.action_counter
        manageMessageCounterService()
    }

    override fun onPermissionsDenied() = showPermissionsRequiredView()

    @TargetApi(Build.VERSION_CODES.M)
    private fun reallyRequestForPermissions() = requestPermissions(requiredPermissions(), PERMISSION_CHECK_REQUEST_CODE)

    private fun showPermissionsRequiredView() {
        mPreferenceRepository.saveRuntimePermissions(false)
        showFragmentContent(NoAccessFragment.newInstance(), true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CHECK_REQUEST_CODE -> {
                mPermissionChecker.handlePermissionsResult(permissions, grantResults)
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onBackPressed() {
        if (mSecondaryFragmentDisplayed && null != mPreviousFragment) {
            mSecondaryFragmentDisplayed = false
            showFragmentContent(mPreviousFragment!!, true)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        if (id == android.R.id.home) {
            if (null != mPreviousFragment) {
                showFragmentContent(mPreviousFragment!!, true)
            }
            return true
        }
        if (id == R.id.action_about) {
            showFragmentContent(AboutFragment.newInstance(), false)
            return true
        }
        if (id == R.id.action_donate) {
            showFragmentContent(DonationsFragment.newInstance(), false)
        }
        if (id == R.id.action_settings) {
            showFragmentContent(SettingsFragment.newInstance(), false)
        }
        if (id == R.id.action_share) {
            startActivity(getShareIntent(this))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun manageMessageCounterService() {
        CounterServiceHelper.monitorMessagesInBackground(this)
    }

    override fun navigateTo(id:Int){
        if (id == R.id.action_settings) {
            showFragmentContent(SettingsFragment.newInstance(), false)
        }
    }

    private fun showFragmentContent(fragment: Fragment, primaryFragment: Boolean) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss()
        if (primaryFragment) {
            mSecondaryFragmentDisplayed = false
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
            // We don't show the bottom nav for NoAccessFragment
            if(fragment::class.java == NoAccessFragment::class.java){
                bottom_navigation.visibility = View.GONE
            } else {
                bottom_navigation.visibility = View.VISIBLE
            }
            mPreviousFragment = fragment
        } else {
            mSecondaryFragmentDisplayed = true
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            bottom_navigation.visibility = View.GONE
        }
    }

    private fun setupBottomNavigation() {
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when {
                item.itemId == R.id.action_ignore -> showFragmentContent(IgnoreNumbersFragment.newInstance(), true)
                item.itemId == R.id.action_counter -> showFragmentContent(SentCountFragment.newInstance(), true)
                item.itemId == R.id.action_list -> showFragmentContent(ContactMessageCountFragment.newInstance(), true)
            }
            true
        }
    }
}

interface AppController {
    fun navigateTo(id:Int)
}
interface AppRequestPermission {
    fun invokeRequestPermissions()
}