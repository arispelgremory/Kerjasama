package com.gremoryyx.kerjasama

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CourseWatchLectureAdapter (private val videoDataList: ArrayList<CourseVideoData>): RecyclerView.Adapter<CourseWatchLectureAdapter.CourseWatchLectureViewHolder>(){
    private var onCardViewClickListener: ((Int) -> Unit)? = null
    private var numbering: Int = 0

    fun setOnCardViewClickListener(listener: (Int) -> Unit) {
        onCardViewClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CourseWatchLectureViewHolder {
        val itemView = View.inflate(parent.context, R.layout.course_lecture_list_item, null)
        return CourseWatchLectureViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseWatchLectureViewHolder, position: Int) {
        val currentItem = videoDataList[position]
        numbering = position + 1
        holder.bind(currentItem, numbering)
    }

    override fun getItemCount(): Int {
        return videoDataList.size
    }

    inner class CourseWatchLectureViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val videoCard: CardView = itemView.findViewById(R.id.videoCardView)
        private val lectureNumbering: TextView = itemView.findViewById(R.id.lectureNumbering)
        private val lectureTitle: TextView = itemView.findViewById(R.id.videoTitle)

        fun bind(lectures: CourseVideoData, numbering: Int) {
            lectureTitle.text = lectures.videoTitle
            lectureNumbering.text = numbering.toString() + "."

            videoCard.setOnClickListener {
                onCardViewClickListener?.let { click ->
                    click(bindingAdapterPosition)
                }
            }
        }
    }
}