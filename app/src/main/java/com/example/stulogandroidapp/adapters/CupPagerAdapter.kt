package com.example.stulogandroidapp.adapters

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.Subject
import com.example.stulogandroidapp.viewmodels.SubjectViewModel
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.coroutines.*

class CupPagerAdapter(
    private val context: Context,
    private var subjects: MutableList<Subject>,
    private val subjectViewModel: SubjectViewModel
) : RecyclerView.Adapter<CupPagerAdapter.CupViewHolder>() {

    private val db = AppDatabase.getDatabase(context)

    inner class CupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cupImage: ImageView = view.findViewById(R.id.cupImage)
        val fillView: View = view.findViewById(R.id.fillView)
        val subjectNameText: TextView = view.findViewById(R.id.tvSubjectName)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cup, parent, false)
        return CupViewHolder(view)
    }

    override fun onBindViewHolder(holder: CupViewHolder, position: Int) {
        val subject = subjects[position]

        holder.subjectNameText.text = subject.name
        holder.cupImage.setColorFilter(null)

        // Reset fill to zero before animation
        holder.fillView.layoutParams.height = 0
        holder.fillView.requestLayout()

        // Set fill color to match subject
        try {
            holder.fillView.setBackgroundColor(Color.parseColor(subject.color))
        } catch (e: Exception) {
            holder.fillView.setBackgroundColor(Color.parseColor("#FF8A65"))
        }

        // Animate cup fill based on completed task weight
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val tasks = db.taskDao().getTasksBySubjectOnce(subject.id)
                val totalWeight = tasks.sumOf { it.weighting }
                val completedWeight = tasks.filter { it.completed }.sumOf { it.weighting }
                val progressPercent = if (totalWeight == 0) 0 else (completedWeight * 100 / totalWeight)

                withContext(Dispatchers.Main) {
                    // Animate fill height
                    holder.itemView.post {
                        val maxHeight = holder.itemView.height
                        val targetHeight = maxHeight * progressPercent / 100

                        val animator = ValueAnimator.ofInt(0, targetHeight)
                        animator.duration = 700
                        animator.addUpdateListener {
                            val value = it.animatedValue as Int
                            holder.fillView.layoutParams.height = value
                            holder.fillView.requestLayout()
                        }
                        animator.start()
                    }
                }
            } catch (e: Exception) {
                Log.e("CupAdapter", "Error animating cup fill", e)
            }
        }

        // Delete subject
        holder.btnDelete.setOnClickListener {
            subjectViewModel.delete(subject)
        }

        // Edit subject
        holder.btnEdit.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_subject, null)
            val inputName = dialogView.findViewById<EditText>(R.id.editSubjectName)
            val colorButton = dialogView.findViewById<Button>(R.id.btnPickColor)

            inputName.setText(subject.name)

            var newColor = subject.color
            colorButton.setBackgroundColor(Color.parseColor(subject.color))

            colorButton.setOnClickListener {
                val currentColor = Color.parseColor(subject.color)
                ColorPickerDialogBuilder
                    .with(context)
                    .setTitle("Choose Subject Color")
                    .initialColor(currentColor)
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton("OK") { _, selectedColor, _ ->
                        newColor = String.format("#%06X", 0xFFFFFF and selectedColor)
                        colorButton.setBackgroundColor(selectedColor)
                    }
                    .setNegativeButton("Cancel", null)
                    .build()
                    .show()
            }

            AlertDialog.Builder(context)
                .setTitle("Edit Subject")
                .setView(dialogView)
                .setPositiveButton("Update") { _, _ ->
                    val newName = inputName.text.toString()
                    if (newName.isNotEmpty()) {
                        subjectViewModel.update(subject.copy(name = newName, color = newColor))
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int = subjects.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateSubjects(newSubjects: List<Subject>) {
        subjects = newSubjects.toMutableList()
        notifyDataSetChanged()
    }
}
