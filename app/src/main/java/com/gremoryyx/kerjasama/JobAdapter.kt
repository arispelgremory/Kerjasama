package com.gremoryyx.kerjasama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class JobAdapter(private val jobList: ArrayList<Job>) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.job_list_item, parent, false)
        return JobViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val currentItem = jobList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    inner class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(job: Job) {
            itemView.findViewById<TextView>(R.id.jobNameTextView).text = job.name
            itemView.findViewById<TextView>(R.id.companyNameTextView).text = job.companyName
            itemView.findViewById<TextView>(R.id.jobTypeTextView).text = job.jobType
            itemView.findViewById<TextView>(R.id.locationTextView).text = job.location

            val chipGroup = itemView.findViewById<ChipGroup>(R.id.requirementsTextView)

            for (criterion in job.requirements) {
                val chip = Chip(itemView.context)
                chip.text = criterion
                chipGroup.addView(chip)
            }
        }
    }
}