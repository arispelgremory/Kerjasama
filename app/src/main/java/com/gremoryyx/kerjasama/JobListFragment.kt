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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.rpc.context.AttributeContext.Auth
import com.gremoryyx.kerjasama.repository.JobRepository
import com.gremoryyx.kerjasama.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Document

class JobListFragment : Fragment(), JobSearchListener {

    private lateinit var jobListRecyclerView: RecyclerView
    private lateinit var jobListArrayList: ArrayList<JobData>
    private lateinit var jobAdapter: JobAdapter
    private lateinit var originalJobList: ArrayList<JobData>

    private lateinit var db: FirebaseFirestore
    var loginRepo = LoginRepository()
    var jobRepo = JobRepository()
    var regisFrag = RegisteredJobFragment()

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
            applyJob(job)
        }

        return view
    }

    private fun applyJob(jobData: JobData) {
        val user = Firebase.auth.currentUser
        val regRef = db.collection("Registered Job")
        regRef.get().addOnCompleteListener { task->
            if (task.isSuccessful){
                CoroutineScope(Dispatchers.IO).launch {
                    // Registered job will have some field data
                    // job, registered status and user
                    var jobDocument = ""
                    var userDoc = ""
                        //JOB
                        // job will be a reference to the job, to get the job reference, use jobData.jobName and jobData.companyName
                        // After that, use the job reference to get the job document
                        // Go through all the document, to check which one contain both jobName and companyName
                        // If the document contain both jobName and companyName, then get the document path reference
                        val jobRef = db.collection("Job")
                        CoroutineScope(Dispatchers.IO).launch {
                            jobDocument = withContext(Dispatchers.IO){
                                jobRepo.validateDocument(jobRef, jobData.jobName, jobData.companyName)
                            }

                            Log.d("LOG job doc#####", "Job Doc: $jobDocument")

                            //USER
                            // get into the user collection and then compare the user.uid with the document.id to get the document path reference
                            val userRef = db.collection("User")

                            Log.d("LOG user id#####", "User ID: ${userRef.document("${user!!.uid}").id}")
                            if (userRef.document("${user!!.uid}").id != null){
                                userDoc = userRef.document("${user!!.uid}").path
                            }
                            Log.d("LOG user doc#####", "User Doc: $userDoc")

                            if (jobDocument != "" && userDoc != ""){
                                //ADD DATA TO REGISTERED JOB
                                val docFormat_jobDocument: DocumentReference = db.document(jobDocument)
                                val docFormat_userDoc: DocumentReference = db.document(userDoc)
                                regRef.add(hashMapOf(
                                    "job" to docFormat_jobDocument,
                                    "registered_status" to "pending",
                                    "user" to docFormat_userDoc
                                ))
                            }
                        }


                }

            }else {
                // Handle error getting documents
                Toast.makeText(requireContext(), "Error getting documents.", Toast.LENGTH_SHORT).show()
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