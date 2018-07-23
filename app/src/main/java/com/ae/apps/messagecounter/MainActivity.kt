package com.ae.apps.messagecounter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ae.apps.messagecounter.data.preferences.PreferenceRepository
import com.ae.apps.messagecounter.fragments.*
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Main Entry point to the application
 */
class MainActivity : AppCompatActivity() {

    private val PERMISSION_CHECK_REQUEST_CODE = 8000
    private var mPreviousFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissions: Array<String> = arrayOf(Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_SMS)
        checkPermissions(permissions)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
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

    private fun checkPermissions(permissions: Array<String>) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAllPermissions(permissions)) {
                onPermissionGranted()
            } else {
                requestPermissions(permissions, PERMISSION_CHECK_REQUEST_CODE)
            }
        } else {
            onPermissionGranted()
        }
    }

    private fun checkAllPermissions(permissions: Array<String>): Boolean {
        for (permissionName in permissions) {
            if (PackageManager.PERMISSION_GRANTED != PermissionChecker.checkSelfPermission(this, permissionName)) {
                return false
            }
        }
        return true
    }

    private fun onPermissionGranted() {
        showFragmentContent(SentCountFragment.newInstance(), true)
        setupBottomNavigation()
        manageMessageCounterService()
    }

    private fun manageMessageCounterService() {
        val preferenceRepository = PreferenceRepository.newInstance(
                PreferenceManager.getDefaultSharedPreferences(this))
        if (preferenceRepository.backgroundServiceEnabled()) {
            startService(getMessageCounterServiceIntent(this))
        } else {
            stopService(getMessageCounterServiceIntent(this))
        }
    }

    private fun onPermissionNotGranted(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        showFragmentContent(NoAccessFragment.newInstance(), true)
        bottom_navigation.visibility = View.INVISIBLE
    }

    private fun showFragmentContent(fragment: Fragment, primaryFragment: Boolean) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        if (primaryFragment) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
            bottom_navigation.visibility = View.VISIBLE
            mPreviousFragment = fragment
        } else {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CHECK_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted()
                } else {
                    onPermissionNotGranted(requestCode, permissions, grantResults)
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}
