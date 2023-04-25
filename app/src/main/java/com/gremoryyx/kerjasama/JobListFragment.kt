package com.gremoryyx.kerjasama

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.gremoryyx.kerjasama.repository.JobRepository
import com.gremoryyx.kerjasama.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class JobListFragment : Fragment(), JobSearchListener {

    private lateinit var jobListRecyclerView: RecyclerView
    private lateinit var jobListArrayList: ArrayList<JobData>
    private lateinit var jobAdapter: JobAdapter
    private lateinit var originalJobList: ArrayList<JobData>

    private lateinit var db: FirebaseFirestore
    var loginRepo = LoginRepository()
    var jobRepo = JobRepository()

    fun filterJobList(filteredJobList: ArrayList<JobData>) {
        jobAdapter.updateJobList(filteredJobList)
    }

    fun getJobList(): ArrayList<JobData> {
        return jobListArrayList
    }

    fun getJobArrayList(): ArrayList<JobData> {
        return jobListArrayList
    }

    fun updateJobList(newList: List<JobData>) {
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

        if (loginRepo.validateUser()){
            CoroutineScope(Dispatchers.IO).launch {
                jobListLoadJobs()
            }
        }
        else{
            Toast.makeText(context, "Login First", Toast.LENGTH_LONG).show()
        }



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
            Toast.makeText(context, "Failed to applied for ${job.jobName}", Toast.LENGTH_LONG).show()
            addJob(job)

//            val appliedJob = hashMapOf(
//                "company" to job.companyName,
//                "job" to job.jobImage,
//                "registered_status" to "pending",
//            )
//            db.collection("Registered Job").add(appliedJob).addOnSuccessListener { documentReference ->
//                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                Toast.makeText(context, "Applied for ${job.jobName}", Toast.LENGTH_LONG).show()
//            }.addOnFailureListener { e ->
//                Log.w(ContentValues.TAG, "Error adding document", e)
//                Toast.makeText(context, "Failed to applied for ${job.jobName}", Toast.LENGTH_LONG).show()
//            }
        }

        return view
    }

    private fun addJob(jobData: JobData) {
        CoroutineScope(Dispatchers.IO).launch {
            val appliedJob = hashMapOf(
                "company" to jobData.companyName,
                "job" to jobData.jobName,
                "registered_status" to "pending",
            )
            db.collection("Registered Job").add(appliedJob).addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(context, "Applied for ${jobData.jobName}", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
                Toast.makeText(context, "Failed to applied for ${jobData.jobName}", Toast.LENGTH_LONG).show()
            }
        }

    }

    private suspend fun jobListLoadJobs() {
        db = FirebaseFirestore.getInstance()
        val JobRef = db.collection("Job")
        JobRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.IO).launch {
                    for (document in task.result!!) {
                        val jobData = JobData()
                        // Use CoroutineScope to wait for the image to be retrieved
                        val jobImage = document.data["job_image"] as String
                        val bitmap = jobRepo.getImageFile(jobImage).await()
                        jobData.jobImage = bitmap
                        jobData.jobName = (document.data["job_name"]).toString()
                        jobData.companyName = (document.data["company"]).toString()
                        jobData.jobType = (document.data["job_type"]).toString()
                        jobData.location = (document.data["location"]).toString()
                        jobData.duration = (document.data["work_duration"]).toString()
                        jobData.salary = (document.data["salary"]).toString()
                        jobData.jobDescription = (document.data["job_description"]).toString()

                        var walfaresList = document.data["walfares"]
                        for (walfaresData in walfaresList as ArrayList<String>) {
                            jobData.walfares.add(walfaresData)
                        }

                        var requirementList = document.data["requirements"]
                        for (requirementData in requirementList as ArrayList<String>) {
                            jobData.requirements.add(requirementData)
                        }

                        jobListArrayList.add(jobData)
                    }
                    activity?.runOnUiThread {
                        jobAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                // Handle error getting documents
                Toast.makeText(requireContext(), "Error getting documents.", Toast.LENGTH_SHORT).show()
            }
        }
    }
//        // Add your job data here
//        val jobs = listOf(
//            JobData(
//                imageResource = null,
//                jobName = "Software Engineer",
//                companyName = "ABC Company",
//                jobType = "Full-time",
//                location = "San Francisco, CA",
//                duration = "Permanent",
//                jobDescription = "We are looking for a talented software engineer to join our team.",
//                welfares = listOf("Flexible Schedule", "401(k) Plan"),
//                requirements = listOf("Bachelor's degree in Marketing or related field", "3+ years of experience in marketing management", "Excellent communication and leadership skills")
//            ),
//            JobData(
//                imageResource = null,
//                jobName = "Software Engineer",
//                companyName = "ABC Company",
//                jobType = "Full-time",
//                location = "San Francisco, CA",
//                duration = "Permanent",
//                jobDescription = "We are looking for a talented software engineer to join our team.",
//                welfares = listOf("Flexible Schedule", "401(k) Plan"),
//                requirements = listOf("Bachelor's degree in Marketing or related field", "3+ years of experience in marketing management", "Excellent communication and leadership skills")
//            ),
//            JobData(
//                imageResource = null,
//                jobName = "Software Engineer",
//                companyName = "ABC Company",
//                jobType = "Full-time",
//                location = "San Francisco, CA",
//                duration = "Permanent",
//                jobDescription = "We are looking for a talented software engineer to join our team.",
//                welfares = listOf("Flexible Schedule", "401(k) Plan"),
//                requirements = listOf("Bachelor's degree in Marketing or related field", "3+ years of experience in marketing management", "Excellent communication and leadership skills")
//            ),
//            // Add more job data here
//        )
//        jobListArrayList.addAll(jobs)
//        originalJobList.addAll(jobs)

}