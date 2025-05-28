package com.example.stulogandroidapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R

data class Subject(val name: String, val tasks: List<String>)

class SubjectPagerAdapter(
    private val subjects: List<Subject>
) : RecyclerView.Adapter<SubjectPagerAdapter.SubjectViewHolder>() {

    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectTitle: TextView = itemView.findViewById(R.id.subjectTitle)
        val taskList: RecyclerView = itemView.findViewById(R.id.taskRecycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject_page, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.subjectTitle.text = subject.name

        holder.taskList.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.taskList.adapter = TaskListAdapter(subject.tasks)
    }

    override fun getItemCount(): Int = subjects.size
}
