package com.rtcarter.perfvaca

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_scheduled.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ScheduledActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled)

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
                daysScheduled.text = all
            }
            catch (e: IOException) {
            }
        }

        daysRemaining.text = daysCount.toString()

       // val preferences = getSharedPreferences ("data", Context.MODE_PRIVATE)
       // val days = preferences.getString("scheduled", "")

       // daysScheduled.text = "$days"

        rightBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
