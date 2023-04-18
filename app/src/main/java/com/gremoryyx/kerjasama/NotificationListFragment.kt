package com.gremoryyx.kerjasama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class NotificationListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationDataArrayList: ArrayList<NotificationData>
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_notification_list, container, false)

        recyclerView = view.findViewById(R.id.notificationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        notificationDataArrayList = arrayListOf()
        notificationAdapter = NotificationAdapter(notificationDataArrayList)

        getItemData()

        return view
    }

    private fun getItemData() {
        db = FirebaseFirestore.getInstance()
        db.collection("Notification")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val notificationData = NotificationData()
                        notificationData.companyImg = document.data["companyImg"] as Int?
                        notificationData.messages = document.data["messages"] as String?
                        notificationDataArrayList.add(notificationData)
                    }
                    recyclerView.adapter = notificationAdapter
                } else {
                    // Handle error getting documents
                }
            }
    }
}
