package com.gremoryyx.kerjasama

data class Job(
    val name: String,
    val companyName: String,
    val jobType: String,
    val location: String,
    val requirements: List<String>,
    val imageResource: Int // You can also use String to store the image url
)

data class RegisteredJob(
    val jobId: String,
    val jobTitle: String,
    val company: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val salary: String,
    val jobDescription: String
)