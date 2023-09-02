

package com.example.masterrunning.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.masterrunning.R
import com.example.masterrunning.TrainingAlarmReceiver
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        if (isUserDataSaved()) {
            loadStartRunningFragment()
        } else {
            loadGenderFragment()
        }

        val alarmMgr = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, TrainingAlarmReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        // Set the alarm to run at 23:50 daily
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 50)

        alarmMgr.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, alarmIntent
        )
    }

    private fun isUserDataSaved(): Boolean {
        val keys = arrayOf(
            "user_goals", "available_days", "height", "weight", "age", "gender", "fitness_level"
        )
        keys.forEach {
            if (!sharedPreferences.contains(it)) {
                return false
            }
        }
        return true
    }

    private fun loadGenderFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, GenderFragment())
        transaction.commit()
    }

    private fun loadStartRunningFragment() {
        // Replace this with your actual StartRunningFragment class
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, StartRunningFragment())
        transaction.commit()
    }
}

private fun FragmentTransaction.replace(
    fragmentContainer: Int,
    startRunningFragment: StartRunningFragment
) {
    TODO("Not yet implemented")
}
