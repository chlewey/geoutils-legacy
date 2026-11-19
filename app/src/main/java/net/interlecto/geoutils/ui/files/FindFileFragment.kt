package net.interlecto.geoutils.ui.files

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
import net.interlecto.geoutils.databinding.FragmentFindFileBinding
import net.interlecto.geoutils.project.ProjectGalleryAdapter

class FindFileFragment : Fragment() {
    private lateinit var findFileViewModel: FindFileViewModel
    private lateinit var projectGalleryAdapter: ProjectGalleryAdapter
    private lateinit var fileDictionaryRepository: FileDictionaryRepository
    private lateinit var projectViewModel: ProjectViewModel

    private var _binding: FragmentFindFileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFindFileBinding.inflate(inflater, container, false)

        val database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "your_database_name"
        ).build()

        findFileViewModel = ViewModelProvider(this)[FindFileViewModel::class.java]
        fileDictionaryRepository = FileDictionaryRepository(database.fileDictionaryDao())
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]

        projectGalleryAdapter = ProjectGalleryAdapter(
            emptyList(),
            { projectItem ->
                val file = projectItem.file
                if (file.isDirectory) {
                    findFileViewModel.navigateToDirectory(file)
                } else {
                    // Handle file selection (e.g. open/preview/import)
                }
            },
            viewLifecycleOwner.lifecycleScope,
            fileDictionaryRepository,
            projectViewModel
        )

        val recyclerView = binding.projectGallery
        recyclerView.adapter = projectGalleryAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        findFileViewModel.projects.observe(viewLifecycleOwner) { projects ->
            projectGalleryAdapter = ProjectGalleryAdapter(
                projects,
                { projectItem ->
                    val file = projectItem.file
                    if (file.isDirectory) {
                        findFileViewModel.navigateToDirectory(file)
                    } else {
                        // Handle file selection (e.g. open/preview/import)
                    }
                },
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    fun onTap() {
        when (item.type) {
            ProjectItemType.FOLDER -> findFileViewModel.navigateToDirectory(item.file!!)
            ProjectItemType.PROJECT -> openProject(item)
            ProjectItemType.FILE -> askUserToConvertToProject(item)
            ProjectItemType.AD -> openAd(item.url)
            ProjectItemType.DEMO -> loadEmbeddedDemo(item.embeddedId!!)
            ProjectItemType.URL -> openInBrowser(item.url!!)
            else -> showUnsupportedMessage()
        }
    }
    */
}