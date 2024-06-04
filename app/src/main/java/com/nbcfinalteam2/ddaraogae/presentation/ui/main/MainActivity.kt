package com.nbcfinalteam2.ddaraogae.presentation.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.data.datasource.remote.firebase.FirebaseDataSourceImpl
import com.nbcfinalteam2.ddaraogae.data.dto.DogDto
import com.nbcfinalteam2.ddaraogae.data.dto.WalkingDto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repo = FirebaseDataSourceImpl(
            Firebase.database.reference,
            Firebase.firestore
        )

        lifecycleScope.launch {
            repo.insertWalkingData(
                WalkingDto(
                    "asdasdasd",
                    12,
                    1.4,
                    Date(System.currentTimeMillis()),
                    Date.from(LocalDateTime.of(2025, 11, 12, 23, 2, 11).toInstant(ZoneOffset.UTC)),
                    "asdasdasd"
                )
            )
            val dogList = repo.getDogList()
            Log.d("MainActivity", dogList.toString())
        }
    }
}