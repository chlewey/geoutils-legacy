package net.interlecto.geoutils.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import net.interlecto.geoutils.ProjectViewModel
import net.interlecto.geoutils.R
import net.interlecto.geoutils.data.AppDatabase
import net.interlecto.geoutils.data.FileDictionaryRepository
import net.interlecto.geoutils.databinding.FragmentHomeBinding
import net.interlecto.geoutils.project.ProjectGalleryAdapter
import net.interlecto.geoutils.project.ProjectRepository

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var projectGalleryAdapter: ProjectGalleryAdapter
    private lateinit var fileDictionaryRepository: FileDictionaryRepository
    private lateinit var projectViewModel: ProjectViewModel

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "your_database_name"
        ).build()

        fileDictionaryRepository = FileDictionaryRepository(database.fileDictionaryDao())
        val projectRepository = ProjectRepository()
        val factory = HomeViewModelFactory(projectRepository)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]

        projectGalleryAdapter = ProjectGalleryAdapter(
            emptyList(),
            { projectItem -> /* Handle file selection */ },
            viewLifecycleOwner.lifecycleScope,
            fileDictionaryRepository,
            projectViewModel
        )

        val recyclerView = binding.projectGallery
        recyclerView.adapter = projectGalleryAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        homeViewModel.projects.observe(viewLifecycleOwner) { projects ->
            projectGalleryAdapter = ProjectGalleryAdapter(
                projects,
                { projectItem -> /* Handle file selection */ },
                viewLifecycleOwner.lifecycleScope,
                fileDictionaryRepository,
                projectViewModel
            )
            recyclerView.adapter = projectGalleryAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.findFiles.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_FindFileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}