package com.gremoryyx.kerjasama.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.gremoryyx.kerjasama.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CompanyRepository {
    var companyImg_bitmap: Bitmap
    lateinit var db: FirebaseFirestore
    lateinit var jobRef: CollectionReference
    lateinit var jobDocument: DocumentReference

    init {
        companyImg_bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    suspend fun getCollection(){
        jobRef = db.collection("Company")
    }

    suspend fun getDocument(collection: CollectionReference){
        collection.get().addOnSuccessListener { documents ->
            for (document in documents) {
                jobDocument = document.reference
            }
        }
    }

    fun getImageFile(phonenumber: String): Task<Bitmap> {
        val companyStorageRef = Firebase.storage("gs://kerjasama-676767.appspot.com").reference.child("Company")
        val companyImgRef = companyStorageRef.child("${phonenumber}.jpg")
        return companyImgRef.getBytes(Long.MAX_VALUE)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                val data = task.result
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                companyImg_bitmap = bitmap
                Tasks.forResult(bitmap)
            }
    }


//    fun getCompanyData(): DocumentReference{
//        CoroutineScope(Dispatchers.IO).launch {
//            getCollection()
//            getDocument(jobRef)
//        }
//        return jobDocument
//    }

}