package com.gremoryyx.kerjasama

import android.os.Bundle
import android.util.Log
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
import com.gremoryyx.kerjasama.repository.UserRepository
import kotlinx.coroutines.*

class CourseRegisteredListFragment : Fragment(), SearchListener {
    private lateinit var courseRegisteredListRecyclerView: RecyclerView
    private lateinit var courseRegisteredArrayList: ArrayList<CourseData>
    private lateinit var courseRegisteredListAdapter: CourseRegisteredListAdapter
    private lateinit var originalCourseRegisteredList: ArrayList<CourseData>

    private lateinit var db: FirebaseFirestore
    var loginRepo = LoginRepository()
    var courseRepo = CourseRepository()
    var userRepo = UserRepository()

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_course_registered_list, container, false)

        courseRegisteredListRecyclerView =
            view.findViewById(R.id.course_registered_list_recycler_view)
        courseRegisteredListRecyclerView.layoutManager = LinearLayoutManager(context)
        courseRegisteredListRecyclerView.setHasFixedSize(true)

        courseRegisteredArrayList = ArrayList()
        courseRegisteredListAdapter = CourseRegisteredListAdapter(courseRegisteredArrayList)
        courseRegisteredListRecyclerView.adapter = courseRegisteredListAdapter
        //courseRegisteredListAdapter.notifyDataSetChanged()

        originalCourseRegisteredList = ArrayList(courseRegisteredArrayList)

        if (loginRepo.validateUser()) {
            CoroutineScope(Dispatchers.IO).launch {
                courseRegisteredLoadCourses()
            }
        } else {
            Toast.makeText(context, "Login First", Toast.LENGTH_LONG).show()
        }

        courseRegisteredListAdapter.setOnCardViewClickListener { courseData ->
            Log.d("RICKROLL", courseData.toString())

            val watchLectureFragment = CourseWatchLectureFragment()
            var args = Bundle()
            args.putParcelable("courseData", courseData)
            watchLectureFragment.arguments = args
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, watchLectureFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private suspend fun courseRegisteredLoadCourses() {
        db = FirebaseFirestore.getInstance()
        var regCourseData = ArrayList<String>()
        val reg_courseRef = db.collection("Registered Course")
        val courseRef = db.collection("Course")

        reg_courseRef.get().addOnCompleteListener { Task ->
            if (Task.isSuccessful) {
                for (document in Task.result!!) {
                    if (document.data["user"] == userRepo.getUserRef()) {
                        val from_courseRef = document.data["course"] as DocumentReference
                        CoroutineScope(Dispatchers.IO).launch {
                            val retrieveRegCourse = async {
                                courseRepo.getCourseRegisteredData(
                                    courseRef.document(from_courseRef.id),
                                    (document.data["lectures_watched"] as Long).toInt()
                                )
                            }
                            courseRegisteredArrayList = retrieveRegCourse.await()

                            activity?.runOnUiThread {
                                courseRegisteredListAdapter.setCourseRegisteredList(
                                    courseRegisteredArrayList
                                )
                                courseRegisteredListAdapter.notifyDataSetChanged()
                            }

//                            if (retrieveRegCourse.isCompleted) {
//                                Log.d("IS COMPLETED", "retrieve: DONE")
//                                courseRegisteredArrayList.add(retrieveRegCourse.getCompleted())
//                                Log.d("IS COMPLETED", "retrieved data: $courseRegisteredArrayList")
//                            }
                        }
                    }
                }
            }
        }
    }
}