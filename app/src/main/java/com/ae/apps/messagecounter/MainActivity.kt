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
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.ae.apps.lib.permissions.AbstractPermissionsAwareActivity
import com.ae.apps.lib.permissions.PermissionsAwareComponent
import com.ae.apps.messagecounter.core.analytics.AppAnalytics
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.fragments.*
import com.ae.apps.messagecounter.services.CounterServiceHelper

/**
 * Main Entry point to the application
 */
class MainActivity : AbstractPermissionsAwareActivity(), PermissionsAwareComponent, AppController,
    AppRequestPermission {

    companion object {
        private const val PERMISSION_CHECK_REQUEST_CODE = 8000
        private val PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS
        )
    }

    private var previousFragment: Fragment? = null
    private var isSecondaryFragmentDisplayed = false
    private lateinit var preferenceRepository: PreferenceRepository
    private lateinit var appAnalytics: AppAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(binding.root)

        setContentView(R.layout.activity_main)

        checkPermissions()

        preferenceRepository =
            PreferenceRepository.newInstance(PreferenceManager.getDefaultSharedPreferences(this))

        appAnalytics = AppAnalytics.newInstance(baseContext)
        appAnalytics.logAppStart(resources.getString(R.string.app_name))
    }

    override fun requiredPermissions() = PERMISSIONS

    override fun invokeRequestPermissions() = reallyRequestForPermissions()

    override fun onPermissionsGranted() {
        preferenceRepository.saveRuntimePermissions(true)
        showFragmentContent(SentCountFragment.newInstance())
        setupBottomNavigation()
        //bottom_navigation.selectedItemId = R.id.action_counter
        // bottom_navigation.selectedItemId = R.id.action_list

        manageMessageCounterService()
    }

    override fun onPermissionsDenied() = showPermissionsRequiredView()

    @TargetApi(Build.VERSION_CODES.M)
    private fun reallyRequestForPermissions() =
        requestPermissions(requiredPermissions(), PERMISSION_CHECK_REQUEST_CODE)

    override fun showPermissionsRequiredView() {
        preferenceRepository.saveRuntimePermissions(false)
        showFragmentContent(NoAccessFragment.newInstance())
    }

    /*
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onBackPressed() {
        if (isSecondaryFragmentDisplayed && null != previousFragment) {
            isSecondaryFragmentDisplayed = false
            showFragmentContent(previousFragment!!)
        } else {
            super.onBackPressed()
        }
    }

     */


    fun onOptionsItemSelected(item: MenuItem?): Any {
        val id = item?.itemId

        if (id == android.R.id.home) {
            if (null != previousFragment) {
                showFragmentContent(previousFragment!!)
            }
            return true
        }
        if (id == R.id.action_about) {
            appAnalytics.logScreenView(AboutFragment::class.java.name)
            showFragmentContent(AboutFragment.newInstance(), false)
            return true
        }
        if (id == R.id.action_donate) {
            appAnalytics.logScreenView(DonationsFragment::class.java.name)
            showFragmentContent(DonationsFragment.newInstance(), false)
        }
        if (id == R.id.action_settings) {
            appAnalytics.logScreenView(SettingsFragment::class.java.name)
            Toast.makeText(baseContext, " No Settings Frag", Toast.LENGTH_SHORT).show()
            // showFragmentContent(SettingsFragment.newInstance(), false)
        }
        if (id == R.id.action_share) {
            startActivity(getShareIntent(this))
            return true
        }
        return item.let {
            if (it != null) {
                super.onOptionsItemSelected(it)
            }
        }
    }

    private fun manageMessageCounterService() {
        CounterServiceHelper.monitorMessagesInBackground(this)
    }

    override fun navigateTo(id: Int) {
        if (id == R.id.action_settings) {
            Toast.makeText(baseContext, " No Settings Frag", Toast.LENGTH_SHORT).show()
            //showFragmentContent(SettingsFragment.newInstance(), false)
        }
    }

    private fun showFragmentContent(fragment: Fragment, primaryFragment: Boolean = true) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

        if (primaryFragment) {
            isSecondaryFragmentDisplayed = false
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
            // We don't show the bottom nav for NoAccessFragment
            /*
                        if(fragment::class.java == NoAccessFragment::class.java){
                bottom_navigation.visibility = View.GONE
            } else {
                bottom_navigation.visibility = View.VISIBLE
            }
             */
            previousFragment = fragment
        } else {
            isSecondaryFragmentDisplayed = true
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            //bottom_navigation.visibility = View.GONE
        }
    }

    private fun setupBottomNavigation() {
        /*
                bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_ignore -> showFragmentContent(IgnoreNumbersFragment.newInstance())
                R.id.action_counter -> showFragmentContent(SentCountFragment.newInstance())
                R.id.action_list -> showFragmentContent(SmsBackupFragment.newInstance())
                // R.id.action_list -> showFragmentContent(ContactMessageCountFragment.newInstance())
            }
            true
        }
         */
    }
}

interface AppController {
    fun navigateTo(id: Int)
}

interface AppRequestPermission {
    fun invokeRequestPermissions()
}