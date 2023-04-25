package com.gremoryyx.kerjasama

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class JobListFragment : Fragment(), JobSearchListener {

    private lateinit var jobListRecyclerView: RecyclerView
    private lateinit var jobListArrayList: ArrayList<Job>
    private lateinit var jobAdapter: JobAdapter
    private lateinit var originalJobList: ArrayList<Job>

    fun filterJobList(filteredJobList: ArrayList<Job>) {
        jobAdapter.updateJobList(filteredJobList)
    }

    fun getJobList(): ArrayList<Job> {
        return jobListArrayList
    }

    fun getJobArrayList(): ArrayList<Job> {
        return jobListArrayList
    }

    fun updateJobList(newList: List<Job>) {
        jobListArrayList.clear()
        jobAdapter.setJobList(newList)
        jobAdapter.notifyDataSetChanged()
    }

    fun resetJobList() {
        jobAdapter.setJobList(originalJobList)
        jobAdapter.notifyDataSetChanged()
    }

    override fun onSearchInput(newText: String) {
        val newJobList = jobListArrayList.filter { job ->
            job.jobName.contains(newText, ignoreCase = true)
        }
        updateJobList(newJobList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_job_list, container, false)

        jobListRecyclerView = view.findViewById(R.id.job_list_recycler_view)
        jobListRecyclerView.layoutManager = LinearLayoutManager(context)
        jobListRecyclerView.setHasFixedSize(true)

        jobListArrayList = ArrayList()
        jobAdapter = JobAdapter(jobListArrayList)
        jobListRecyclerView.adapter = jobAdapter

        originalJobList = ArrayList(jobListArrayList)
        jobListLoadJobs()

        // Contact Button OnClick
        jobAdapter.setOnContactButtonClickListenerLambda { job ->
            // Handle the click event for the "more" button here
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("example@example.com"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Job Application for ${job.jobName}")
            // Specify the package name of the Gmail app
            emailIntent.setPackage("com.google.android.gm")
            startActivity(Intent.createChooser(emailIntent, "Send email via:"))
        }

        // Apply Button OnClick
        jobAdapter.setOnApplyButtonClickListenerLambda { job ->
            // Handle the click event for the "more" button here
            Toast.makeText(context, "Applied for ${job.jobName}", Toast.LENGTH_LONG).show()
        }

        return view
    }

    private fun jobListLoadJobs() {
        // Add your job data here
        val jobs = listOf(
            Job(
                imageResource = R.drawable.job_image,
                jobName = "Software Engineer",
                companyName = "ABC Company",
                jobType = "Full-time",
                location = "San Francisco, CA",
                duration = "Permanent",
                jobDescription = "We are looking for a talented software engineer to join our team.",
                welfares = listOf("Flexible Schedule", "401(k) Plan"),
                requirements = listOf("Bachelor's degree in Marketing or related field", "3+ years of experience in marketing management", "Excellent communication and leadership skills")
            ),
            Job(
                imageResource = R.drawable.job_image,
                jobName = "Software Engineer",
                companyName = "ABC Company",
                jobType = "Full-time",
                location = "San Francisco, CA",
                duration = "Permanent",
                jobDescription = "We are looking for a talented software engineer to join our team.",
                welfares = listOf("Flexible Schedule", "401(k) Plan"),
                requirements = listOf("Bachelor's degree in Marketing or related field", "3+ years of experience in marketing management", "Excellent communication and leadership skills")
            ),
            Job(
                imageResource = R.drawable.job_image,
                jobName = "Software Engineer",
                companyName = "ABC Company",
                jobType = "Full-time",
                location = "San Francisco, CA",
                duration = "Permanent",
                jobDescription = "We are looking for a talented software engineer to join our team.",
                welfares = listOf("Flexible Schedule", "401(k) Plan"),
                requirements = listOf("Bachelor's degree in Marketing or related field", "3+ years of experience in marketing management", "Excellent communication and leadership skills")
            ),
            // Add more job data here
        )
        jobListArrayList.addAll(jobs)
        originalJobList.addAll(jobs)
        jobAdapter.notifyDataSetChanged()
    }
}