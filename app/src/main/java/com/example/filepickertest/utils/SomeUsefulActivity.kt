package com.flamingo.ipynbviewer.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/** Contains an [ActivityHelper], which overrides startActivityForResult.
 * Also contains a [Repository] instance for working with local files. */
abstract class SomeUsefulActivity : AppCompatActivity() {
    companion object {
        private const val WRITE_STORAGE_REQUEST_ID = 121
    }
    
    /** Intent, that is passed to result with function [updateResultIntent] */
    val resultIntent = Intent()
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityHelper.onActivityResult(requestCode, resultCode, data)
    }
    
    /** Can launch new activity, waiting the result in a special lambda. */
    val activityHelper = ActivityHelper(this)
    
    
    private var writePermissionCallback: (() -> Unit)? = null
    
    
    // https://developer.android.com/training/permissions/requesting.html
    fun requestWritePermission(onSuccess: () -> Unit) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_STORAGE_REQUEST_ID
            )
            writePermissionCallback = onSuccess
        } else {
            // Permission has already been granted
            onSuccess()
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            WRITE_STORAGE_REQUEST_ID -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    writePermissionCallback?.invoke()
                    
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                writePermissionCallback = null
                return
            }
            
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}