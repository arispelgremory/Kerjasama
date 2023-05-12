package com.gremoryyx.kerjasama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CourseListAdapter (private val courseList: ArrayList<CourseData>): RecyclerView.Adapter<CourseListAdapter.CourseViewHolder>() {
    private var onCardViewClickListener: ((CourseData) -> Unit)? = null

    fun setOnCardViewClickListener(listener: (CourseData) -> Unit) {
        onCardViewClickListener = listener
    }

    fun updateCourseList(newCourseList: ArrayList<CourseData>) {
        courseList.clear()
        courseList.addAll(newCourseList)
        notifyDataSetChanged()
    }

    fun setCourseList(newCourseList: ArrayList<CourseData>) {
        courseList.clear()
        courseList.addAll(newCourseList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.course_list_item, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentItem = courseList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseCard: CardView = itemView.findViewById(R.id.courseCardView)
        private val courseImageView: ImageView = itemView.findViewById(R.id.courseImageView)
        private val courseName: TextView = itemView.findViewById(R.id.courseNameTextView)
        private val instructorName: TextView = itemView.findViewById(R.id.instructorNameTextView)
        private val ratingNumber: TextView = itemView.findViewById(R.id.ratingNumberTextView)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBarView)
        private val usersRated: TextView = itemView.findViewById(R.id.usersRatedTextView)

        private var currentCourse: CourseData? = null

        fun bind(course: CourseData) {
            if (currentCourse == null || currentCourse != course) {
                courseImageView.setImageBitmap(course.courseImage)
                courseName.text = course.courseName
                instructorName.text = course.instructorName
                ratingNumber.text = course.ratingNumber.toString()
                ratingBar.rating = course.ratingNumber
                usersRated.text = "(" + course.usersRated.toString() + ")"

                currentCourse = course
            }
        }
    }
}