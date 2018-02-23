package com.rtcarter.perfvaca

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_scheduled.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*

class ScheduledActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled)

        // List of strings to store lines from text file
        var schedList = listOf("")

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
            }
            catch (e: IOException) {
            }
        }

        radio5.text = schedList[0]
        radio4.text = schedList[1]
        radio3.text = schedList[2]
        radio2.text = schedList[3]
        radio1.text = schedList[4]

        daysRemaining.text = daysCount.toString()


        rightBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        clearBtn.setOnClickListener {
            try {
                val file = OutputStreamWriter(openFileOutput("days.txt", Activity.MODE_PRIVATE))

                file.write ("")
                file.close ()
            } catch (e : IOException) {
            }
        }
    }
}
