package com.gremoryyx.kerjasama

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.gremoryyx.kerjasama.repository.LoginRepository
import com.gremoryyx.kerjasama.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import com.gremoryyx.kerjasama.repository.CourseRepository
import kotlinx.coroutines.async

class CourseListFragment : Fragment(), JobSearchListener {
    private lateinit var courseListRecyclerView: RecyclerView
    private lateinit var courseListArrayList: ArrayList<CourseData>
    private lateinit var courseListAdapter: CourseListAdapter
    private lateinit var defaultCourseList: ArrayList<CourseData>

    private lateinit var db: FirebaseFirestore
    var loginRepo = LoginRepository()
    var courseRepo = CourseRepository()
//    var regisFrag = RegisteredCourseFragment()
    var userRepo = UserRepository()

    fun updateCourseList(newList: ArrayList<CourseData>) {
        courseListAdapter.setCourseList(newList)
        courseListAdapter.notifyDataSetChanged()
    }

    fun resetCourseList() {
        courseListArrayList.clear()
        CoroutineScope(Dispatchers.IO).launch {
            courseListLoadCourse()
        }
        courseListRecyclerView.scrollToPosition(0)
    }

    override fun onSearchInput(newText: String) {
        val filteredCourseList = courseListArrayList.filter { course ->
            course.courseName.contains(newText, true)
        }
        updateCourseList(filteredCourseList as ArrayList<CourseData>)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_course_list, container, false)

        courseListRecyclerView = view.findViewById(R.id.course_list_recycler_view)
        courseListRecyclerView.layoutManager = LinearLayoutManager(context)
        courseListRecyclerView.setHasFixedSize(true)

        courseListArrayList = ArrayList()
        courseListAdapter = CourseListAdapter(courseListArrayList)
        courseListRecyclerView.adapter = courseListAdapter

        defaultCourseList = ArrayList(courseListArrayList)

        if (loginRepo.validateUser()) {
            CoroutineScope(Dispatchers.IO).launch {
                courseListLoadCourse()
            }
        }
        else {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
        }

        // Card onClick
        courseListAdapter.setOnCardViewClickListener { courseData ->

            // TODO: Replace fragment
        }

        return view
    }

    private suspend fun courseListLoadCourse() {
        db = FirebaseFirestore.getInstance()
        val courseRef = db.collection("Course")
        var CourseRegisteredList = ArrayList<String>()
        courseRef.get().addOnCompleteListener { task ->
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
                                    CourseRegisteredList.add(courseRepo.checkCourseRegistered(document.id))
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
                            courseRepo.getCourseData(CourseRegisteredList)
                        }
                        courseListArrayList = deferredJobData.await()

                        activity?.runOnUiThread {
                            courseListAdapter.setCourseList(courseListArrayList)
                            courseListAdapter.notifyDataSetChanged()
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