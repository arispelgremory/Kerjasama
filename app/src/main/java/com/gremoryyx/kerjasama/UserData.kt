package com.gremoryyx.kerjasama

import android.graphics.Bitmap

data class UserData(
    var user_image: String,
    var name: String,
    var email:String,
    var ic_number: String,
    var gender: String,
    var date_of_birth: String,
    var phone_number: String,
    var highest_qualifications: String,
    var bio: String


){
    constructor(): this("", "", "", "", "", "","","","")
}
