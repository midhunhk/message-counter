package com.ae.apps.messagecounter

import android.Manifest
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.app.AppCompatDelegate.MODE_NIGHT_AUTO
import com.ae.apps.common.permissions.PermissionsAwareComponent
import com.ae.apps.common.permissions.RuntimePermissionChecker
import com.ae.apps.messagecounter.smsbackup.fragments.NoAccessFragment
import com.ae.apps.messagecounter.smsbackup.fragments.SmsBackupFragment

class MainActivity : AppCompatActivity(), PermissionsAwareComponent, AppRequestPermission {

    companion object {
        private const val PERMISSION_CHECK_REQUEST_CODE = 8001
        private val PERMISSIONS: Array<String> = arrayOf(Manifest.permission.READ_SMS)
    }

    private lateinit var permissionChecker: RuntimePermissionChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Experimental DarkMode
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_AUTO)

        setContentView(R.layout.activity_main)

        permissionChecker = RuntimePermissionChecker(this)
        permissionChecker.checkPermissions()
    }

    override fun onPermissionsDenied() {
        showFragmentContent(NoAccessFragment.newInstance())
    }

    override fun requestForPermissions() {
        showFragmentContent(NoAccessFragment.newInstance())
    }

    override fun onPermissionsGranted() {
        showFragmentContent(SmsBackupFragment.newInstance())
    }

    override fun requiredPermissions(): Array<String> {
        return PERMISSIONS
    }

    private fun showFragmentContent(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss()
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun invokeRequestPermissions() {
        requestPermissions(requiredPermissions(), PERMISSION_CHECK_REQUEST_CODE)
    }
}

interface AppRequestPermission {
    fun invokeRequestPermissions()
}