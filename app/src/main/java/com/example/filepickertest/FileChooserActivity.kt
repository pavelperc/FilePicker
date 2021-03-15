package com.example.filepickertest

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamingo.ipynbviewer.utils.SomeUsefulActivity
import kotlinx.android.synthetic.main.activity_file_chooser.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class FileChooserActivity : SomeUsefulActivity() {
    companion object {
        private const val TAG = "FileChooserActivity"
    }

    val adapter by lazy { FileChooserAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_chooser)

        rvFiles.layoutManager = LinearLayoutManager(this)
        rvFiles.adapter = adapter

//        btnBack.setOnClickListener { finish() }

        btnUseSystemFileChooser.setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT)
            intent.setType("*/*")
            activityHelper.startForResult(
                android.content.Intent.createChooser(intent, "Select file")
            ) { resultCode, data ->
                if (resultCode == Activity.RESULT_OK) {
                    setResult(resultCode, data)
                    finish()
                }
            }
        }
        tvNoFilesFound.isGone = true
        pbLoading.isGone = true

        requestWritePermission {
            pbLoading.isVisible = true
            loadRecursivelyAsync()
        }
    }

    private fun loadRecursivelyAsync() {
        val root = Environment.getExternalStorageDirectory()
        val ignoreDirs = listOf(
            Environment.DIRECTORY_DCIM,
            Environment.DIRECTORY_MOVIES,
            Environment.DIRECTORY_PICTURES,
            Environment.DIRECTORY_MUSIC,
            "Android"
        ).map { root.resolve(it) }.toSet()

        thread {
            val extension = ".txt"
            val files = root.walk()
                .maxDepth(5) // root + 4 deeper
                .onEnter { !it.name.startsWith(".") && it !in ignoreDirs }
                .filter { it.name.endsWith(extension) }
                .take(200)
                .sortedBy { -it.lastModified() }

            val formatter = SimpleDateFormat.getDateTimeInstance()
            val pathToTrim = root.path + "/"

            adapter.files += files.map {
                FileItem(
                    Uri.fromFile(it),
                    it.name.substringBefore(extension),
                    formatter.format(Date(it.lastModified())),
                    it.parentFile.path.substringAfter(pathToTrim)
                )
            }
            Log.i(TAG, "Files found: " + adapter.files.size)

            runOnUiThread {
                pbLoading.isGone = true
                tvNoFilesFound.isVisible = adapter.files.isEmpty()
                adapter.notifyDataSetChanged()
            }
        }
    }
}

class FileItem(val uri: Uri, val name: String, val date: String, val path: String)