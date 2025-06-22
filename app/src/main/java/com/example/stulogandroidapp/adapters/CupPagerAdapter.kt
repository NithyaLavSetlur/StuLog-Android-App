package com.example.stulogandroidapp.adapters

import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.models.Subject
import com.example.stulogandroidapp.viewmodels.SubjectViewModel

class CupPagerAdapter(
    private val context: Context,
    private var subjects: MutableList<Subject>,
    private val subjectViewModel: SubjectViewModel
) : RecyclerView.Adapter<CupPagerAdapter.CupViewHolder>() {

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
            val input = EditText(context)
            input.setText(subject.name)
            input.setPadding(20)

            AlertDialog.Builder(context)
                .setTitle("Edit Subject Name")
                .setView(input)
                .setPositiveButton("Update") { _, _ ->
                    val newName = input.text.toString()
                    if (newName.isNotEmpty()) {
                        subjectViewModel.update(subject.copy(name = newName))
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int = subjects.size
}
