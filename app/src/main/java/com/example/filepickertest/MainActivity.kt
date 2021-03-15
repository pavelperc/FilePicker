package com.example.filepickertest

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.example.filepickertest.utils.longToast
import com.example.filepickertest.utils.toast
import com.flamingo.ipynbviewer.utils.SomeUsefulActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : SomeUsefulActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSelectFile.setOnClickListener {
            tvOpenedFile.text = ""
            activityHelper.startForResult(
                Intent(this, FileChooserActivity::class.java)
            ) { resultCode, data ->
                val uri = data?.data
                if (resultCode == Activity.RESULT_OK && uri != null) {
                    contentResolver.openInputStream(uri)!!.bufferedReader().use {
                        tvOpenedFile.text = it.readText()
                    }
                }
            }
        }

        btnSave1.setOnClickListener {
            requestWritePermission {
                val downloadsFolder =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                if (downloadsFolder == null || !downloadsFolder.canWrite()) {
                    toast("Can not write to downloads folder")
                    return@requestWritePermission
                }
                val file = downloadsFolder.resolve("file1.txt")
                file.writeText("Hello from file1.")
                longToast("Saved file to $file")
            }
        }
        btnSave2.setOnClickListener {
            requestWritePermission {
                val rootFolder = Environment.getExternalStorageDirectory()

                if (rootFolder == null || !rootFolder.canWrite()) {
                    toast("Can not write to root folder")
                    return@requestWritePermission
                }
                val file = rootFolder.resolve("file2.txt")
                file.writeText("Hello from file2.")
                longToast("Saved file to $file")
            }
        }
        btnDelete.setOnClickListener {
            requestWritePermission {
                val downloadsFolder =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val rootFolder = Environment.getExternalStorageDirectory()


                val deleted1 = downloadsFolder?.resolve("file1.txt")?.delete() ?: false
                val deleted2 = rootFolder?.resolve("file2.txt")?.delete() ?: false

                longToast("Deleted file1: $deleted1, file2: $deleted2")
            }
        }
    }
}