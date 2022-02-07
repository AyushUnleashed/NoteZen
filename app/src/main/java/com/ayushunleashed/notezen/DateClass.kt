package com.ayushunleashed.notezen

import android.util.Log
import com.google.android.material.timepicker.TimeFormat
import java.sql.Time
import java.text.DateFormat
import java.util.*

class DateClass() {
    lateinit var date: Date
    lateinit var currentDate: String
    public fun getDate(): String {
        date= Calendar.getInstance().time
        currentDate=DateFormat.getDateInstance().format(date)
        var currentTime =DateFormat.getTimeInstance().format(date.time)
        return currentTime+"\n"+currentDate
    }
}