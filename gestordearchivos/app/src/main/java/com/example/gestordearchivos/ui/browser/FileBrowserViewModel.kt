package com.example.gestordearchivos.ui.browser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestordearchivos.data.model.FileItem
import com.example.gestordearchivos.data.repository.FileRepository
import kotlinx.coroutines.launch

class FileBrowserViewModel : ViewModel() {

    private val repository = FileRepository()

    private val _files = MutableLiveData<List<FileItem>>()
    val files: LiveData<List<FileItem>> = _files

    private val _currentPath = MutableLiveData<String>()
    val currentPath: LiveData<String> = _currentPath

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadFiles(path: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _currentPath.value = path
            val result = repository.getFiles(path)
            result.onSuccess {
                _files.value = it
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun onErrorShown() {
        _error.value = null
    }
}