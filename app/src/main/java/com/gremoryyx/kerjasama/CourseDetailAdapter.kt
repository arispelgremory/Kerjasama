//package com.gremoryyx.kerjasama
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.widget.Button
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.RatingBar
//import android.widget.TextView
//
//class CourseDetailAdapter(private val courseData: CourseData, private val parent: Context) {
//    private var onApplyButtonClickListener: ((CourseData) -> Unit)? = null
//    private var onBackButtonClickListener: ((CourseData) -> Unit)? = null
//
//    val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_course_detail, parent, false)
//
//    fun bind(courseData: CourseData) {
//        val courseImageView = view.findViewById<ImageView>(R.id.courseDetailImageView)
//        val courseName = view.findViewById<TextView>(R.id.courseDetailName)
//        val courseDescription = view.findViewById<TextView>(R.id.courseDetailDescription)
//        val courseRatingNum = view.findViewById<TextView>(R.id.courseDetailRating)
//        val courseRatingBar = view.findViewById<RatingBar>(R.id.courseDetailRatingBar)
//        val courseUserRated = view.findViewById<TextView>(R.id.courseDetailUserRated)
//        val instructorName = view.findViewById<TextView>(R.id.courseDetailInstructor)
//        val lastUpdate = view.findViewById<TextView>(R.id.courseDetailLastUpdateTextView)
//        val language = view.findViewById<TextView>(R.id.courseDetailLangTextView)
//        val caption = view.findViewById<TextView>(R.id.courseDetailCCTextView)
//
//        val learnItemOne = view.findViewById<TextView>(R.id.courseDetailFirstList)
//        val learnItemTwo = view.findViewById<TextView>(R.id.courseDetailSecondList)
//        val learnItemThree = view.findViewById<TextView>(R.id.courseDetailThirdList)
//        val learnItemFour = view.findViewById<TextView>(R.id.courseDetailFourthList)
//        val learnItemFive = view.findViewById<TextView>(R.id.courseDetailFifthList)
//
//        val applyButton: Button = view.findViewById(R.id.courseDetailApplyBtn)
//        val backButton: ImageButton = view.findViewById(R.id.courseDetailBackBtn)
//
//        courseImageView.setImageBitmap(courseData.courseImage)
//        courseName.text = courseData.courseName
//        courseDescription.text = courseData.courseDescription
//        courseRatingNum.text = courseData.ratingNumber.toString()
//        courseRatingBar.rating = courseData.ratingNumber
//        courseUserRated.text = courseData.usersRated.toString()
//        instructorName.text = "Offered by " + courseData.instructorName
//        lastUpdate.text = "Last Updated " + courseData.lastUpdate
//
//        val builder = StringBuilder()
//        for (i in 0 until courseData.language.size) {
//            builder.append(courseData.language[i] + " ")
//        }
//        language.text = builder.toString()
//
//        builder.clear()
//        for (i in 0 until courseData.captions.size) {
//            builder.append(courseData.captions[i] + " ")
//        }
//        caption.text = builder.toString()
//
//        learnItemOne.text = courseData.itemsToLearn[0]
//        learnItemTwo.text = courseData.itemsToLearn[1]
//        learnItemThree.text = courseData.itemsToLearn[2]
//        learnItemFour.text = courseData.itemsToLearn[3]
//        learnItemFive.text = courseData.itemsToLearn[4]
//    }
////    private val courseImageView = view.findViewById<ImageView>(R.id.courseDetailImageView)
////    private val courseName = view.findViewById<TextView>(R.id.courseDetailName)
////    private val courseDescription = view.findViewById<TextView>(R.id.courseDetailDescription)
////    private val courseRatingNum = view.findViewById<TextView>(R.id.courseDetailRating)
////    private val courseRatingBar = view.findViewById<RatingBar>(R.id.courseDetailRatingBar)
////    private val courseUserRated = view.findViewById<TextView>(R.id.courseDetailUserRated)
////    private val instructorName = view.findViewById<TextView>(R.id.courseDetailInstructor)
////    private val lastUpdate = view.findViewById<TextView>(R.id.courseDetailLastUpdateTextView)
////    private val language = view.findViewById<TextView>(R.id.courseDetailLangTextView)
////    private val caption = view.findViewById<TextView>(R.id.courseDetailCCTextView)
////
////    private val learnItemOne = view.findViewById<TextView>(R.id.courseDetailFirstList)
////    private val learnItemTwo = view.findViewById<TextView>(R.id.courseDetailSecondList)
////    private val learnItemThree = view.findViewById<TextView>(R.id.courseDetailThirdList)
////    private val learnItemFour = view.findViewById<TextView>(R.id.courseDetailFourthList)
////    private val learnItemFive = view.findViewById<TextView>(R.id.courseDetailFifthList)
////
////    private val applyButton: Button = view.findViewById(R.id.courseDetailApplyBtn)
////    private val backButton: ImageButton = view.findViewById(R.id.courseDetailBackBtn)
////
////    init {
////        courseImageView.setImageBitmap(courseData.courseImage)
////        courseName.text = courseData.courseName
////        courseDescription.text = courseData.courseDescription
////        courseRatingNum.text = courseData.ratingNumber.toString()
////        courseRatingBar.rating = courseData.ratingNumber
////        courseUserRated.text = courseData.usersRated.toString()
////        instructorName.text = "Offered by " + courseData.instructorName
////        lastUpdate.text = "Last Updated " + courseData.lastUpdate
////
////        val builder = StringBuilder()
////        for (i in 0 until courseData.language.size)
////            builder.append(courseData.language[i] + " ")
////        language.text = builder.toString()
////
////        builder.clear()
////        for (i in 0 until courseData.captions.size)
////            builder.append(courseData.captions[i] + " ")
////        caption.text = builder.toString()
////
////        learnItemOne.text = courseData.itemsToLearn[0]
////        learnItemTwo.text = courseData.itemsToLearn[1]
////        learnItemThree.text = courseData.itemsToLearn[2]
////        learnItemFour.text = courseData.itemsToLearn[3]
////        learnItemFive.text = courseData.itemsToLearn[4]
////    }
//
////    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseDetailAdapter.CourseDetailViewHolder {
////        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_course_detail, parent, false)
////        return CourseDetailViewHolder(itemView)
////    }
////
////    fun onBindViewHolder(holder: CourseDetailAdapter.CourseDetailViewHolder, position: Int) {
////        holder.bind(courseData)
////    }
//
////    inner class CourseDetailViewHolder(itemView: View) {
////        val courseDetailScrollView: ScrollView = itemView.findViewById(R.id.courseDetailScrollView)
////        private val courseImageView: ImageView = itemView.findViewById<ImageView>(R.id.courseDetailImageView)
////        private val courseName: TextView = itemView.findViewById<TextView>(R.id.courseDetailName)
////        private val courseDescription: TextView = itemView.findViewById<TextView>(R.id.courseDetailDescription)
////        private val courseRatingNum: TextView = itemView.findViewById<TextView>(R.id.courseDetailRating)
////        private val courseRatingBar: RatingBar = itemView.findViewById<RatingBar>(R.id.courseDetailRatingBar)
////        private val courseUserRated: TextView = itemView.findViewById<TextView>(R.id.courseDetailUserRated)
////        private val instructorName: TextView = itemView.findViewById<TextView>(R.id.courseDetailInstructor)
////        private val lastUpdate: TextView = itemView.findViewById<TextView>(R.id.courseDetailLastUpdateTextView)
////        private val language: TextView = itemView.findViewById<TextView>(R.id.courseDetailLangTextView)
////        private val caption: TextView = itemView.findViewById<TextView>(R.id.courseDetailCCTextView)
////
////        private val learnItemOne: TextView = itemView.findViewById<TextView>(R.id.courseDetailFirstList)
////        private val learnItemTwo: TextView = itemView.findViewById<TextView>(R.id.courseDetailSecondList)
////        private val learnItemThree: TextView = itemView.findViewById<TextView>(R.id.courseDetailThirdList)
////        private val learnItemFour: TextView = itemView.findViewById<TextView>(R.id.courseDetailFourthList)
////        private val learnItemFive: TextView = itemView.findViewById<TextView>(R.id.courseDetailFifthList)
////
////        private val applyButton: Button = itemView.findViewById(R.id.courseDetailApplyBtn)
////        private val backButton: ImageButton = itemView.findViewById(R.id.courseDetailBackBtn)
////
////        fun bind(courseData: CourseData) {
////            courseImageView.setImageBitmap(courseData.courseImage)
////            courseName.text = courseData.courseName
////            courseDescription.text = courseData.courseDescription
////            courseRatingNum.text = courseData.ratingNumber.toString()
////            courseRatingBar.rating = courseData.ratingNumber
////            courseUserRated.text = courseData.usersRated.toString()
////            instructorName.text = "Offered by " + courseData.instructorName
////            lastUpdate.text = "Last Updated " + courseData.lastUpdate
////
////            val builder = StringBuilder()
////            for (i in 0 until courseData.language.size)
////                builder.append(courseData.language[i] + " ")
////            language.text = builder.toString()
////
////            builder.clear()
////            for (i in 0 until courseData.captions.size)
////                builder.append(courseData.captions[i] + " ")
////            caption.text = builder.toString()
////
////            learnItemOne.text = courseData.itemsToLearn[0]
////            learnItemTwo.text = courseData.itemsToLearn[1]
////            learnItemThree.text = courseData.itemsToLearn[2]
////            learnItemFour.text = courseData.itemsToLearn[3]
////            learnItemFive.text = courseData.itemsToLearn[4]
////        }
//
//    fun setOnApplyButtonClickListenerLambda(listener: (CourseData) -> Unit) {
//        onApplyButtonClickListener = listener
//    }
//
//    fun setOnBackButtonClickListenerLambda(listener: (CourseData) -> Unit) {
//        onBackButtonClickListener = listener
//    }
//
//}
