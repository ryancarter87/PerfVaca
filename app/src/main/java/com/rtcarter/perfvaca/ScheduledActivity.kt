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
        var schedList = listOf("")

        // Read text file, add each value to schedList list
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

                // Possibly a much better way to do this, but I convert schedList to a mutable
                // list here so the user can remove values using removeBtn.
            }
            catch (e: IOException) {
            }
        }

        // Iterate through values in schedList to add radio buttons for each
        for (i in 0..(schedList.size-1)) {
            val button = RadioButton(this)
            button.setId(i)
            button.setText(schedList[i])
            radiogroup.addView(button)
        }


        daysRemaining.text = daysCount.toString()


        rightBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        // NOT WORKING! ******************************************************************************

        // Maybe add the inputreader for the text file after .remove()? I think the value is
        // being removed from the array but the view isn't being updated. Also might be an issue
        // with the format of ${selectedRadioButton.text}\n, that might not be how the values are stored
        // in the list.
        removeBtn.setOnClickListener {
            val selectedId = days_radio_group.getCheckedRadioButtonId()
            val selectedRadioButton = findViewById<RadioButton>(selectedId)
            if (schedList.contains(selectedRadioButton.text)) {
                schedList.toMutableList().remove("${selectedRadioButton.text}\n")
            } else {
                Toast.makeText(this, "Error: Couldn't find date in list.", Toast.LENGTH_LONG).show()
            }
        }

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