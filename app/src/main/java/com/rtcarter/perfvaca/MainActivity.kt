package com.rtcarter.perfvaca

import android.app.Activity
import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.Toast
import java.text.SimpleDateFormat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*


class MainActivity : AppCompatActivity() {

    val calendar = Calendar.getInstance()
    var chosenDate: Date = calendar.getTime()

    // Initialize the format dates will be parsed in later
    val myFormat = "MM/dd/yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale.US)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var gson = Gson()

        // Initialize a PeopleDates() so that peopleList can be initialized
        // NEEED BETTER WAY TO DO THIS!!!!!!!!!!!!!!!!!!!!! LOOK INTO MAKING peopleList READ ONLY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        var person = PeopleDates()
        var peopleList: MutableList<PeopleDates> = mutableListOf(person)

        // If the json file exists then load its contents into peopleList
        if (fileList().contains("people.json")) {
            peopleList = (gson.fromJson(read("people.json"), object : TypeToken<MutableList<PeopleDates>>() {}.type))
        }

        // Initialize the calendar and add onclicklistener to display date picker to user
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        chooseDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@MainActivity,
                        dateSetListener,
                        // set DatePickerDialog to point to today's date when it loads up
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        scheduleBtn.setOnClickListener {
            val nameIn = nameText.text.toString()
            // Create a boolean var to check if the name entered already exists in the data
            var check: Boolean = false

            // selected used to hold either a new PeopleDates object or an existing one
            // based on whether an object with that name already exists in peopleList
            var selected = PeopleDates()

            // Iterate through peopleList, if an object with same "name" exists, then add the chosen
            // date to that object. Otherwise create a new object with entered name and chosen date
            for (i in peopleList) {
                if (i.name.equals(nameIn)) {
                    check = true
                    selected = i
                    break
                } else {
                    check = false
                }
            }

            if (check) {
                if (selected.dates.contains(chosenDate)) {
                    Toast.makeText(this, "Date already scheduled for $nameIn", Toast.LENGTH_LONG).show()
                } else {
                    selected.dates.add(chosenDate)
                }
            } else {
                selected.dates = mutableListOf(chosenDate)
                selected.name = nameIn
                peopleList.add(selected)
            }

            // write peopleList to the json file
            var json: String = gson.toJson(peopleList)
            write("people.json", json)
        }


        schedBtn.setOnClickListener {
            val intent = Intent(this, ScheduledActivity::class.java)
            startActivity(intent)
        }
    }

    // Function updates the textView dateText with the correctly formatted time from calendar
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateText!!.text = sdf.format(calendar.getTime())
        chosenDate = sdf.parse(sdf.format(calendar.getTime()))
    }

    // Read the json file line by line, adding each to all and then casting all toString as json
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

    // Simple write function for storing json data
    fun write(txt: String, str: String) {
        try {
            val file = OutputStreamWriter(openFileOutput(txt, Activity.MODE_PRIVATE))

            file.write(str)
            file.close()
        } catch (e: IOException) {
        }
    }
}
