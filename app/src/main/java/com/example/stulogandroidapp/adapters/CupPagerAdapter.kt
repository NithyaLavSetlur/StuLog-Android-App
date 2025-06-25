package com.example.stulogandroidapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.models.Subject
import com.example.stulogandroidapp.viewmodels.SubjectViewModel
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder

class CupPagerAdapter(
    private val context: Context,
    private var subjects: MutableList<Subject>,
    private val subjectViewModel: SubjectViewModel
) : RecyclerView.Adapter<CupPagerAdapter.CupViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateSubjects(newSubjects: List<Subject>) {
        subjects = newSubjects.toMutableList()
        notifyDataSetChanged()
    }

    inner class CupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cupImage: ImageView = view.findViewById(R.id.cupImage)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cup, parent, false)
        return CupViewHolder(view)
    }

    override fun onBindViewHolder(holder: CupViewHolder, position: Int) {
        val subject = subjects[position]
        holder.cupImage.setColorFilter(Color.parseColor(subject.color))

        holder.btnDelete.setOnClickListener {
            subjectViewModel.delete(subject)
        }

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
}
