package com.example.stulogandroidapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.yourpackage.models.Subject

class SubjectPagerAdapter(
    private val subjects: List<Subject>,
    private val context: Context,
    private val onSubjectClick: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectPagerAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cupView: View = itemView.findViewById(R.id.cup_view)
        val subjectName: TextView = itemView.findViewById(R.id.tv_subject_name)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_subject)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit_subject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject_cup, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]

        // Set cup color
        holder.cupView.setBackgroundColor(subject.color)
        holder.subjectName.text = subject.name

        holder.btnDelete.setOnClickListener {
            // TODO: Delete subject from database
        }

        holder.btnEdit.setOnClickListener {
            // TODO: Show edit dialog
        }

        holder.itemView.setOnClickListener {
            onSubjectClick(subject)
        }
    }

    override fun getItemCount() = subjects.size
}