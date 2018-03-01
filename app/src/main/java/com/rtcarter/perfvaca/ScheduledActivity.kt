package com.rtcarter.perfvaca

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlinx.android.synthetic.main.activity_scheduled.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import android.view.ViewGroup
import android.widget.*
import java.text.SimpleDateFormat
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import com.rtcarter.perfvaca.R.layout.activity_scheduled
import java.lang.ref.WeakReference


class ScheduledActivity : AppCompatActivity() {

    val myFormat = "MM/dd/yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled)

        var gson = Gson()

        // Variable to hold radiogoup from layout
        val radiogroup = findViewById<View>(R.id.radioGroup) as ViewGroup
        // Variable to hold spinner from layout
        val spinner = findViewById<View>(R.id.spinnerView) as Spinner

        // Read the json file into peopleList. Same as MainActivity.

        var peopleList: MutableList<PeopleDates> = gson.fromJson(read("people.json"), object : TypeToken<MutableList<PeopleDates>>() {}.type)

        // Create variable to hold all "name" values from peopleList objects
        var nameList: MutableList<String> = mutableListOf("All")
        for (i in peopleList) {
            nameList.add(i.name)
        }

        // Create a spinner to hold all "name"s from objects in peopleList. When user selects
        // one, display all dates assigned to that name
        val spin = ArrayAdapter(this, android.R.layout.simple_spinner_item, nameList)
        spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(spin)

        // Variable to check which spinner button was selected in order to determine radio buttons:
        var spinItem = "All"

        // Spinner functions
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
                // Set spinItem to the selected spinner item, refresh radiogroup
                // then add radiobuttons based on dates assigned to object with name selected
                spinItem = nameList[position]
                radiogroup.removeAllViews()
                var count = 0
                if (spinItem.equals("All")) {
                    for (i in peopleList) {
                        println(peopleList)
                        for (x in i.dates) {
                            val button = RadioButton(this@ScheduledActivity)
                            button.setText(sdf.format(x))
                            button.setId(count)
                            radiogroup.addView(button)
                            count++
                        }
                    }
                } else {
                    for (i in peopleList) {
                        if (spinItem.equals(i.name)) {
                            for (x in i.dates) {
                                val button = RadioButton(this@ScheduledActivity)
                                button.setText(sdf.format(x))
                                button.setId(count)
                                radiogroup.addView(button)
                                count++
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

        // Switch to MainActivity
        rightBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Clear the file on click
        // !!!!!!!!!!!!!!!!!!!!NEED TO ADD A CONFIRMATION BUTTON IN CASE THE USER ACCIDENTALLY PRESSES THIS!!!!!!!!!!!!!!!!!!!!!!!!
        clearBtn.setOnClickListener {
            write("people.json", "")

            Toast.makeText(this, "ALL SCHEDULED DAYS REMOVED!", Toast.LENGTH_LONG).show()
        }

        /* ************************************** ADD REMOVE BUTTON FUNCTION!!!!!*******************************************************
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
        */
    }

    // Read from json file
    fun read(txt: String): String {

        val all = StringBuilder()

        if (fileList().contains(txt)) {
            try {
                val file = InputStreamReader(openFileInput(txt))
                val br = BufferedReader(file)
                var line = br.readLine()
                while (line != null) {
                    all.append(line + "\n")
                    line = br.readLine()
                }
                br.close()
                file.close()
            } catch (e: IOException) {
            }
        }

        var json: String = all.toString()
        return json
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
