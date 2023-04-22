package com.gremoryyx.kerjasama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class JobListFragment : Fragment() {

    private lateinit var jobListRecyclerView: RecyclerView
    private lateinit var jobListArrayList: ArrayList<Job>
    private lateinit var jobAdapter: JobAdapter

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

        val jobs = listOf(
            Job(
                "Food Delivery Services",
                "FoodPanda Malaysia Sdn Bhd",
                "Food Delivery",
                "Selangor, KL",
                listOf("Driver's License", "Motorcycle", "Smartphone with GPS"),
                R.drawable.job_image
            ),
            Job(
                "Software Engineer",
                "Google Inc.",
                "Software Engineering",
                "California, USA",
                listOf("Bachelor's Degree in Computer Science", "3+ years of experience"),
                R.drawable.job_image
            ),
            Job(
                "Data Analyst",
                "Facebook Inc.",
                "Data Analysis",
                "New York, USA",
                listOf("Bachelor's Degree in Statistics", "2+ years of experience"),
                R.drawable.job_image
            )
        )

        jobListArrayList.add(jobs[0])
        jobListArrayList.add(jobs[1])
        jobListArrayList.add(jobs[2])

        jobListRecyclerView.adapter = jobAdapter
        return view
    }
}