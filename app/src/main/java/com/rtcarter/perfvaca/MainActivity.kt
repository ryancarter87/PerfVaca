package com.rtcarter.perfvaca

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.Intent
import android.widget.Toast
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Using daysList here to convert text file to list of strings and then check its size in the write block
        var daysList = listOf("")
        // Using the mutable list daysMut to copy daysList to so the list can be edited
        var daysMut = mutableListOf("")

        var daysString = ""
        dateText.text = "${datePicker.month + 1}/${datePicker.dayOfMonth}/${datePicker.year}"

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                DatePicker.OnDateChangedListener { datePicker, year, month, day ->
                    dateText.text = "${month + 1}/${day}/${year}"
                })

        // User can only schedule 20 or less days. If 20 is reached, display a Toast notifying user.
        // Otherwise, when button is clicked, add the date in the dateText TextView to the text file
        // and append it to daysString.
        scheduleBtn.setOnClickListener {
            daysList = read("days.txt").split("\n").map { it.trim() }
            daysMut = daysList.toMutableList()
            daysMut.removeAt((daysList.size-1))

            if (daysList.size >= 20) {
                Toast.makeText(this, "You've already scheduled 20 days. Remove one to schedule another.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Date successfully scheduled as vacation.", Toast.LENGTH_LONG).show()
                daysMut.add("${dateText.text}")
                daysString = ""

                for (i in daysMut) {
                    daysString += i + "\n"
                }

                write("days.txt", daysString)
            }
        }

        leftBtn.setOnClickListener {
            val intent = Intent(this, ScheduledActivity::class.java)
            startActivity(intent)
        }
    }

    fun read(txt: String): StringBuilder {

        val all = StringBuilder()

        if (fileList().contains(txt)) {
            try {
                val file = InputStreamReader(openFileInput(txt))
                val br = BufferedReader(file)
                var line = br.readLine()
                // val all = StringBuilder()
                while (line != null) {
                    all.append(line + "\n")
                    line = br.readLine()
                }
                br.close()
                file.close()
            } catch (e: IOException) {
            }
        }

        return all
    }

    // Function takes a text file name as String and the string to write as String
// Then writes the string to the file
    fun write(txt: String, str: String) {
        try {
            val file = OutputStreamWriter(openFileOutput(txt, Activity.MODE_PRIVATE))

            file.write(str)
            file.close()
        } catch (e: IOException) {
        }
    }
}
