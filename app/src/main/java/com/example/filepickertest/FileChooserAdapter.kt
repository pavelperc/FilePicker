package com.example.filepickertest

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class FileChooserAdapter(
    private val activity: FileChooserActivity
) :
    RecyclerView.Adapter<FileChooserAdapter.MyViewHolder>() {
    
    val files = mutableListOf<FileItem>()
    
    inner class MyViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
        val tvName = root.findViewById<TextView>(R.id.tvName)
        val tvDate = root.findViewById<TextView>(R.id.tvDate)
        val tvFolder = root.findViewById<TextView>(R.id.tvFolder)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_chooser_item, parent, false)
        return MyViewHolder(root)
    }
    
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val file = files[position]
        holder.tvName.text = file.name
        holder.tvDate.text = file.date
        holder.tvFolder.text = file.path
        
        holder.root.setOnClickListener {
            val intent = Intent()
            intent.data = files[holder.adapterPosition].uri
            activity.setResult(Activity.RESULT_OK, intent)
            activity.finish()
        }
    }
    
    override fun getItemCount() = files.size
}