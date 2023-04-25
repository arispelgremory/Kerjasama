package com.gremoryyx.kerjasama

import android.graphics.Bitmap

data class JobData(
    var jobImage: Bitmap,
    var jobName: String,
    var companyName: String,
    var jobType: String,
    var location: String,
    var duration: String,
    var salary: String,
    var jobDescription: String,
    var walfares: ArrayList<String>,
    var requirements: ArrayList<String>,
){
    constructor(): this(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888), "", "", "", "", "", "", "", ArrayList(), ArrayList())

}
