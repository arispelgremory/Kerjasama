package com.gremoryyx.kerjasama

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior

class RegisteredJobFragment : Fragment() {
    private lateinit var registeredJobRecyclerView: RecyclerView
    private lateinit var registeredJobArrayList: ArrayList<RegisteredJob>
    private lateinit var registeredJobAdapter: RegisteredJobAdapter
    private lateinit var registeredJobBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        registeredJobRecyclerView = view.findViewById(R.id.registered_job_recycler_view)
        registeredJobRecyclerView.layoutManager = LinearLayoutManager(context)
        registeredJobRecyclerView.setHasFixedSize(true)

        registeredJobArrayList = ArrayList()
        registeredJobAdapter = RegisteredJobAdapter(registeredJobArrayList)

        val registeredJobs = listOf(
            RegisteredJob(
                registeredStatus = "Registered",
                imageResource = R.drawable.job_image,
                jobName = "Software Engineer",
                companyName = "ABC Company",
                jobType = "Full-time",
                location = "San Francisco, CA",
                duration = "Permanent",
                jobDescription = "We are looking for a talented software engineer to join our team.",
                welfares = listOf("Health Insurance", "Paid Time Off", "Retirement Plan"),
                requirements = listOf("Bachelor's degree in Computer Science or equivalent experience", "5+ years of experience in software development", "Proficiency in Java and Python")
            ),

            RegisteredJob(
                registeredStatus = "Registered",
                imageResource = R.drawable.job_image,
                jobName = "Marketing Manager",
                companyName = "XYZ Company",
                jobType = "Part-time",
                location = "New York, NY",
                duration = "Contract",
                jobDescription = "We are seeking an experienced marketing manager to help us drive growth.",
                welfares = listOf("Flexible Schedule", "401(k) Plan"),
                requirements = listOf("Bachelor's degree in Marketing or related field", "3+ years of experience in marketing management", "Excellent communication and leadership skills")
        ),

            RegisteredJob(
                registeredStatus = "Registered",
                imageResource = R.drawable.job_image,
                jobName = "Graphic Designer",
                companyName = "PQR Company",
                jobType = "Freelance",
                location = "Los Angeles, CA",
                duration = "Temporary",
                jobDescription = "We are looking for a talented graphic designer to work on our new project.",
                welfares = listOf("Remote work", "Competitive pay"),
                requirements = listOf("Bachelor's degree in Graphic Design or related field", "2+ years of experience in graphic design", "Proficiency in Adobe Creative Suite")
            )
        )

        registeredJobArrayList.add(registeredJobs[0])
        registeredJobArrayList.add(registeredJobs[1])
        registeredJobArrayList.add(registeredJobs[2])

        registeredJobRecyclerView.adapter = registeredJobAdapter

        // Initialize the BottomSheetBehavior
        registeredJobBottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet))
        registeredJobBottomSheetBehavior.peekHeight = 0
        registeredJobBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        registeredJobAdapter.setOnMoreButtonClickListenerLambda { job ->
            // Handle the click event for the "more" button here
            registeredJobBottomSheetBehavior.peekHeight = 0
            registeredJobBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        registeredJobBottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // The bottom sheet is hidden, do something here
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // The bottom sheet is sliding, do something here if needed
                if (slideOffset == -1f) {
                    registeredJobBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        })

        return view
    }
}