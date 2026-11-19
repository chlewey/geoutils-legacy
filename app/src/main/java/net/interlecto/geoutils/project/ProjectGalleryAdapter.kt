package net.interlecto.geoutils.project

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import net.interlecto.geoutils.ProjectViewModel
import net.interlecto.geoutils.R
import net.interlecto.geoutils.data.FileDictionaryRepository
import net.interlecto.geoutils.gup.GupFileManager
import java.io.File

class ProjectGalleryAdapter(
    private val projects: List<ProjectItem>,
    private val onFileSelected: (ProjectItem) -> Unit,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val fileDictionaryRepository: FileDictionaryRepository,
    private val projectViewModel: ProjectViewModel
) : RecyclerView.Adapter<ProjectGalleryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
        private lateinit var item: ProjectItem
        private val projectNameTextView: TextView = itemView.findViewById(R.id.project_filename)
        private val projectThumbnailImageView: ImageView = itemView.findViewById(R.id.project_thumbnail)
        private val gestureDetector = GestureDetector(itemView.context, this)

        init {
            itemView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        }

        fun bind(item: ProjectItem) {
            this.item = item
            projectNameTextView.text = item.name

           val icon: Drawable? = when (item.type) {
                ProjectItemType.PROJECT -> {
                    val thumbnailBytes = GupFileManager(File(item.path)).readThumbnail()
                    val thumbnailBitmap = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.size)
                    BitmapDrawable(itemView.context.resources, thumbnailBitmap)
                }
                ProjectItemType.IMAGE -> {
                    // Using Glide to create a thumbnail
                    Glide.with(itemView.context)
                        .load(item.path)
                        .override(
                            itemView.context.resources.getDimensionPixelSize(R.dimen.thumbnail_size),
                            itemView.context.resources.getDimensionPixelSize(R.dimen.thumbnail_size)
                        )
                        .centerCrop()
                        .into(projectThumbnailImageView)
                    null // No need to set a drawable icon for images
                }
                ProjectItemType.KML -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_fileformat_kml)
                ProjectItemType.SVG -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_fileformat_svg)
                ProjectItemType.UNKNOWN -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_unknown_format)
               ProjectItemType.FILE -> TODO()
               ProjectItemType.FOLDER -> TODO()
               ProjectItemType.REMOTE -> TODO()
               ProjectItemType.DEMO -> TODO()
               ProjectItemType.AD -> TODO()
           }

            if (icon != null) {
                projectThumbnailImageView.setImageDrawable(icon)
            }

            // Set the click listener
            itemView.setOnClickListener {
                onFileSelected(item) // Use the provided lambda to handle click events
            }
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            when(item.type) {
                ProjectItemType.PROJECT -> onProjectDoubleTapped(item)
                else -> onFileDoubleTapped(item)
            }
            return true
        }

        // You also need to override the other methods of OnDoubleTapListener:
        override fun onDoubleTapEvent(e: MotionEvent): Boolean { return false }
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean { return false  }
        override fun onDown(e: MotionEvent): Boolean { return false }
        override fun onShowPress(e: MotionEvent) {}
        override fun onSingleTapUp(e: MotionEvent): Boolean { return false }
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean { return false }
        override fun onLongPress(e: MotionEvent) {}
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean { return false }

        private fun onProjectDoubleTapped(item: ProjectItem) {
            val tempDir = GupFileManager(File(item.path)).extractGup()
            projectViewModel.openProject(tempDir.absolutePath, Uri.fromFile(File(item.path)))
        }

        private fun onFileDoubleTapped(item: ProjectItem) {
            lifecycleScope.launch {
                val associatedProjects = fileDictionaryRepository.getProjectsForFile(item.path)
                when (associatedProjects.size) {
                    0 -> {
                        // Open dialog to create new project
                        // ... (implementation for creating new project)
                    }
                    1 -> {
                        val gupFile = File(associatedProjects[0])
                        val tempDir = GupFileManager(gupFile).extractGup()
                        projectViewModel.openProject(tempDir.absolutePath, Uri.fromFile(gupFile))
                    }
                    else -> {
                        // Open dialog to select project
                        // ... (implementation for selecting project)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
        /*
        val thumbnail = item.thumbnailUri
        if (thumbnail != null) {
            imageView.setImageURI(thumbnail)
        } else {
            val icon = item.iconResId ?: getDefaultIcon(item)
            imageView.setImageResource(icon)
        }*/
    }

    override fun getItemCount(): Int = projects.size
}