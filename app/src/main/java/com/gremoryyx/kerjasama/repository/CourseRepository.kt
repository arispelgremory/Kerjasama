package com.gremoryyx.kerjasama.repository

import android.graphics.Bitmap
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CourseRepository {
    var courseImgBitmap: Bitmap
    private var db = FirebaseFirestore.getInstance()
    lateinit var courseRef: CollectionReference
    var temp = ArrayList<CourseData>()

    init {
        courseImgBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    fun getImageFile(courseImage: DocumentReference): Task<Bitmap> {
        val courseStorageRef = Firebase.storage("gs://kerjasama-676767.appspot.com").reference.child("Course")
        val courseImgRef = courseStorageRef.child("${courseImage.id}")
        Log.d("IMAGE########", "getImageFile: ${courseImgRef}")
        return courseImgRef.getBytes(Long.MAX_VALUE)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                } else {
                    Log.d("Get Image Success!!!", "getImageFile: ${task.result}")
                }
                val data = task.result
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(data, 0, data!!.size)
                courseImgBitmap = bitmap
                com.google.android.gms.tasks.Tasks.forResult(bitmap)
            }
    }

    suspend fun getCourseData(regCourseID: ArrayList<String>): ArrayList<CourseData> = suspendCoroutine { continuation ->
        val courseRef = db.collection("Course")
        val filteredCourseArrayList = ArrayList<CourseData>()
        Log.d("GETTING COURSE DATA", "TESTING")
        courseRef.get().addOnSuccessListener { documents ->
            CoroutineScope(Dispatchers.IO).async {
                val displayRegJob = async {
                    for (document in documents) {
                        if (!regCourseID.contains(document.id)) {
                            Log.d("ADD THE DATA INTO ARRAY LIST: ", "ADDING")
                            val courseImage = document.data["course_image"] as DocumentReference
                            // Suspend the current coroutine and wait for the image retrieval
                            val bitmap = getImageFile(courseImage).await()
                            val courseData = CourseData()
                            courseData.courseImage = bitmap

                            courseData.courseName = document.data["course_name"].toString()
                            courseData.courseDescription = document.data["course_description"].toString()
                            courseData.instructorName = document.data["instructor_name"].toString()
                            courseData.ratingNumber = document.data["rating_number"].toString().toFloat()
                            courseData.usersRated = document.data["user_rated"].toString().toInt()
                            courseData.lastUpdate = document.data["last_update"].toString()

                            courseData.language.clear()
                            var language = document.data["language"]
                            for (languageData in language as ArrayList<String>) {
                                courseData.language.add(languageData)
                            }

                            courseData.captions.clear()
                            var captions = document.data["captions"]
                            for (captionsData in captions as ArrayList<String>) {
                                courseData.captions.add(captionsData)
                            }

                            courseData.itemsToLearn.clear()
                            var itemsToLearn = document.data["items_to_learn"]
                            for (itemsToLearnData in itemsToLearn as ArrayList<String>) {
                                courseData.itemsToLearn.add(itemsToLearnData)
                            }

                            courseData.lectureVideos.clear()
                            var lectureVideos = document.data["lecture_videos"]
                            for (lectureVideosData in lectureVideos as ArrayList<String>) {
                                courseData.lectureVideos.add(lectureVideosData)
                            }

                            courseData.lectureName.clear()
                            var lectureName = document.data["lecture_name"]
                            for (lecturenameData in lectureName as ArrayList<String>) {
                                courseData.lectureName.add(lecturenameData)
                            }
                            filteredCourseArrayList.add(courseData)
                            Log.d("COURSE DATA", "${document.id} => ${document.data}")
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
                        Log.d("CHECKING REGISTERED COURSE", "$courseID Already Registered")
                        continuation.resume(courseID)
                    }
                }
                Log.d("WAITING THE FILTERING CHECKING REGISTERED COURSE", "${doc.id}")
            }
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }

    suspend fun validateDocument(courseName: String, courseDesc: String): String= suspendCoroutine { continuation ->
        var courseRef = ""
        db.collection("Course").get().addOnSuccessListener {
            for (doc in it){
                if (doc.data["course_name"].toString() == courseName && doc.data["course_description"].toString() == courseDesc){
                    Log.d("VALIDATE DOCUMENT", "validateDocument: ${doc.reference.path}")
                    courseRef = doc.reference.path
                }
            }
            continuation.resume(courseRef)
        }.addOnFailureListener{
            Log.d("VALIDATE DOCUMENT", "validate Course Failed: ${it}")
            continuation.resumeWithException(it)
        }
    }
    suspend fun validateDocument(courseCollection:CollectionReference, courseRefPath: DocumentReference): Boolean = suspendCoroutine { continuation ->
        var courseToDisplay: DocumentReference
        courseCollection.get().addOnSuccessListener {
            if (courseCollection.document("${courseRefPath.id}") != null){
                continuation.resume(true)
            } else {
                continuation.resume(false)
            }
        }.addOnFailureListener {
            Log.d("${it}", "FAILED: ${it.message}")
            continuation.resumeWithException(it)
        }
    }

    suspend fun getCourseRegisteredData(doc: DocumentReference, watched_lecture:Int): ArrayList<CourseData> = suspendCoroutine { continuation ->

        CoroutineScope(Dispatchers.IO).launch {
            courseRef = db.collection("Course")
            courseRef.document("${doc.id}").get().addOnSuccessListener { documents ->
                var regCourseData = CourseData()
                val courseImage = documents.data?.get("course_image") as DocumentReference

                CoroutineScope(Dispatchers.IO).launch {
                    val waitingData = async {
                        val bitmap = getImageFile(courseImage).await()
                        // Suspend the current coroutine and wait for the image retrieval
                        regCourseData.courseImage = bitmap

                        regCourseData.courseName = documents.data?.get("course_name").toString()
                        regCourseData.courseDescription = documents.data?.get("course_description").toString()
                        regCourseData.instructorName = documents.data?.get("instructor_name").toString()
                        regCourseData.ratingNumber = documents.data?.get("rating_number").toString().toFloat()
                        regCourseData.usersRated = documents.data?.get("user_rated").toString().toInt()
                        regCourseData.lastUpdate = documents.data?.get("last_update").toString()
                        regCourseData.lecturesWatched = watched_lecture
                        regCourseData.language.clear()
                        var language = documents.data?.get("language")
                        for (languageData in language as ArrayList<String>) {
                            regCourseData.language.add(languageData)
                        }

                        regCourseData.captions.clear()
                        var captions = documents.data?.get("captions")
                        for (captionsData in captions as ArrayList<String>) {
                            regCourseData.captions.add(captionsData)
                        }

                        regCourseData.itemsToLearn.clear()
                        var itemsToLearn = documents.data?.get("items_to_learn")
                        for (itemsToLearnData in itemsToLearn as ArrayList<String>) {
                            regCourseData.itemsToLearn.add(itemsToLearnData)
                        }
                    }
                    waitingData.await()

                    temp.add(regCourseData)
                    Log.d("getCourseRegisteredData DATA", "After GET DATA: $regCourseData")
                    continuation.resume(temp)
                }


            }

        }
    }


}