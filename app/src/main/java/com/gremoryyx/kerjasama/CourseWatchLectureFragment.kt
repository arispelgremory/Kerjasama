package com.gremoryyx.kerjasama

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_COURSE_DATA = "courseData"
private var courseData = CourseData()

class CourseWatchLectureFragment : Fragment() {
    private lateinit var videoListRecyclerView: RecyclerView
    private lateinit var courseWatchLectureAdapter: CourseWatchLectureAdapter

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_course_watch_lecture, container, false)

        videoListRecyclerView = view.findViewById(R.id.lecture_list_recycler_view)
        videoListRecyclerView.layoutManager = LinearLayoutManager(context)
        videoListRecyclerView.setHasFixedSize(true)

        val videoView = view.findViewById<VideoView>(R.id.lectureVideoView)
        var vPath = "gs://kerjasama-676767.appspot.com/Course/Videos/" + courseData.lectureVideos[0]
        val videoUri = Uri.parse(vPath)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        videoView.setVideoURI(videoUri)

        val lectures = ArrayList<CourseVideoData>()
        for (i in 0..courseData.lectureVideos.size) {
            vPath = "gs://kerjasama-676767.appspot.com/Course/Videos/" + courseData.lectureVideos[i]
            val videoUri = Uri.parse(vPath)
            lectures.add(CourseVideoData(videoUri, courseData.lectureName[i]))
        }

        videoListRecyclerView.adapter = courseWatchLectureAdapter

        courseWatchLectureAdapter.setOnCardViewClickListener { index ->
            videoView.setVideoURI(lectures[index].videoURI)
        }

        return view
    }

}