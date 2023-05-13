package com.gremoryyx.kerjasama

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.gremoryyx.kerjasama.repository.CourseRepository

private const val ARG_COURSE_DATA = "courseData"
private var courseData: CourseData? = null

class CourseDetailFragment : Fragment() {
    private lateinit var courseDetailAdapter: CourseDetailAdapter

    private lateinit var db: FirebaseFirestore
    var courseRepo = CourseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseData = it.getParcelable(ARG_COURSE_DATA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        courseDetailAdapter = CourseDetailAdapter(courseData!!, container!!)
        val view = inflater.inflate(R.layout.fragment_course_detail, container, false)
        Log.d("CourseDetailFragment", "onCreateView: courseData: $courseData")

        courseDetailAdapter.setOnBackButtonClickListenerLambda {
            val courseFragnent = CourseFragment()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.courseDetailScrollView, courseFragnent)
                .addToBackStack(null)
                .commit()
        }

        courseDetailAdapter.bind(courseData!!)

        return view
    }
}