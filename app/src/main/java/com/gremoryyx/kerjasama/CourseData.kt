package com.gremoryyx.kerjasama

import android.graphics.Bitmap
import android.provider.MediaStore.Files
import android.provider.MediaStore.Video

data class CourseData(
    var courseImage: Bitmap,
    var courseName: String,
    var courseDescription: String,
    var instructorName: String,
    var ratingNumber: Float,
    var usersRated: Number,
    var lastUpdate: String,
    var itemsToLearn: ArrayList<String>,
    var lectureVideos: ArrayList<Video>,
    var courseMaterials: ArrayList<Files>

){
    constructor(): this(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888), "", "", "", 0, 0, "",  ArrayList(), ArrayList(), ArrayList())

}
