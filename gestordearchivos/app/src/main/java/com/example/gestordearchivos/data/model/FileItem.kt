package com.example.gestordearchivos.data.model

import java.io.File

data class FileItem(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val sizeInBytes: Long,
    val lastModified: Long,
    val fileType: FileType,
    val childrenCount: Int? = null // Para directorios
)

enum class FileType {
    DIRECTORY, IMAGE, VIDEO, AUDIO, TEXT, PDF, ARCHIVE, APK, GENERIC
}

fun File.toFileItem(): FileItem {
    val fileType = when {
        isDirectory -> FileType.DIRECTORY
        extension.matches(Regex("jpg|jpeg|png|gif|bmp|webp")) -> FileType.IMAGE
        extension.matches(Regex("mp4|mkv|3gp|webm")) -> FileType.VIDEO
        extension.matches(Regex("mp3|wav|ogg|m4a")) -> FileType.AUDIO
        extension.matches(Regex("txt|md|log|json|xml|html")) -> FileType.TEXT
        extension.equals("pdf", ignoreCase = true) -> FileType.PDF
        extension.matches(Regex("zip|rar|7z|tar|gz")) -> FileType.ARCHIVE
        extension.equals("apk", ignoreCase = true) -> FileType.APK
        else -> FileType.GENERIC
    }
    return FileItem(
        path = this.absolutePath,
        name = this.name,
        isDirectory = this.isDirectory,
        sizeInBytes = this.length(),
        lastModified = this.lastModified(),
        fileType = fileType,
        childrenCount = if (this.isDirectory) this.listFiles()?.size else null
    )
}