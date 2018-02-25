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
import com.rtcarter.perfvaca.R.id.all
import java.time.LocalDateTime


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

        // IF FUN READ DOESN'T WORK PASTE CODE BLOCK BACK HERE

        // Call read function to store values from text file in schedList list.
        // Then split the values into seperate elements at newline
        schedList = read("days.txt").split("\n").map { it.trim() }
        schedMut = schedList.toMutableList()
        schedMut.removeAt((schedList.size-1))

        // Iterate through values in schedList to add radio buttons for each
        for (i in 0..(schedMut.size-1)) {
            val button = RadioButton(this)
            button.setId(i)
            button.setText(schedMut[i])
            radiogroup.addView(button)
        }

        rightBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        removeBtn.setOnClickListener {
            val selectedId = days_radio_group.getCheckedRadioButtonId()
            val selectedRadioButton = findViewById<RadioButton>(selectedId)
            if (schedMut.contains(schedMut[selectedRadioButton.id])) {
                // Remove the element at the specified location and remove the radio buttons
                schedMut.removeAt(selectedRadioButton.id)
                radiogroup.removeAllViews()
                // Recreate radio buttons with new size
                for (i in 0..(schedMut.size-1)) {
                    val button = RadioButton(this)
                    button.setId(i)
                    button.setText(schedMut[i])
                    radiogroup.addView(button)
                }

                // Iterate through schedMut and copy the values to schedString so they can be written
                // to file
                schedString = ""
                for (i in schedMut) {
                    schedString += i + "\n"
                }

                // IF WRITE FUNCTION DOESN'T WORK, PASTE CODE BLOCK HERE
                write("days.txt", schedString)
            } else {
                Toast.makeText(this, "Error: Couldn't find date in list.", Toast.LENGTH_LONG).show()
            }
        }

        // Clear the file on click
        // !!!!!!!!!!!!!!!!!!!!NEED TO ADD A CONFIRMATION BUTTON IN CASE THE USER ACCIDENTALLY PRESSES THIS!!!!!!!!!!!!!!!!!!!!!!!!
        clearBtn.setOnClickListener {
            write("days.txt", "")

            Toast.makeText(this, "ALL SCHEDULED DAYS REMOVED!", Toast.LENGTH_LONG).show()
        }
    }

    // Function takes a String for the name of the file to read, and then returns all lines
    // in file appended with \n as StringBuilder
    fun read(txt: String): StringBuilder {

        val all = StringBuilder()

        if(fileList().contains(txt)) {
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
            }
            catch (e: IOException) {
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
        } catch (e : IOException) {
        }
    }
}
