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
import com.google.firebase.firestore.ktx.firestore
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
    private lateinit var auth: FirebaseAuth

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
        val user = Firebase.auth.currentUser
        val NotificationRef = db.collection("Notification")

        NotificationRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                CoroutineScope(Dispatchers.IO).launch {
                    for (document in task.result!!) {
                        if (user!!.uid == (document.data["user"] as DocumentReference).id){
                            val notificationData = NotificationData()
                            notificationData.messages = document.data["message"] as String?

                            // Use CoroutineScope to wait for the image to be retrieved
                            val companyDoc = document.data["company"] as DocumentReference
                            val cmpy_doc = Firebase.firestore.collection("Company").document("${companyDoc.id}")
                            val cmpy_phone = cmpy_doc.get().await().data?.get("phonenumber") as String
                            val bitmap = companyRepository.getImageFile(cmpy_phone).await()
                            notificationData.companyImg = bitmap

                            notificationDataArrayList.add(notificationData)
                        }

                    }
                    activity?.runOnUiThread {
                        notificationAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                // Handle error getting documents
                Toast.makeText(requireContext(), "Error getting documents.", Toast.LENGTH_SHORT).show()
            }
        }
    }



}




