package com.rtcarter.perfvaca

import android.os.Build
import android.support.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
val myFormat = "MM/dd/yyyy" // mention the format you need
val sdf = SimpleDateFormat(myFormat, Locale.US)

data class PeopleDates(var dates: MutableList<Date> = mutableListOf(sdf.parse(sdf.format(Date()))), var name: String = "Unspecified") {
}
