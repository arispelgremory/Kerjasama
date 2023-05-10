package com.gremoryyx.kerjasama

import android.graphics.Bitmap

data class UserData(
    var jobImage: Bitmap,
    var name: String,
    var username:String,
    var email:String,
    var ic_number: String,
    var gender: String,
    var date_of_birth: String,
    var phone_number: String,
    var highest_qualifications: String,


){
    constructor(): this(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),"", "", "", "", "", "", "","")
}
