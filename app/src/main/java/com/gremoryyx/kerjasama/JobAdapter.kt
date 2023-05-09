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

        fun bind(job: JobData) {
            itemView.findViewById<ImageView>(R.id.jobImageView).setImageBitmap(job.jobImage)
            itemView.findViewById<TextView>(R.id.jobNameTextView).text = job.jobName
            itemView.findViewById<TextView>(R.id.companyNameTextView).text = job.companyName
            itemView.findViewById<TextView>(R.id.jobTypeTextView).text = job.jobType
            itemView.findViewById<TextView>(R.id.locationTextView).text = job.location
            itemView.findViewById<TextView>(R.id.durationTextView).text = job.duration
            itemView.findViewById<TextView>(R.id.salaryTextView).text = job.salary
            itemView.findViewById<TextView>(R.id.jobDescriptionContentTextView).text = job.jobDescription
            val WalfresChipGroup = itemView.findViewById<ChipGroup>(R.id.walfresChipGroup)


            Log.d("JobAdapter", "This is job adapter: RYU JIN NO KEN WO KURAE")
            for (criterion in job.walfares){
                val chip = Chip(itemView.context)
                chip.text = criterion
                WalfresChipGroup.addView(chip)
            }

            val RequirementChipGroup = itemView.findViewById<ChipGroup>(R.id.requirementChipGroup)

            for (criterion in job.requirements) {
                val chip = Chip(itemView.context)
                chip.text = criterion
                RequirementChipGroup.addView(chip)
            }


        }
    }

//    class JobDiffCallback(private val oldList: List<JobData>, private val newList: List<JobData>) : DiffUtil.Callback() {
//
//        override fun getOldListSize(): Int {
//            return oldList.size
//        }
//
//        override fun getNewListSize(): Int {
//            return newList.size
//        }
//
//        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//            return oldList[oldItemPosition].id == newList[newItemPosition].id
//        }
//
//        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//            return oldList[oldItemPosition] == newList[newItemPosition]
//        }
//    }
}
