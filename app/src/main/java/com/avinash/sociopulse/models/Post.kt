package com.avinash.sociopulse.models

data class Post(
    val text: String = "",
    val imageUrl: String? = null,
    val user: User = User(),
    val time: Long = 0L,
    val likeList: MutableList<String> = mutableListOf(),

    val listYes: MutableList<String> = mutableListOf(),
    val listProbablyYes: MutableList<String> = mutableListOf(),
    val listProbablyNo: MutableList<String> = mutableListOf(),
    val listNot: MutableList<String> = mutableListOf()

)
