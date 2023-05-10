package com.gremoryyx.kerjasama

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class JobAdapter(private val jobList: ArrayList<JobData>) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    private var onContactButtonClickListener: ((JobData) -> Unit)? = null
    private var onApplyButtonClickListener: ((JobData) -> Unit)? = null

    fun setOnContactButtonClickListenerLambda(listener: (JobData) -> Unit) {
        onContactButtonClickListener = listener

    }

    fun setOnApplyButtonClickListenerLambda(listener: (JobData) -> Unit) {
        onApplyButtonClickListener = listener

    }

    fun updateJobList(newJobList: ArrayList<JobData>) {
        jobList.clear()
        jobList.addAll(newJobList)
        notifyDataSetChanged()
    }

    fun setJobList(newList: List<JobData>) {
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
        holder.contactButton.setOnClickListener {
            onContactButtonClickListener?.invoke(currentItem)
        }

        holder.applyButton.setOnClickListener {
            onApplyButtonClickListener?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    inner class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactButton: Button = itemView.findViewById(R.id.contactButton)
        val applyButton: Button = itemView.findViewById(R.id.applyButton)
        private val jobImageView: ImageView = itemView.findViewById(R.id.jobImageView)
        private val jobNameTextView: TextView = itemView.findViewById(R.id.jobNameTextView)
        private val companyNameTextView: TextView = itemView.findViewById(R.id.companyNameTextView)
        private val jobTypeTextView: TextView = itemView.findViewById(R.id.jobTypeTextView)
        private val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        private val salaryTextView: TextView = itemView.findViewById(R.id.salaryTextView)
        private val jobDescriptionContentTextView: TextView = itemView.findViewById(R.id.jobDescriptionContentTextView)
        private val WalfresChipGroup: ChipGroup = itemView.findViewById(R.id.walfresChipGroup)
        private val RequirementChipGroup: ChipGroup = itemView.findViewById(R.id.requirementChipGroup)

        private var currentJob: JobData? = null

        fun bind(job: JobData) {
            if (currentJob == null || currentJob != job) {
                jobImageView.setImageBitmap(job.jobImage)
                jobNameTextView.text = job.jobName
                companyNameTextView.text = job.companyName
                jobTypeTextView.text = job.jobType
                locationTextView.text = job.location
                durationTextView.text = job.duration
                salaryTextView.text = job.salary
                jobDescriptionContentTextView.text = job.jobDescription

                setWalfares(job.walfares)
                setRequirements(job.requirements)

                currentJob = job
            }
        }

        private fun setWalfares(walfares: List<String>) {
            WalfresChipGroup.removeAllViews()
            for (criterion in walfares) {
                val chip = Chip(itemView.context)
                chip.text = criterion
                WalfresChipGroup.addView(chip)
            }
        }

        private fun setRequirements(requirements: List<String>) {
            RequirementChipGroup.removeAllViews()
            for (criterion in requirements) {
                val chip = Chip(itemView.context)
                chip.text = criterion
                RequirementChipGroup.addView(chip)
            }
        }
    }

}
