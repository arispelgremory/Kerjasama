package com.gremoryyx.kerjasama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior

class JobListFragment : Fragment(), JobSearchListener {

    private lateinit var jobListRecyclerView: RecyclerView
    private lateinit var jobListArrayList: ArrayList<Job>
    private lateinit var jobAdapter: JobAdapter
    private lateinit var jobListBottomSheetBehavior: BottomSheetBehavior<FrameLayout>
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

        originalJobList = ArrayList(jobListArrayList)
        loadJobs()

        jobListRecyclerView.adapter = jobAdapter

        // Initialize the BottomSheetBehavior
        jobListBottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet))
        jobListBottomSheetBehavior.peekHeight = 0
        jobListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        jobAdapter.setOnMoreButtonClickListenerLambda { job ->
            // Handle the click event for the "more" button here
            jobListBottomSheetBehavior.peekHeight = 0
            jobListBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        jobListBottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // The bottom sheet is hidden, do something here
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // The bottom sheet is sliding, do something here if needed
                if (slideOffset == -1f) {
                    jobListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        })

        view.setOnClickListener {
            if (jobListBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                jobListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        return view
    }

    private fun loadJobs() {
        // Add your job data here
        val jobs = listOf(
            Job(
                R.drawable.job_image,
                "Software Engineer",
                "Google",
                "Mountain View, CA",
                "$120k-$150k",
                "Full-time",
                "https://careers.google.com/jobs/results/131139632569880966-software-engineer/",
                welfares = listOf("Flexible Schedule", "401(k) Plan"),
                requirements = listOf("Bachelor's degree in Marketing or related field", "3+ years of experience in marketing management", "Excellent communication and leadership skills")

            ),
            Job(
                R.drawable.job_image,
                "UX Designer",
                "Apple",
                "Cupertino, CA",
                "$130k-$160k",
                "Full-time",
                "https://careers.google.com/jobs/results/131139632569880966-software-engineer/",
                welfares = listOf("Flexible Schedule", "401(k) Plan"),
                requirements = listOf("Bachelor's degree in Marketing or related field", "3+ years of experience in marketing management", "Excellent communication and leadership skills")
            ),
            Job(
                R.drawable.job_image,
                "Data Scientist",
                "Microsoft",
                "Redmond, WA",
                "$140k-$170k",
                "Full-time",
                "https://careers.google.com/jobs/results/131139632569880966-software-engineer/",
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