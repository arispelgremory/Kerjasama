package com.gremoryyx.kerjasama

import android.os.Bundle
import android.provider.MediaStore
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
import android.provider.MediaStore.Video
import android.provider.MediaStore.Files

class CourseListFragment : Fragment(), JobSearchListener {
    private lateinit var courseListRecyclerView: RecyclerView
    private lateinit var courseListArrayList: ArrayList<CourseData>
    private lateinit var courseListAdapter: CourseListAdapter
    private lateinit var defaultCourseList: ArrayList<CourseData>

    private lateinit var db: FirebaseFirestore
    var loginRepo = LoginRepository()
//    var courseRepo = CourseRepository()
//    var regisFrag = RegisteredCourseFragment()
    var userRepo = UserRepository()

    fun filterCourseList(filteredCourseList: ArrayList<CourseData>) {
        courseListAdapter.updateCourseList(filteredCourseList)
    }

    fun getCourseList(): ArrayList<CourseData> {
        return courseListArrayList
    }

    fun updateCourseList(newList: ArrayList<CourseData>) {
        courseListArrayList.clear()
        courseListAdapter.setCourseList(newList)
        courseListAdapter.notifyDataSetChanged()
    }

    fun resetCourseList() {
        courseListAdapter.setCourseList(defaultCourseList)
        courseListAdapter.notifyDataSetChanged()
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
        courseRef.get().addOnCompleteListener { task->
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.IO).launch {
                    for (document in task.result!!) {
                        val courseData = CourseData()

                        val courseImage = document.data["course_image"].toString()
                        val bitmap = courseRepo.getImageFile(courseImage).await()
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
                        for (lectureVideosData in lectureVideos as ArrayList<Video>) {
                            courseData.lectureVideos.add(lectureVideosData)
                        }

                        courseData.courseMaterials.clear()
                        var courseMaterials = document.data["course_materials"]
                        for (courseMaterialsData in courseMaterials as ArrayList<Files>) {
                            courseData.courseMaterials.add(courseMaterialsData)
                        }

                        courseListArrayList.add(courseData)
                    }

                    activity?.runOnUiThread {
                        courseListAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                Toast.makeText(context, "Failed to load course list", Toast.LENGTH_SHORT).show()
            }
        }
    }
}