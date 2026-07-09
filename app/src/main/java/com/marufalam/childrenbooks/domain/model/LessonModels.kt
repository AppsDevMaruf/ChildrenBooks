package com.marufalam.childrenbooks.domain.model

data class LessonItem(
    val title: String,
    val image: Int,
    val sound: Int,
)

data class PictureLesson(
    val title: String,
    val image: Int,
)

data class AlphabetLesson(
    val letter: String,
    val description: Int,
    val capitalImage: Int,
    val exampleImage: Int,
    val smallImage: Int,
    val firstWord: Int,
    val secondWord: Int,
    val letterSound: Int,
    val wordSound: Int,
)

data class NumberLesson(
    val number: String,
    val numberImage: Int,
    val countImage: Int,
    val word: Int,
    val numberSound: Int,
    val wordSound: Int,
)
