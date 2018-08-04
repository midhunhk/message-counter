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
class MainActivity : AppCompatActivity(), PermissionsAwareComponent {

    companion object {
        private const val PERMISSION_CHECK_REQUEST_CODE = 8000
    }

    private var mPreviousFragment: Fragment? = null
    private var mSecondaryFragmentDisplayed = false
    private lateinit var mPermissionChecker: RuntimePermissionChecker
    private val permissions: Array<String> = arrayOf(Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS)
    private lateinit var mPreferenceRepository: PreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPreferenceRepository = PreferenceRepository.newInstance(PreferenceManager.getDefaultSharedPreferences(this))

        mPermissionChecker = RuntimePermissionChecker(this)
        mPermissionChecker.checkPermissions()

    }

    override fun requiredPermissions() = permissions

    @TargetApi(Build.VERSION_CODES.M)
    override fun requestForPermissions() {
        requestPermissions(permissions, PERMISSION_CHECK_REQUEST_CODE)
    }

    override fun onPermissionsGranted() {
        mPreferenceRepository.saveRuntimePermissions(true)
        showFragmentContent(SentCountFragment.newInstance(), true)
        setupBottomNavigation()
        manageMessageCounterService()
    }

    override fun onPermissionsDenied() {
        mPreferenceRepository.saveRuntimePermissions(false)
        showFragmentContent(NoAccessFragment.newInstance(), true)
        bottom_navigation.visibility = View.INVISIBLE
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

    private fun showFragmentContent(fragment: Fragment, primaryFragment: Boolean) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        if (primaryFragment) {
            mSecondaryFragmentDisplayed = false
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
            bottom_navigation.visibility = View.VISIBLE
            mPreviousFragment = fragment
        } else {
            mSecondaryFragmentDisplayed = true
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            bottom_navigation.visibility = View.INVISIBLE
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
