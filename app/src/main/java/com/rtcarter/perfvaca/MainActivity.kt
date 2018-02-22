package com.rtcarter.perfvaca

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.Intent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = getSharedPreferences ("data", Context.MODE_PRIVATE)


        dateText.text = "${datePicker.month+1}/${datePicker.dayOfMonth}/${datePicker.year}"

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                DatePicker.OnDateChangedListener { datePicker, year, month, day ->
                    dateText.text = "${month+1}/${day}/${year} "
                    successText.text = ""
                })

        scheduleBtn.setOnClickListener {
            val editor = preferences.edit()
            editor.putString("scheduled", dateText.text.toString())
            editor.commit()
            successText.text = "NEW VACATION DAY SCHEDULED"
        }

        leftBtn.setOnClickListener {
            val intent = Intent(this, ScheduledActivity::class.java)
            startActivity(intent)
        }
    }
}
