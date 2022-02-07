package com.ayushunleashed.notezen.models

import com.google.firebase.firestore.Exclude

class NotesModel(
    var title:String="",
    var description:String="",
    var date: String? ="",
)
