package com.gremoryyx.kerjasama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CourseRegisteredListAdapter (private val courseRegisteredList: ArrayList<CourseData>): RecyclerView.Adapter<CourseRegisteredListAdapter.CourseRegisteredViewHolder>() {
    private var onCardViewClickListener: ((CourseData) -> Unit)? = null

    fun setOnCardViewClickListener(listener: (CourseData) -> Unit) {
            onCardViewClickListener = listener
        }

    fun setCourseRegisteredList(newCourseRegisteredList: ArrayList<CourseData>) {
            courseRegisteredList.clear()
            courseRegisteredList.addAll(newCourseRegisteredList)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseRegisteredViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.course_registered_list_item, parent, false)
            return CourseRegisteredViewHolder(itemView)
        }

    override fun onBindViewHolder(holder: CourseRegisteredViewHolder, position: Int) {
            val currentItem = courseRegisteredList[position]
            holder.bind(currentItem)
        }

    override fun getItemCount(): Int {
            return courseRegisteredList.size
        }

    inner class CourseRegisteredViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val courseCard: CardView = itemView.findViewById(R.id.courseCardView)
        private val courseRegisteredImageView: ImageView = itemView.findViewById(R.id.courseRegisteredCardView)
        private val courseRegisteredName: TextView = itemView.findViewById(R.id.courseNameRegisteredTextView)
        private val instructorRegisteredName: TextView = itemView.findViewById(R.id.instructorNameRegisteredTextView)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.courseProgressBar)
        private val courseProgressStatus : TextView = itemView.findViewById(R.id.courseProgressTextView)


        private var currentCourse: CourseData? = null

        fun bind(course: CourseData) {
            if (currentCourse == null || currentCourse != course) {
                courseRegisteredImageView.setImageBitmap(course.courseImage)
                courseRegisteredName.text = course.courseName
                instructorRegisteredName.text = course.instructorName

                val numberOfLectures = course.lectureVideos.size
                var numberOfLecturesWatched = course.lecturesWatched.toInt()
                var progress = (numberOfLecturesWatched / numberOfLectures).toFloat()
                progress *= 100

                progressBar.progress = progress.toInt()

                if (progress.toInt() == 100)
                    courseProgressStatus.text = "Completed"
                else
                    courseProgressStatus.text = progress.toString() + "% Completed"

                courseCard.setOnClickListener {
                    onCardViewClickListener?.let { click ->
                        click(course)
                    }
                }
            }
        }
    }
}
