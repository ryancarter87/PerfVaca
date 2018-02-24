package com.rtcarter.perfvaca

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_scheduled.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast


class ScheduledActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled)

        // Assign the RadioGroup view to this variable in order to iterate through and add
        // radio buttons later
        val radiogroup = findViewById<View>(R.id.days_radio_group) as ViewGroup

        // List of strings to store lines from text file
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!LOOK FOR A BETTER WAY TO DO THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Also create a mutable list to copy schedList to so that it can be edited
        var schedList = listOf("")
        var schedMut = mutableListOf("")
        // Use this string to store elements from schedMut so they can be written to file after
        // removing an element with the removeBtn
        var schedString = ""

        // Read text file, add each value to schedList list
        // Then copy schedList to the mutable list schedMut
        if(fileList().contains("days.txt")) {
            try {
                val file = InputStreamReader(openFileInput("days.txt"))
                val br = BufferedReader(file)
                var line = br.readLine()
                val all = StringBuilder()
                while (line != null) {
                    all.append(line + "\n")
                    line = br.readLine()
                }
                br.close()
                file.close()
                schedList = all.split("\n").map { it.trim() }
                // Copy schedList to a mutable list and remove the last element because it is a blank
                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!LOOK FOR A BETTER WAY TO DO ALL OF THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                schedMut = schedList.toMutableList()
                schedMut.removeAt((schedList.size-1))
            }
            catch (e: IOException) {
            }
       }

        // Iterate through values in schedList to add radio buttons for each
        for (i in 0..(schedMut.size-1)) {
            val button = RadioButton(this)
            button.setId(i)
            button.setText(schedMut[i])
            radiogroup.addView(button)
        }


        daysRemaining.text = daysCount.toString()


        rightBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        removeBtn.setOnClickListener {
            val selectedId = days_radio_group.getCheckedRadioButtonId()
            val selectedRadioButton = findViewById<RadioButton>(selectedId)
            if (schedMut.contains(schedMut[(selectedRadioButton.id + 1)])) {
                // Remove the element at the specified location and remove the radio button
                schedMut.removeAt(selectedRadioButton.id)
                days_radio_group.removeView(selectedRadioButton)

                // Iterate through schedMut and copy the values to schedString so they can be written
                // to file
                for (i in schedMut) {
                    schedString += i + "\n"
                }

                 try {
                    val file = OutputStreamWriter(openFileOutput("days.txt", Activity.MODE_PRIVATE))

                    file.write (schedString)
                    file.close ()

                    Toast.makeText(this, "Successfully removed date from the list.", Toast.LENGTH_LONG).show()
                }
                 catch (e : IOException) {
                }
            } else {
                Toast.makeText(this, "Error: Couldn't find date in list.", Toast.LENGTH_LONG).show()
            }
        }


        // Clear the file on click
        // !!!!!!!!!!!!!!!!!!!!NEED TO ADD A CONFIRMATION BUTTON IN CASE THE USER ACCIDENTALLY PRESSES THIS!!!!!!!!!!!!!!!!!!!!!!!!
        clearBtn.setOnClickListener {
            try {
                val file = OutputStreamWriter(openFileOutput("days.txt", Activity.MODE_PRIVATE))

                file.write ("")
                file.close ()
                Toast.makeText(this, "Successfully cleared all vacation days.", Toast.LENGTH_LONG).show()
            } catch (e : IOException) {
            }
        }

    }
}
