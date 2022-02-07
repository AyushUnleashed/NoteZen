package com.ayushunleashed.notezen

import java.text.DateFormat
import java.util.*

class DateClass() {
    lateinit var calendar: Calendar
    lateinit var currentDate: String
    public fun getDate(): String {
        calendar= Calendar.getInstance()
        currentDate=DateFormat.getDateInstance().format(calendar.time)
        return currentDate
    }
}