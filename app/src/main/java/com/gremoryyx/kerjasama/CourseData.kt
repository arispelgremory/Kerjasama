package com.gremoryyx.kerjasama

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
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
    var language: ArrayList<String>,
    var captions: ArrayList<String>,
    var itemsToLearn: ArrayList<String>,
    var lectureVideos: ArrayList<String>,
    var lectureName: ArrayList<String>,
    var lecturesWatched: Number

): Parcelable
{
    constructor(): this(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888), "", "", "", 0.0f, 0, "",  ArrayList(), ArrayList(), ArrayList(), ArrayList(), ArrayList(), 0)
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(courseImage, flags)
        parcel.writeString(courseName)
        parcel.writeString(courseDescription)
        parcel.writeString(instructorName)
        parcel.writeFloat(ratingNumber)
        parcel.writeInt(usersRated.toInt())
        parcel.writeString(lastUpdate)
        parcel.writeList(language)
        parcel.writeList(captions)
        parcel.writeList(itemsToLearn)
        parcel.writeList(lectureVideos)
        parcel.writeList(lectureName)
        parcel.writeInt(lecturesWatched.toInt())
    }

    companion object CREATOR : Parcelable.Creator<CourseData> {
        override fun createFromParcel(parcel: Parcel): CourseData {
            // Read the values from the parcel and create a new instance of the CourseData class
            val courseImage = parcel.readParcelable<Bitmap>(Bitmap::class.java.classLoader)
            val courseName = parcel.readString()
            val courseDescription = parcel.readString()
            val instructorName = parcel.readString()
            val ratingNumber = parcel.readFloat()
            val usersRated = parcel.readInt()
            val lastUpdate = parcel.readString()
            val language = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>
            val captions = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>
            val itemsToLearn = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>
            val lectureName = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>
            val lectureVideos = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>
            val lecturesWatched = parcel.readInt()
            return CourseData(courseImage!!, courseName!!, courseDescription!!, instructorName!!, ratingNumber, usersRated, lastUpdate!!, language, captions,itemsToLearn, lectureVideos, lectureName, lecturesWatched)
        }

        override fun newArray(size: Int): Array<CourseData?> {
            return arrayOfNulls(size)
        }
    }
}

