package com.gremoryyx.kerjasama.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.core.RepoManager.resume
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.gremoryyx.kerjasama.R
import com.gremoryyx.kerjasama.RegisteredJobData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.*

class JobRepository {
    var jobImg_bitmap: Bitmap
    lateinit var db: FirebaseFirestore
    lateinit var jobRef: CollectionReference
    lateinit var jobDocument: DocumentReference

    init {
        jobImg_bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    suspend fun getCollection(){
        jobRef = db.collection("Job")
    }

    suspend fun validateDocument(collection:CollectionReference, jobName: String, companyName: String): String = suspendCoroutine{ continuation ->
        var jobDoc = ""
        collection.get().addOnSuccessListener { documents ->
            for (document in documents) {
                if (document.data["job_name"] == jobName && document.data["company"] == companyName){
                    jobDoc = document.reference.path
                }
            }
            continuation.resume(jobDoc)
        }.addOnFailureListener{
            Log.d("${it}", "FAILED: ${it.message}")
            continuation.resumeWithException(it)
        }

    }

    suspend fun validateDocument(collection:CollectionReference, jobRefPath: DocumentReference): Boolean = suspendCoroutine{ continuation ->
        collection.get().addOnSuccessListener { documents ->
            if (collection.document("${jobRefPath.id}") != null){
                continuation.resume(true)
            } else {
                continuation.resume(false)
            }
        }.addOnFailureListener{
            Log.d("${it}", "FAILED: ${it.message}")
            continuation.resumeWithException(it)
        }

    }



    fun getImageFile(jobImage: String): Task<Bitmap> {
        val jobStorageRef = Firebase.storage("gs://kerjasama-676767.appspot.com").reference.child("Job")
        val jobImgRef = jobStorageRef.child("${jobImage}.jpg")
        Log.d("IMAGE#######", "getImageFile: ${jobImgRef.toString()}")
        return jobImgRef.getBytes(Long.MAX_VALUE)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!

                }else{
                    Log.d("getImageFailed!!!!", "getImageFile: ${task.result}")
                }
                val data = task.result
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                jobImg_bitmap = bitmap
                Tasks.forResult(bitmap)
            }
    }

    suspend fun getData(doc:DocumentReference): RegisteredJobData = suspendCoroutine{ continuation ->
        CoroutineScope(Dispatchers.IO).launch {
            db = FirebaseFirestore.getInstance()
            jobRef = db.collection("Job")
            jobRef.document("${doc.id}").get().addOnSuccessListener { documents ->
                var regJobData = RegisteredJobData()
                val jobImage = documents.data?.get("job_image") as String
                CoroutineScope(Dispatchers.IO).launch {
                    val bitmap = getImageFile(jobImage).await()
                    regJobData.jobImage = bitmap
                    regJobData.jobName = documents.data?.get("job_name") as String
                    regJobData.companyName = documents.data?.get("company") as String
                    regJobData.jobDescription = documents.data?.get("job_description") as String
                    regJobData.jobType = documents.data?.get("job_type") as String
                    regJobData.location = documents.data?.get("location") as String
                    regJobData.duration = documents.data?.get("work_duration") as String
                    regJobData.salary = documents.data?.get("salary") as String
                    regJobData.walfares.clear()

                    continuation.resume(regJobData)
                }


            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        }
    }


}