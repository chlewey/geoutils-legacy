package net.interlecto.geoutils

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProjectViewModel : ViewModel() {

    private val _workingDir = MutableLiveData<String>()
    val workingDir: LiveData<String> = _workingDir

    private val _projectUri = MutableLiveData<Uri>()
    val projectUri: LiveData<Uri> = _projectUri

    fun openProject(workingDir: String, projectUri: Uri) {
        _workingDir.value = workingDir
        _projectUri.value = projectUri
    }

    fun setWorkingDir(dir: String) {
        _workingDir.value = dir
    }

    fun setProjectUri(uri: Uri) {
        _projectUri.value = uri
    }
}