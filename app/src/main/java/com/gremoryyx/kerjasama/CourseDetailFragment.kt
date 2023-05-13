package com.gremoryyx.kerjasama

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.gremoryyx.kerjasama.repository.CourseRepository

private const val ARG_COURSE_DATA = "courseData"
private var courseData = CourseData()

class CourseDetailFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    var courseRepo = CourseRepository()
    var currentCourse = CourseData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_course_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Retrieve course details
        val data = arguments?.getParcelable<CourseData>(ARG_COURSE_DATA)
        Log.d("PASSING COURSE DATA FROM LIST!!!!!!!", "onViewCreated: $data")
        Log.d("PASSING COURSE DATA FROM LIST!!!!!!!", "Course Name: ${data?.courseName}")
        Log.d("PASSING COURSE DATA FROM LIST!!!!!!!", "Course Image: ${data?.courseImage}")
        readCourseDetails(data)

        val backButton = getView()?.findViewById<ImageButton>(R.id.courseDetailBackBtn)
        backButton?.setOnClickListener {
//            val courseFragment = CourseFragment()
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.wholeCourseDetailLayout, courseFragment)
//                .addToBackStack(null)
//                .commit()
            val courseFragment = CourseFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

    }

    fun readCourseDetails(data: CourseData?) {
        val courseImage = getView()?.findViewById<ImageView>(R.id.courseDetailImageView)
        val courseName = getView()?.findViewById<TextView>(R.id.courseDetailName)
        val courseDesc = getView()?.findViewById<TextView>(R.id.courseDetailDescription)
        val courseRate = getView()?.findViewById<TextView>(R.id.courseDetailRating)
        val courseUserRate = getView()?.findViewById<TextView>(R.id.courseDetailUserRated)
        val courseInstructor = getView()?.findViewById<TextView>(R.id.courseDetailInstructor)
        val courseLastUpdate = getView()?.findViewById<TextView>(R.id.courseDetailLastUpdateTextView)
        val courseLang= getView()?.findViewById<TextView>(R.id.courseDetailLangTextView)
        val courseCC= getView()?.findViewById<TextView>(R.id.courseDetailCCTextView)
        val courseFirstList = getView()?.findViewById<TextView>(R.id.courseDetailFirstList)
        val courseSecList = getView()?.findViewById<TextView>(R.id.courseDetailSecondList)
        val courseThirdList = getView()?.findViewById<TextView>(R.id.courseDetailThirdList)
        val courseFourthList = getView()?.findViewById<TextView>(R.id.courseDetailFourthList)
        val courseFifthList = getView()?.findViewById<TextView>(R.id.courseDetailFifthList)

        data?.courseImage?.let { courseImage?.setImageBitmap(it)}
        data?.courseName?.let { courseName?.setText(it) }
        data?.courseDescription?.let { courseDesc?.setText(it) }
        data?.ratingNumber?.let { courseRate?.setText(it.toString()) }
        data?.usersRated?.let { courseUserRate?.setText(it.toString()) }
        data?.instructorName?.let { courseInstructor?.setText(it) }
        data?.lastUpdate?.let { courseLastUpdate?.setText(it) }


        val LanguageList = data?.language!!
        var allLang = ""
        for (numLang in LanguageList){
            if (LanguageList.indexOf(numLang) == LanguageList.size-1){
                allLang += numLang
            }else{
                allLang += numLang+", "
            }
        }
        courseLang?.text = allLang
        val CaptionList = data?.captions!!
        var allCaption = ""
        for (numCC in CaptionList){
            if (CaptionList.indexOf(numCC) == CaptionList.size-1){
                allCaption += numCC
            }else{
                allCaption += numCC+", "
            }
        }
        courseCC?.text = allCaption

        val learnList = data?.itemsToLearn!!
        for (numLearn in learnList){

            if (learnList.indexOf(numLearn) == 0){
                courseFirstList?.text = numLearn
            }else if (learnList.indexOf(numLearn) == 1){
                courseSecList?.text = numLearn
            }else if (learnList.indexOf(numLearn) == 2){
                courseThirdList?.text = numLearn
            }else if (learnList.indexOf(numLearn) == 3){
                courseFourthList?.text = numLearn
            }else if (learnList.indexOf(numLearn) == 4) {
                courseFifthList?.text = numLearn
            }

           if (learnList.indexOf(numLearn) == CaptionList.size-2) {
               view?.findViewById<LinearLayout>(R.id.courseDetailFifthListLayout)?.visibility = View.GONE
           }else if (learnList.indexOf(numLearn) == CaptionList.size-3){
               view?.findViewById<LinearLayout>(R.id.courseDetailFourthListLayout)?.visibility = View.GONE
            }else if (learnList.indexOf(numLearn) == CaptionList.size-4){
               view?.findViewById<LinearLayout>(R.id.courseDetailThirdListLayout)?.visibility = View.GONE
            }else if (learnList.indexOf(numLearn) == CaptionList.size-5){
               view?.findViewById<LinearLayout>(R.id.courseDetailSecondListLayout)?.visibility = View.GONE
            }else if (learnList.indexOf(numLearn) == CaptionList.size-6){
               view?.findViewById<LinearLayout>(R.id.courseDetailFirstListLayout)?.visibility = View.GONE
            }

        }


    }
}