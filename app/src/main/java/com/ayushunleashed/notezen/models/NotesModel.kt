package com.ayushunleashed.notezen.models

import com.google.firebase.firestore.Exclude

class NotesModel(
    val title:String="",
    val description:String="",
    val date: String? =""
)
