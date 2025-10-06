package com.example.gestordearchivos.data.repository

import com.example.gestordearchivos.data.model.FileItem
import com.example.gestordearchivos.data.model.toFileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileRepository {

    suspend fun getFiles(path: String): Result<List<FileItem>> = withContext(Dispatchers.IO) {
        try {
            val directory = File(path)
            if (!directory.canRead()) {
                return@withContext Result.failure(Exception("Acceso denegado a $path"))
            }
            val files = directory.listFiles()?.map { it.toFileItem() }
            val sortedFiles = files?.sortedWith(
                compareBy({ !it.isDirectory }, { it.name.lowercase() })
            ) ?: emptyList()
            Result.success(sortedFiles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createDirectory(parentPath: String, dirName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val newDir = File(parentPath, dirName)
            if (newDir.exists() || !newDir.mkdir()) {
                throw Exception("No se pudo crear la carpeta")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Aquí irían las funciones para renombrar, copiar, mover y eliminar
    // suspend fun deleteFile(path: String): Result<Unit> { ... }
    // suspend fun renameFile(path: String, newName: String): Result<Unit> { ... }
}