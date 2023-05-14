package com.gremoryyx.kerjasama

import android.content.Intent
import android.graphics.BitmapFactory
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
import com.gremoryyx.kerjasama.repository.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Document

class JobListFragment : Fragment(), SearchListener {

    private lateinit var jobListRecyclerView: RecyclerView
    private lateinit var jobListArrayList: ArrayList<JobData>
    private lateinit var jobAdapter: JobAdapter
    private lateinit var originalJobList: ArrayList<JobData>

    private lateinit var db: FirebaseFirestore
    var loginRepo = LoginRepository()
    var jobRepo = JobRepository()
    var regisFrag = RegisteredJobFragment()
    var userRepo = UserRepository()

    fun updateJobList(newList: List<JobData>) {
        jobAdapter.setJobList(newList)
        jobAdapter.notifyDataSetChanged()
    }

    fun resetJobList() {
        jobListArrayList.clear()
        CoroutineScope(Dispatchers.IO).launch{
            jobListLoadJobs()
        }
        jobListRecyclerView.scrollToPosition(0)
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
            applyJob(job)
        }

        return view
    }

    private fun applyJob(jobData: JobData) {
        val regRef = db.collection("Registered Job")
        val userId = userRepo.getUserID()
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

                            //USER
                            // get into the user collection and then compare the user.uid with the document.id to get the document path reference
                            val userRef = db.collection("User")
                            if (userRef.document("${userId}") != null){
                                userDoc = userRef.document("${userId}").path
                            }

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
        var RegisteredJobList = ArrayList<String>()
        JobRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.IO).async {
                    Log.d("FILTERING", "@@@@@@@@@@@@@@@@@@@@@@")
                    val filteringJob = async {
                        for (document in task.result!!) {
                            // To check if the job is already registered
                            // First I need to get the job document path reference
                            // And then get the user document path reference & registered job document path reference
                            CoroutineScope(Dispatchers.IO).async{
                                try {
                                    Log.d("FILTERING", "${document.id}")
                                    RegisteredJobList.add(jobRepo.checkJobRegistered(document.id))
                                }catch (e: Exception){
                                    Log.d("ERROR", "Error getting documents: ", e)
                                }
                            }
                        }
                    }
                    filteringJob.await()

                    CoroutineScope(Dispatchers.IO).async {
                        Log.d("AFTER FILTERING", "###################")
                        // To filter the registered job, I need to pass in the registered job id to the function
                        // So that I could compare it and filter out add it into the array list.
                        val deferredJobData = async{
                            jobRepo.getJobData(RegisteredJobList)
                        }
                        jobListArrayList = deferredJobData.await()

                        activity?.runOnUiThread {
                            jobAdapter.setJobList(jobListArrayList)
                            jobAdapter.notifyDataSetChanged()
                        }
                    }
                }


            } else {
                // Handle error getting documents
                Toast.makeText(requireContext(), "Error getting documents.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}