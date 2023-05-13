package com.gremoryyx.kerjasama.repository

import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gremoryyx.kerjasama.CourseData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CourseRepository {
    var courseImgBitmap: Bitmap
    private var db = FirebaseFirestore.getInstance()
    lateinit var courseRef: CollectionReference

    init {
        courseImgBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    fun getImageFile(courseImage: String): Task<Bitmap> {
        val courseStorageRef = Firebase.storage("gs://kerjasama-676767.appspot.com").reference.child("Course")
        val courseImgRef = courseStorageRef.child("${courseImage}.jpg")
        Log.d("IMAGE########", "getImageFile: ${courseImgRef.toString()}")
        return courseImgRef.getBytes(Long.MAX_VALUE)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                } else {
                    Log.d("Get Image Failed!!!", "getImageFile: ${task.result}")
                }
                val data = task.result
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(data, 0, data!!.size)
                courseImgBitmap = bitmap
                com.google.android.gms.tasks.Tasks.forResult(bitmap)
            }
    }

    suspend fun getCourseData(regCourseID: ArrayList<String>): ArrayList<CourseData> = suspendCoroutine { continuation ->
        val jobRef = db.collection("Job")
        val filteredCourseArrayList = ArrayList<CourseData>()
        Log.d("GETTING COURSE DATA", "TESTING")
        jobRef.get().addOnSuccessListener { documents ->
            CoroutineScope(Dispatchers.IO).async {
                val displayRegJob = async {
                    for (document in documents) {
                        if (!regCourseID.contains(document.id)) {
                            Log.d("ADD THE DATA INTO ARRAY LIST: ", "ADDING")
                            val courseImage = document.data["course_image"].toString()
                            // Suspend the current coroutine and wait for the image retrieval
                            val bitmap = getImageFile(courseImage).await()
                            val courseData = CourseData()
                            courseData.courseImage = bitmap
                            courseData.courseName = document.data["course_name"].toString()
                            courseData.courseDescription = document.data["course_description"].toString()
                            courseData.instructorName = document.data["instructor_name"].toString()
                            courseData.ratingNumber = document.data["rating_number"].toString().toFloat()
                            courseData.usersRated = document.data["users_rated"].toString().toInt()
                            courseData.lastUpdate = document.data["last_update"].toString()

                            courseData.itemsToLearn.clear()
                            var itemsToLearn = document.data["items_to_learn"]
                            for (itemsToLearnData in itemsToLearn as ArrayList<String>) {
                                courseData.itemsToLearn.add(itemsToLearnData)
                            }

                            courseData.lectureVideos.clear()
                            var lectureVideos = document.data["lecture_videos"]
                            for (lectureVideosData in lectureVideos as ArrayList<MediaStore.Video>) {
                                courseData.lectureVideos.add(lectureVideosData)
                            }

                            courseData.courseMaterials.clear()
                            var courseMaterials = document.data["course_materials"]
                            for (courseMaterialsData in courseMaterials as ArrayList<MediaStore.Files>) {
                                courseData.courseMaterials.add(courseMaterialsData)
                            }
                            filteredCourseArrayList.add(courseData)
                        }
                    }
                    continuation.resume(filteredCourseArrayList)
                }
                displayRegJob.await()
            }

        }.addOnFailureListener { exception ->
            Log.d("COURSE DATA", "Error getting documents: ", exception)
            // Resume the continuation with an empty list or handle the failure case
            continuation.resume(ArrayList())
        }
    }

    suspend fun checkCourseRegistered(checkCourseID: String): String = suspendCoroutine { continuation ->
        val userRepo = UserRepository()
        val currentUserID = userRepo.getUserID()
        val courseRegRef = db.collection("Registered Course")
        courseRegRef.get().addOnSuccessListener {
            for (doc in it) {
                if ((doc.data["user"] as DocumentReference).id == currentUserID){
                    //Get the matches job field id
                    val courseID = (doc.data["course"] as DocumentReference).id

                    if (courseID == checkCourseID){
                        //If the job is already registered, then I don't need add into the job list
                        continuation.resume(courseID)
                    }
                }
                Log.d("WAITING THE FILTERING", "${doc.id}")
            }
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }
}