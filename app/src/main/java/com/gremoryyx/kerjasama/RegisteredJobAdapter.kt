package com.gremoryyx.kerjasama

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class RegisteredJobAdapter(private val registeredJobList: ArrayList<RegisteredJobData>) : RecyclerView.Adapter<RegisteredJobAdapter.RegisteredJobViewHolder>() {

    private var onCancelButtonClickListener: ((RegisteredJobData) -> Unit)? = null

    fun setOnCancelButtonClickListenerLambda(listener: (RegisteredJobData) -> Unit) {
        onCancelButtonClickListener = listener
    }

    fun updateJobList(newJobList: ArrayList<RegisteredJobData>) {
        registeredJobList.clear()
        registeredJobList.addAll(newJobList)
        notifyDataSetChanged()
    }

    fun setJobList(newList: List<RegisteredJobData>) {
        registeredJobList.clear()
        registeredJobList.addAll(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegisteredJobViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.register_item, parent, false)
        return RegisteredJobViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RegisteredJobViewHolder, position: Int) {
        val currentItem = registeredJobList[position]
        holder.bind(currentItem)
        holder.cancelButton.setOnClickListener {
            onCancelButtonClickListener?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return registeredJobList.size
    }



    inner class RegisteredJobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cancelButton = itemView.findViewById<Button>(R.id.cancelButton)
        private val jobImageView: ImageView = itemView.findViewById(R.id.jobImageView)
        private val jobNameTextView: TextView = itemView.findViewById(R.id.jobNameTextView)
        private val companyNameTextView: TextView = itemView.findViewById(R.id.companyNameTextView)
        private val jobTypeTextView: TextView = itemView.findViewById(R.id.jobTypeTextView)
        private val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        private val salaryTextView: TextView = itemView.findViewById(R.id.salaryTextView)
        private val jobDescriptionContentTextView: TextView = itemView.findViewById(R.id.jobDescriptionContentTextView)

        private var currentRegJob: RegisteredJobData? = null

        fun bind(job: RegisteredJobData) {
            if (currentRegJob == null || currentRegJob != job) {
                jobImageView.setImageBitmap(job.jobImage)
                jobNameTextView.text = job.jobName
                companyNameTextView.text = job.companyName
                jobTypeTextView.text = job.jobType
                locationTextView.text = job.location
                durationTextView.text = job.duration
                salaryTextView.text = job.salary
                jobDescriptionContentTextView.text = job.jobDescription


                currentRegJob = job
            }
        }
    }

}