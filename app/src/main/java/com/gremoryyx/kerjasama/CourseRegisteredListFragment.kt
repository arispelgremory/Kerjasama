package com.gremoryyx.kerjasama

import android.os.Bundle
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
import com.gremoryyx.kerjasama.repository.CourseRepository
import com.gremoryyx.kerjasama.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseRegisteredListFragment : Fragment(), SearchListener {
    private lateinit var courseRegisteredListRecyclerView: RecyclerView
    private lateinit var courseRegisteredArrayList: ArrayList<CourseData>
    private lateinit var courseRegisteredListAdapter: CourseRegisteredListAdapter
    private lateinit var originalCourseRegisteredList: ArrayList<CourseData>

    private lateinit var db: FirebaseFirestore
    var loginRepo = LoginRepository()
    var courseRepo = CourseRepository()

    override fun onResume() {
        super.onResume()
        if (loginRepo.validateUser()) {
            CoroutineScope(Dispatchers.IO).launch {
                courseRegisteredLoadCourses()
            }
        } else {
            Toast.makeText(context, "Login First", Toast.LENGTH_LONG).show()
        }
    }

    fun updateRegisteredCourseList(newList: ArrayList<CourseData>) {
        courseRegisteredArrayList.clear()
        courseRegisteredListAdapter.setCourseRegisteredList(newList)
        courseRegisteredListAdapter.notifyDataSetChanged()
    }

    override fun onSearchInput(newText: String) {
        val newCourseList = courseRegisteredArrayList.filter { course ->
            course.courseName.contains(newText, true)
        }
        updateRegisteredCourseList(newCourseList as ArrayList<CourseData>)
    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_course_registered_list, container, false)

        courseRegisteredListRecyclerView = view.findViewById(R.id.course_registered_list_recycler_view)
        courseRegisteredListRecyclerView.layoutManager = LinearLayoutManager(context)
        courseRegisteredListRecyclerView.setHasFixedSize(true)

        courseRegisteredArrayList = ArrayList()
        courseRegisteredListAdapter = CourseRegisteredListAdapter(courseRegisteredArrayList)
        courseRegisteredListRecyclerView.adapter = courseRegisteredListAdapter

        originalCourseRegisteredList = ArrayList(courseRegisteredArrayList)

        if (loginRepo.validateUser()) {
            CoroutineScope(Dispatchers.IO).launch {
                courseRegisteredLoadCourses()
            }
        }
        else {
            Toast.makeText(context, "Login First", Toast.LENGTH_LONG).show()
        }
        return view
    }

    private suspend fun courseRegisteredLoadCourses() {
        db = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        val reg_courseRef = db.collection("Course Registered")
        reg_courseRef.get().addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.IO).launch {
                    for (document in task.result!!) {
                        var refCourse = document.data["course"] as DocumentReference
                        var refUser = document.data["user"] as DocumentReference

                        if (refUser.id == user!!.uid) {
                            val courseRef = db.collection("Course")
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.IO) {
                                    if (courseRepo.validateDocument(courseRef, refCourse)) {
                                        var regCourseData = courseRepo.getCourseRegisteredData(refCourse)

                                        courseRegisteredArrayList.add(regCourseData)
                                        withContext(Dispatchers.Main) {
                                            courseRegisteredListAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}