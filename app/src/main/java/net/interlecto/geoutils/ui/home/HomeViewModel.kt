package net.interlecto.geoutils.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.interlecto.geoutils.project.ProjectItem
import net.interlecto.geoutils.project.ProjectRepository

class HomeViewModel(private val repository: ProjectRepository) : ViewModel() {
    private val _projects = MutableLiveData<List<ProjectItem>>()
    val projects: LiveData<List<ProjectItem>> = _projects

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            // Fetch projects from all sources and combine them
            val allProjects = mutableListOf<ProjectItem>()
            allProjects.addAll(repository.getInternalProjects())
            allProjects.addAll(repository.getDirProjects())
            allProjects.addAll(repository.getWebProjects())
            allProjects.addAll(repository.getAdProjects())

            _projects.value = allProjects
        }
    }
}