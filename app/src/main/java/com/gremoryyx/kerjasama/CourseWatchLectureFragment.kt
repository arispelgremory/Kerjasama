package com.gremoryyx.kerjasama

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

private const val ARG_COURSE_DATA = "courseData"

class CourseWatchLectureFragment : Fragment() {
    private lateinit var videoListRecyclerView: RecyclerView
    private lateinit var courseWatchLectureAdapter: CourseWatchLectureAdapter

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val data = arguments?.getParcelable<CourseData>(ARG_COURSE_DATA)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_course_watch_lecture, container, false)

        videoListRecyclerView = view.findViewById(R.id.lecture_list_recycler_view)
        videoListRecyclerView.layoutManager = LinearLayoutManager(context)
        videoListRecyclerView.setHasFixedSize(true)

        Log.d("DATA GIVEN HERE", data.toString())

        val storageRef = FirebaseStorage.getInstance().reference
        val videoView = view.findViewById<VideoView>(R.id.lectureVideoView)
        val lectures = ArrayList<CourseVideoData>()

        for (i in 0 until data?.lectureVideos!!.size) {
            var vPath = "gs://kerjasama-676767.appspot.com/Course/Videos/" + data?.lectureVideos!![i] + ".mp4"
            lectures.add(CourseVideoData(Uri.parse(vPath), data?.lectureName!![i]))
        }
        displayVideoDetails(data, view, 0)
        playCourseVideo(data, view,videoView, storageRef, 0)
        Log.d("Here comes Lecture", lectures.toString())

        courseWatchLectureAdapter = CourseWatchLectureAdapter(lectures)
        videoListRecyclerView.adapter = courseWatchLectureAdapter

        courseWatchLectureAdapter.setOnCardViewClickListener { index ->
            if (data != null) {
                displayVideoDetails(data, view, index)
                playCourseVideo(data, view,videoView, storageRef, index)

            }
        }

        return view
    }

    private fun playCourseVideo(
        data: CourseData,
        view: View,
        videoView: VideoView,
        storageRef: StorageReference,
        index: Int
    ){
        var courseVideoRef = storageRef.child("Course/Videos/" + data?.lectureVideos!![index] + ".mp4")
        val localTempFile = File.createTempFile("Videos", "mp4")
        courseVideoRef.getFile(localTempFile).addOnSuccessListener {
            Log.d("Success", "Video downloaded")
            // File downloaded successfully, set the local file path as the video source for the VideoView
            val videoUri = Uri.fromFile(localTempFile)
            videoView.setVideoURI(videoUri)
            videoView.start()
        }.addOnFailureListener {
            Log.d("Failed", "Video failed to download")
        }
    }

    private fun displayVideoDetails(data: CourseData, view: View, index: Int) {
        var videoTitle = view.findViewById<TextView>(R.id.courseDetailName)
        var videoInstructor = view.findViewById<TextView>(R.id.courseDetailInstructor)
        videoTitle.setText(data?.lectureName!![index])
        videoInstructor.setText(data?.instructorName)
    }

}