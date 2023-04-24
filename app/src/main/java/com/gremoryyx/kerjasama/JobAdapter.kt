package com.gremoryyx.kerjasama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class JobAdapter(private val jobList: ArrayList<Job>) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    private var onMoreButtonClickListener: ((Job) -> Unit)? = null

    fun setOnMoreButtonClickListenerLambda(listener: (Job) -> Unit) {
        onMoreButtonClickListener = listener
    }

    fun updateJobList(newJobList: ArrayList<Job>) {
        jobList.clear()
        jobList.addAll(newJobList)
        notifyDataSetChanged()
    }

    fun setJobList(newList: List<Job>) {
        jobList.clear()
        jobList.addAll(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.job_list_item, parent, false)
        return JobViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val currentItem = jobList[position]
        holder.bind(currentItem)
        holder.moreButton.setOnClickListener {
            onMoreButtonClickListener?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    inner class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val moreButton = itemView.findViewById<Button>(R.id.moreButton)

        fun bind(job: Job) {
            itemView.findViewById<TextView>(R.id.jobNameTextView).text = job.jobName
            itemView.findViewById<TextView>(R.id.companyNameTextView).text = job.companyName
            itemView.findViewById<TextView>(R.id.jobTypeTextView).text = job.jobType
            itemView.findViewById<TextView>(R.id.locationTextView).text = job.location

            val chipGroup = itemView.findViewById<ChipGroup>(R.id.requirementsChipGroup)

            for (criterion in job.requirements) {
                val chip = Chip(itemView.context)
                chip.text = criterion
                chipGroup.addView(chip)
            }
        }
    }
}
