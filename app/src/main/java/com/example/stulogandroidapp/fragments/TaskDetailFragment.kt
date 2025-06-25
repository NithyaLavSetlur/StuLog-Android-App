package com.example.stulogandroidapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment // ✅ Make sure this is present
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.CompletedTask
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailFragment : Fragment() {

    private val args: TaskDetailFragmentArgs by navArgs()
    private lateinit var db: AppDatabase
    private var taskName = ""
    private var subjectName = ""
    private var selectedImageUri: Uri? = null
    private val IMAGE_PICK_CODE = 200

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_detail, container, false)
        db = AppDatabase.getDatabase(requireContext())

        val taskTitle = view.findViewById<TextView>(R.id.taskDetailTitle) // this is a common issue in the project (the IDE doesn't know what the directory R.id.___ is)
        val taskDesc = view.findViewById<TextView>(R.id.taskDetailDesc)
        val taskDue = view.findViewById<TextView>(R.id.taskDetailDue)
        val taskWeight = view.findViewById<TextView>(R.id.taskDetailWeight)
        val completeBtn = view.findViewById<Button>(R.id.btnMarkComplete)

        lifecycleScope.launch {
            val task = db.taskDao().getTaskById(args.taskId)
            val subject = db.subjectDao().getSubjectById(task.subjectId)

            taskName = task.name
            subjectName = subject.name

            taskTitle.text = task.name
            taskDesc.text = task.description ?: "No description"
            taskDue.text = "Due: ${task.dueDate ?: "None"}"
            taskWeight.text = "Weighting: ${task.weighting}"
        }

        completeBtn.setOnClickListener {
            showImageUploadDialog()
        }

        return view
    }

    private fun showImageUploadDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_upload_photo, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.confirmationImage)
        val pickButton = dialogView.findViewById<Button>(R.id.pickImageButton)

        pickButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Upload Photo Confirmation")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                if (selectedImageUri != null) {
                    val path = selectedImageUri.toString()
                    val timestamp = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                    lifecycleScope.launch {
                        db.completedTaskDao().insertCompletedTask(
                            CompletedTask(
                                taskName = taskName,
                                subjectName = subjectName,
                                imageUri = path,
                                dateCompleted = timestamp
                            )
                        )
                        db.taskDao().markComplete(args.taskId)
                        Toast.makeText(requireContext(), "Task marked as complete", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
        }
    }
}
