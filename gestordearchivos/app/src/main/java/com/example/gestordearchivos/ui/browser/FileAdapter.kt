package com.example.gestordearchivos.ui.browser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gestordearchivos.R
import com.example.gestordearchivos.data.model.FileItem
import com.example.gestordearchivos.data.model.FileType
import com.example.gestordearchivos.databinding.ItemFileListBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileAdapter(private val onClick: (FileItem) -> Unit) :
    ListAdapter<FileItem, FileAdapter.FileViewHolder>(FileDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FileViewHolder(
        private val binding: ItemFileListBinding,
        private val onClick: (FileItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(fileItem: FileItem) {
            binding.tvFileName.text = fileItem.name
            binding.ivFileIcon.setImageResource(getIconForFileType(fileItem.fileType))

            val details = if (fileItem.isDirectory) {
                "${fileItem.childrenCount ?: 0} elementos"
            } else {
                android.text.format.Formatter.formatShortFileSize(itemView.context, fileItem.sizeInBytes)
            }
            val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(fileItem.lastModified))
            binding.tvFileDetails.text = "$details  |  $date"

            itemView.setOnClickListener { onClick(fileItem) }
        }

        private fun getIconForFileType(type: FileType): Int {
            return when (type) {
                FileType.DIRECTORY -> R.drawable.ic_folder
                FileType.IMAGE -> R.drawable.ic_image
                FileType.VIDEO -> R.drawable.ic_video
                FileType.AUDIO -> R.drawable.ic_audio
                FileType.TEXT -> R.drawable.ic_text_file
                FileType.PDF -> R.drawable.ic_pdf
                FileType.ARCHIVE -> R.drawable.ic_archive
                FileType.APK -> R.drawable.ic_apk
                else -> R.drawable.ic_file
            }
        }
    }
}

object FileDiffCallback : DiffUtil.ItemCallback<FileItem>() {
    override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
        return oldItem == newItem
    }
}