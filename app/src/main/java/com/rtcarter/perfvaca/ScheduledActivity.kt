package com.rtcarter.perfvaca

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlinx.android.synthetic.main.activity_scheduled.*
import android.view.ViewGroup
import android.widget.*
import java.text.SimpleDateFormat
import android.widget.AdapterView
import android.widget.TextView
import android.content.DialogInterface
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.io.*


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
        // Variable to reference application context
        var context: Context = applicationContext

        // Create default object to store in peopleList, same as Main
        var person = PeopleDates()
        var peopleList: MutableList<PeopleDates> = mutableListOf(person)

        // Read the json file into peopleList. Same as MainActivity.
        if(fileList().contains("people.json")) {
            peopleList = gson.fromJson(read("people.json"), object : TypeToken<MutableList<PeopleDates>>() {}.type)
        }

        // Set days_scheduled textView to equal the number of dates assigned to the selected name
        days_scheduled.text = (peopleList[0].dates.size.toString())

        // Create variable to hold all "name" values from peopleList objects
        var nameList: MutableList<String> = mutableListOf(peopleList[0].name)
        for (i in peopleList) {
            nameList.add(i.name)
        }

        // Create a spinner to hold all "name"s from objects in peopleList. When user selects
        // one, display all dates assigned to that name
        val spin = ArrayAdapter(this, R.layout.spinner_item, nameList)
        spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(spin)

        // Variable to check which spinner button was selected in order to determine radio buttons:
        var spinItem = peopleList[0].name

        // Spinner functions
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
                // Set spinItem to the selected spinner item, refresh radiogroup
                // then add radiobuttons based on dates assigned to object with name selected
                spinItem = nameList[position]
                radiogroup.removeAllViews()
                var count = 0

                for (i in peopleList) {
                    if (spinItem.equals(i.name)) {
                        days_scheduled.text = i.dates.size.toString()
                        for (x in i.dates) {
                            val button = RadioButton(this@ScheduledActivity)
                            button.setTextColor(Color.WHITE)
                            button.setText(sdf.format(x))
                            button.setId(count)
                            radiogroup.addView(button)
                            count++
                        }
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }

        // Switch to MainActivity
        schedBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Use AlertDialog to create a popup window to confirm the user's request, then if confirmed
        // delete the json file
        clearBtn.setOnClickListener {
            alert("This will permanently delete all scheduled dates. Continue?") {
                title = "Confirm Clear All"
                positiveButton("Yes") {
                    delete("people.json")
                    Toast.makeText(context, "ALL SCHEDULED DAYS REMOVED!", Toast.LENGTH_LONG).show()
                    }
                negativeButton("Cancel") { }
            }.show()
        }

        // Remove button functions
        removeBtn.setOnClickListener {
            val selectedId = radioGroup.getCheckedRadioButtonId()
            val selectedRadioButton = findViewById<RadioButton>(selectedId)
            val selectedName = spinItem
            val selectedText: String = selectedRadioButton.text.toString()
            val selectedDate = sdf.parse(selectedText)

            // On click of Remove, if the list of PeopleDates contains an object with the name
            // selected on the spinner, and that object contains the date displayed by the radio
            // button selected, remove that date from the object and write the edited list to file
            for (i in peopleList) {
                if (i.name == selectedName && i.dates.contains(selectedDate)) {
                    i.dates.remove(selectedDate)
                    radioGroup.removeView(selectedRadioButton)
                    Toast.makeText(this, "Date removed from list.", Toast.LENGTH_LONG).show()

                    val json: String = gson.toJson(peopleList)
                    write("people.json", json)
                }
            }
        }
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

    // Delete the file upon confirmation of the clear button
    fun delete(txt: String) {
        val dir: File = getFilesDir()
        var file = File(dir, txt)
        var deleted: Boolean = file.delete()
    }
}
