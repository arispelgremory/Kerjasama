package com.gremoryyx.kerjasama

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.gremoryyx.kerjasama.repository.CompanyRepository
import com.gremoryyx.kerjasama.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationDataArrayList: ArrayList<NotificationData>
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var db: FirebaseFirestore

    private var companyRepository = CompanyRepository()
    var loginRepo = LoginRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_notification_list, container, false)

        recyclerView = view.findViewById(R.id.notificationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        notificationDataArrayList = ArrayList()
        notificationAdapter = NotificationAdapter(notificationDataArrayList)


        if (loginRepo.validateUser()) {
            CoroutineScope(Dispatchers.IO).launch {
                getItemData()
            }
        } else {
            Toast.makeText(requireContext(), "Please Login First", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = notificationAdapter
        return view
    }

    private suspend fun getItemData() {
        db = FirebaseFirestore.getInstance()
        val NotificationRef = db.collection("Notification")
        NotificationRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.IO).launch {
                    for (document in task.result!!) {
                        val notificationData = NotificationData()
                        notificationData.messages = document.data["message"] as String?

                        // Use CoroutineScope to wait for the image to be retrieved
                        val companyName = document.data["company"] as String
                        Log.d("Company NAME", companyName)
                        val bitmap = companyRepository.getImageFile(companyName).await()
                        Log.d("bitmap Noti:###", "${bitmap}")
                        notificationData.companyImg = bitmap

                        notificationDataArrayList.add(notificationData)
                    }
                    activity?.runOnUiThread {
                        notificationAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                // Handle error getting documents
                Log.w(TAG, "Error getting documents.", task.exception)
                Toast.makeText(requireContext(), "Error getting documents.", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    private fun getItemData(auth: FirebaseUser?) {
//
//        db = FirebaseFirestore.getInstance()
//        val NotificationRef = db.collection("Notification")
//        NotificationRef.get().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                for (document in task.result!!) {
//                    val notificationData = NotificationData()
//
//                    notificationData.messages = document.data["messages"] as String?
//                    companyRepository.getImageFile(document.data["company"] as String)
//                    notificationData.companyImg = companyRepository.companyImg_bitmap
//                    notificationDataArrayList.add(notificationData)
//                }
//                notificationAdapter.notifyDataSetChanged() // Notify adapter that data has changed
//            } else {
//                // Handle error getting documents
//                Log.w(TAG, "Error getting documents.", task.exception)
//            }
//        }
//
//        recyclerView.adapter = notificationAdapter
//    }



}




