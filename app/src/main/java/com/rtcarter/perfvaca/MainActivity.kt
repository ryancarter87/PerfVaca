package com.rtcarter.perfvaca

import android.app.Activity
import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.alert
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
        val spinner = findViewById<View>(R.id.spinnerView2) as Spinner

        // Initialize a PeopleDates() so that peopleList can be initialized
        // NEEED BETTER WAY TO DO THIS!!!!!!!!!!!!!!!!!!!!! LOOK INTO MAKING peopleList READ ONLY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        var person = PeopleDates()
        var peopleList: MutableList<PeopleDates> = mutableListOf(person)

        // Check variable will equal true if people.json does not exist
        // This means this is the first time the program has started, so a default value will
        // be added to peopleList and needs to be removed after another date has been scheduled
        var checkF: Boolean

        // checkDate will equal true if the selected date has already been scheduled for a different name
        var checkDate = false
        var takenName = ""


        // If the json file exists then load its contents into peopleList, set View Dates button to visible
        if (fileList().contains("people.json")) {
            peopleList = (gson.fromJson(read("people.json"), object : TypeToken<MutableList<PeopleDates>>() {}.type))
            checkF = false
            schedBtn.setVisibility(View.VISIBLE)
        } else {
            checkF = true
            schedBtn.setVisibility(View.INVISIBLE)
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

        // Create variable to hold all "name" values from peopleList objects
        var nameList: MutableList<String> = mutableListOf(peopleList[0].name)
        for (i in peopleList) {
            nameList.add(i.name)
        }

        // Create a spinner to hold all "name"s from objects in peopleList. When user selects
        // one, automatically fill in the nameText view with that name
        val spin = ArrayAdapter(this, R.layout.spinner_item, nameList)
        spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(spin)

        // Variable to check which spinner button was selected in order to determine radio buttons:
        var spinItem = peopleList[0].name

        // Spinner functions
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
                // Set spinItem to the selected spinner item, then set nameText to it
                spinItem = nameList[position]

                nameText.setText(spinItem)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }


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

            // Iterate through all dates scheduled to see if that date has already been assigned to a name
            // If it has, prompt the user to see if they still want to schedule that date
            for (i in peopleList) {
                if (i.dates.contains(chosenDate)) {
                    checkDate = true
                    takenName = i.name
                    break
                }
            }

            if (checkDate) {
                alert("That date is already scheduled for $takenName, schedule anyways?") {
                    title = "Date already scheduled"
                    positiveButton("Yes") {
                        if (check) {
                            if (selected.dates.contains(chosenDate)) {
                                Toast.makeText(applicationContext, "Date already scheduled for $nameIn", Toast.LENGTH_LONG).show()
                            } else {
                                selected.dates.add(chosenDate)
                                Toast.makeText(applicationContext, "${dateText.text} scheduled as a vacation day for $nameIn", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            selected.dates = mutableListOf(chosenDate)
                            selected.name = nameIn
                            peopleList.add(selected)
                            Toast.makeText(applicationContext, "${dateText.text} scheduled as a vacation day for $nameIn", Toast.LENGTH_LONG).show()
                        }

                        // Sort the list of dates in descending order
                        for (i in peopleList) {
                            i.dates.sort()
                        }

                        // write peopleList to the json file
                        var json: String = gson.toJson(peopleList)
                        write("people.json", json)
                    }
                    negativeButton("Cancel") {
                    }
                }.show()
            } else {
                if (check) {
                    if (selected.dates.contains(chosenDate)) {
                        Toast.makeText(this, "Date already scheduled for $nameIn", Toast.LENGTH_LONG).show()
                    } else {
                        selected.dates.add(chosenDate)
                        Toast.makeText(this, "${dateText.text} scheduled as a vacation day for $nameIn", Toast.LENGTH_LONG).show()
                    }
                } else {
                    selected.dates = mutableListOf(chosenDate)
                    selected.name = nameIn
                    peopleList.add(selected)
                    Toast.makeText(this, "${dateText.text} scheduled as a vacation day for $nameIn", Toast.LENGTH_LONG).show()
                }
            }

            // If checkF is true then this is the first time program has ever ran on device
            // So, first value in peopleList will be the default placeholder value
            // This value needs to be removed, then set checkF to false
            if (checkF) {
                peopleList.removeAt(0)
                checkF = false
                schedBtn.setVisibility(View.VISIBLE)
            }

            // Sort the list of dates in descending order
            for (i in peopleList) {
                i.dates.sort()
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
