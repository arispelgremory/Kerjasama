package com.gremoryyx.kerjasama

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.gremoryyx.kerjasama.repository.CourseRepository
import com.gremoryyx.kerjasama.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_COURSE_DATA = "courseData"

class CourseDetailFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    var courseRepo = CourseRepository()
    var userRepo = UserRepository()
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
        readCourseDetails(data)

        val backButton = getView()?.findViewById<ImageButton>(R.id.courseDetailBackBtn)
        backButton?.setOnClickListener {
            val courseFragment = CourseFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, courseFragment)
                .addToBackStack(null)
                .commit()

//            val fragmentManager = requireActivity().supportFragmentManager
//            fragmentManager.popBackStack()
        }

        val applyButton = getView()?.findViewById<Button>(R.id.courseDetailApplyBtn)
        applyButton?.setOnClickListener {
            applyCourse(data)
            val courseFragment = CourseFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, courseFragment)
                .addToBackStack(null)
                .commit()
//            val fragmentManager = requireActivity().supportFragmentManager
//            fragmentManager.popBackStack()
        }

    }

    fun applyCourse(courseData: CourseData?) {
        db = FirebaseFirestore.getInstance()
        val regCourseRef = db.collection("Registered Course")
        var courseDoc = ""
        // I want to compare the course name and description
        val currCourseName = courseData?.courseName.toString()
        val currCourseDesc = courseData?.courseDescription.toString()
        CoroutineScope(Dispatchers.IO).launch {
            courseRepo.validateDocument(currCourseName, currCourseDesc)
            if (courseData != null) {
                courseDoc = withContext(Dispatchers.IO) {
                    courseRepo.validateDocument(courseData.courseName, courseData.courseDescription)

                }
            }
            val currUserRef = userRepo.getUserRef().path
            Log.d("APPLY BUTTON", "User: $currUserRef")

            if (courseDoc != "" && currUserRef != ""){
                val docFormat_courseDoc: DocumentReference = db.document(courseDoc)
                val docFomat_currUserRef: DocumentReference = db.document(currUserRef)
                //ADD DATA TO REGISTERED JOB
                regCourseRef.add(hashMapOf(
                    "course" to docFormat_courseDoc,
                    "lectures_watched" to 0,
                    "user" to docFomat_currUserRef
                ))
            }
        }
    }

    fun readCourseDetails(data: CourseData?) {
        val courseImage = getView()?.findViewById<ImageView>(R.id.courseDetailImageView)
        val courseName = getView()?.findViewById<TextView>(R.id.courseDetailName)
        val courseDesc = getView()?.findViewById<TextView>(R.id.courseDetailDescription)
        val courseRate = getView()?.findViewById<TextView>(R.id.courseDetailRating)
        val courseRatingBar = getView()?.findViewById<RatingBar>(R.id.courseDetailRatingBar)
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
        data?.ratingNumber.let { courseRatingBar?.rating = it!!.toFloat()}
        data?.usersRated?.let { courseUserRate?.setText("Total " + it.toString() + " Users Rated") }
        data?.instructorName?.let { courseInstructor?.setText("Offered by " + it) }
        data?.lastUpdate?.let { courseLastUpdate?.setText("Last Updated " + it) }


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

           if (learnList.indexOf(numLearn) == 4) {
               view?.findViewById<LinearLayout>(R.id.courseDetailFifthListLayout)?.visibility = View.VISIBLE
           }else if (learnList.indexOf(numLearn) == 3){
               view?.findViewById<LinearLayout>(R.id.courseDetailFourthListLayout)?.visibility = View.VISIBLE
            }else if (learnList.indexOf(numLearn) == 2){
               view?.findViewById<LinearLayout>(R.id.courseDetailThirdListLayout)?.visibility = View.VISIBLE
            }else if (learnList.indexOf(numLearn) == 1){
               view?.findViewById<LinearLayout>(R.id.courseDetailSecondListLayout)?.visibility = View.VISIBLE
            }else if (learnList.indexOf(numLearn) == 0){
               view?.findViewById<LinearLayout>(R.id.courseDetailFirstListLayout)?.visibility = View.VISIBLE
            }

        }


    }
}