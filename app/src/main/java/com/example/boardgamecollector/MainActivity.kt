package com.example.boardgamecollector

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.Instant
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var hello: TextView
    private lateinit var numOfGames: TextView
    private lateinit var numOfExtensions: TextView
    private lateinit var dateOfLastSync: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello = findViewById(R.id.main_hello)
        numOfGames = findViewById(R.id.main_num_of_games)
        numOfExtensions = findViewById(R.id.main_num_of_extensions)
        dateOfLastSync = findViewById(R.id.main_date_of_last_sync)

        hello.text = getString(R.string.main_hello, "World")
        numOfGames.text = getString(R.string.main_num_of_games, 3)
        numOfExtensions.text = getString(R.string.main_num_of_extensions, 4)
        dateOfLastSync.text = getString(R.string.main_date_of_last_sync, Date.from(Instant.now()))
    }
}