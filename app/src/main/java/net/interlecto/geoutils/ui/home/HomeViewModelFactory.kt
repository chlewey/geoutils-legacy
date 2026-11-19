package net.interlecto.geoutils.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.interlecto.geoutils.project.ProjectRepository

class HomeViewModelFactory(
    private val projectRepository: ProjectRepository
) : ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(projectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}