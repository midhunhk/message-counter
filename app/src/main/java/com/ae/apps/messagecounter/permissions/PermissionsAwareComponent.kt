package com.ae.apps.messagecounter.permissions

/**
 *
 */
interface RuntimePermissionsAwareComponent {

    fun requiredPermissions():Array<String>

    fun requestForPermissions()

    fun onPermissionsGranted()

    fun onPermissionsDenied()
}