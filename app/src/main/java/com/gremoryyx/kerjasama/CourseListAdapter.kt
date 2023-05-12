package com.gremoryyx.kerjasama

import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CourseAdapter (private val courseList: ArrayList<CourseData>): RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseCard: CardView = itemView.findViewById(R.id.courseCardView)
        private val courseImageView: ImageView = itemView.findViewById(R.id.courseImageView)
        private val courseName: TextView = itemView.findViewById(R.id.courseNameTextView)
        private val instructorName: TextView = itemView.findViewById(R.id.instructorNameTextView)
        private val ratingNumber: TextView = itemView.findViewById(R.id.ratingNumberTextView)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBarView)
        private val usersRated: TextView = itemView.findViewById(R.id.usersRatedTextView)

    }
}