package com.gremoryyx.kerjasama

import com.google.firebase.firestore.DocumentId

data class CompanyData(
    @DocumentId
    var company_id: String,
    var company_img: String,
    var company_name: String
    ){
    constructor(): this("", "", "")
}


