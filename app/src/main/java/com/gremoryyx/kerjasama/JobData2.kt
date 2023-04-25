package com.gremoryyx.kerjasama

data class Job(
    val imageResource: Int,
    val jobName: String,
    val companyName: String,
    val jobType: String,
    val location: String,
    val duration: String,
    val jobDescription: String,
    val welfares: List<String>,
    val requirements: List<String>,
)

data class RegisteredJob(
    val registeredStatus: String,
    val imageResource: Int,
    val jobName: String,
    val companyName: String,
    val jobType: String,
    val location: String,
    val duration: String,
    val jobDescription: String,
    val welfares: List<String>,
    val requirements: List<String>,
)