package net.interlecto.geoutils

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import net.interlecto.geoutils.gup.GupFileManager
import java.io.File
import java.nio.file.Files

class ProjectFragment : Fragment() {

    private lateinit var _projectViewModel: ProjectViewModel
    private val projectViewModel: ProjectViewModel get() = _projectViewModel
    private val workingDir: String get() = projectViewModel.workingDir.value?: ""
    private val projectUri: Uri get() = projectViewModel.projectUri.value?: Uri.EMPTY
    private lateinit var gupFileManager: GupFileManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]

        projectViewModel.projectUri.observe(viewLifecycleOwner) { projectFileUri ->
            if (projectViewModel.projectUri.value == null) {
                projectViewModel.workingDir.observe(viewLifecycleOwner) { workingDirPath ->
                    if (workingDirPath == null) {
                        projectViewModel.setWorkingDir(createTempDir().absolutePath)
                    }
                    showProjectNameDialog(File(workingDir))
                }
            } else {
                val gupFile = File(projectFileUri.path!!)
                gupFileManager = GupFileManager(gupFile)
                projectViewModel.workingDir.observe(viewLifecycleOwner) { workingDirPath ->
                    if (workingDirPath == null) {
                        val tempDir = gupFileManager.extractGup()
                        projectViewModel.setWorkingDir(tempDir.absolutePath)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_project, container, false)
    }

    private fun createTempDir(): File {
        val tempDir = Files.createTempDirectory("geoutils_project_").toFile()
        Runtime.getRuntime().addShutdownHook(
            Thread {
                gupFileManager.createGup()
            }
        )
        tempDir.deleteOnExit()
        return tempDir
    }

    @SuppressLint("CheckResult")
    private fun showProjectNameDialog(workingDir: File) {
        val suggestedName = workingDir.listFiles()?.firstOrNull { !it.isHidden }?.nameWithoutExtension
        MaterialDialog(requireContext()).show {
            title(R.string.project_name_dialog_title)
            input(
                prefill = suggestedName,
                hint = getString(R.string.project_name_hint)
            ) { _, text ->
                // Create PROJECT file and update ViewModel
                val gupFileManager = GupFileManager.createWithWorkingDir(workingDir, text.toString(), requireContext())
                projectViewModel.setProjectUri(gupFileManager.gupFileUri)
            }
            positiveButton(R.string.ok)
            negativeButton(R.string.cancel) {
                findNavController().navigateUp()
            }
        }
    }
}