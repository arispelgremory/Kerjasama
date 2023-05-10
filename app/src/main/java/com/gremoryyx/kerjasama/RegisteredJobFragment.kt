package com.gremoryyx.kerjasama

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gremoryyx.kerjasama.repository.JobRepository
import com.gremoryyx.kerjasama.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisteredJobFragment : Fragment(), JobSearchListener {
    private lateinit var registeredJobRecyclerView: RecyclerView
    private lateinit var registeredJobArrayList: ArrayList<RegisteredJobData>
    private lateinit var registeredJobAdapter: RegisteredJobAdapter
    private lateinit var registeredJobBottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var originalJobList: ArrayList<RegisteredJobData>

    private lateinit var db: FirebaseFirestore
    var loginRepo = LoginRepository()
    var jobRepo = JobRepository()

    fun filterJobList(filteredJobList: ArrayList<RegisteredJobData>) {
        registeredJobAdapter.updateJobList(filteredJobList)
    }

    fun getJobList(): ArrayList<RegisteredJobData> {
        return registeredJobArrayList
    }

    fun getJobArrayList(): ArrayList<RegisteredJobData> {
        return registeredJobArrayList
    }

    override fun onResume(){
        super.onResume()
        if (loginRepo.validateUser()){
            CoroutineScope(Dispatchers.IO).launch {
                registeredJobLoadJobs()
            }
        }
        else{
            Toast.makeText(context, "Login First", Toast.LENGTH_LONG).show()
        }
    }

    fun updateJobList(newList: List<RegisteredJobData>) {
        registeredJobArrayList.clear()
        registeredJobAdapter.setJobList(newList)
        registeredJobAdapter.notifyDataSetChanged()
    }

    fun resetJobList() {
        registeredJobAdapter.setJobList(originalJobList)
        registeredJobAdapter.notifyDataSetChanged()
    }

    override fun onSearchInput(newText: String) {
        val newJobList = registeredJobArrayList.filter { job ->
            job.jobName.contains(newText, ignoreCase = true)
        }
        updateJobList(newJobList)
    }

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
        registeredJobRecyclerView.adapter = registeredJobAdapter

        originalJobList = ArrayList(registeredJobArrayList)

        if (loginRepo.validateUser()){
            CoroutineScope(Dispatchers.IO).launch {
                registeredJobLoadJobs()
            }
        }
        else{
            Toast.makeText(context, "Login First", Toast.LENGTH_LONG).show()
        }

        // Cancel Button OnClick
        registeredJobAdapter.setOnCancelButtonClickListenerLambda { job ->
            // To delete the document in the Registered Job Collection
            Toast.makeText(context, "Cancel button clicked", Toast.LENGTH_SHORT).show()
            removeJob(job)
        }

        return view
    }

    private fun removeJob(regJobData: RegisteredJobData){
        val jobRef = db.collection("Job")   // Get the job collection
        val userRef = db.collection("User") // Get the user collection
        val regJobRef = db.collection("Registered Job") // Get the Registered Job collection
        val user = Firebase.auth.currentUser
        val user_email = user!!.email
        var jobDocId = ""

        userRef.get().addOnCompleteListener{ task->
            if (task.isSuccessful){
                CoroutineScope(Dispatchers.IO).launch{
                    for (document in task.result!!){
                        if (document.data["email"] == user_email){
                            jobRef.get().addOnCompleteListener { task->
                                if (task.isSuccessful){
                                    CoroutineScope(Dispatchers.IO).launch {
                                        for (document in task.result!!){
                                            if (document.data["job_name"] == regJobData.jobName && document.data["company"] == regJobData.companyName){
                                                jobDocId = document.id

                                                regJobRef.get().addOnCompleteListener { task->
                                                    if (task.isSuccessful){
                                                        CoroutineScope(Dispatchers.IO).launch {
                                                            for (document in task.result!!){
                                                                if (document.data["job"] == jobRef.document(jobDocId) && document.data["user"] == userRef.document(user.uid)){
                                                                    regJobRef.document(document.id).delete().await()


                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                    activity?.runOnUiThread {
                                        registeredJobArrayList.remove(regJobData)
                                        registeredJobAdapter.notifyDataSetChanged()
                                    }
                                }else{
                                    Log.d("Job!!!!", "Error getting documents: ", task.exception)
                                }
                            }
                        }
                    }
                }
            }else{
                Log.d("User!!!!", "Error getting documents: ", task.exception)
            }
        }


    }

    private suspend fun registeredJobLoadJobs() {
        // Add your job data here
        db = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        val reg_jobRef = db.collection("Registered Job")
        reg_jobRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.IO).launch {
                    for (document in task.result!!) {
                        var resJob = document.data["job"] as DocumentReference
                        var resUser = document.data["user"] as DocumentReference
                        if (resUser.id == user!!.uid) {
                            val jobRef = db.collection("Job")
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.IO) {
                                    if (jobRepo.validateDocument(jobRef, resJob)) {
                                        registeredJobArrayList.clear()
                                        var regJobData = jobRepo.getData(resJob)

                                        registeredJobArrayList.add(regJobData)
                                        withContext(Dispatchers.Main) {
                                            registeredJobAdapter.notifyDataSetChanged()
                                        }

                                    }
                                }
                            }
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