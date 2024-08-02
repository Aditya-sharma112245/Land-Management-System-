package com.example.landmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DataEntryActivity : AppCompatActivity() {

    private lateinit var btnSave: Button
    private lateinit var btnView: Button
    private lateinit var location: EditText
    private lateinit var latitude: EditText
    private lateinit var area: EditText
    private lateinit var spinnerLandUse: Spinner
    private lateinit var registrationDate: EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Define your array of land use options
    private val landUseOptions = arrayOf("Residential", "Commercial", "Industrial", "Agricultural","Transportation")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_entry)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        btnSave = findViewById(R.id.btn_save)
        btnView = findViewById(R.id.btn_view)
        location = findViewById(R.id.location)
        latitude = findViewById(R.id.latitude)
        area = findViewById(R.id.area)
        spinnerLandUse = findViewById(R.id.spinner_land_use)
        registrationDate = findViewById(R.id.registration_date)

        // Setup spinner adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, landUseOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLandUse.adapter = adapter

        // Setup click listeners for buttons
        btnSave.setOnClickListener {
            saveDataToFirestore()
        }

        btnView.setOnClickListener {
            startActivity(Intent(this, DataListActivity::class.java))
        }
    }

    private fun saveDataToFirestore() {
        val loc = location.text.toString().trim()
        val lat = latitude.text.toString().trim()
        val ar = area.text.toString().trim()
        val landUse = spinnerLandUse.selectedItem.toString()
        val regDate = registrationDate.text.toString().trim()

        // Validate input
        if (loc.isEmpty() || lat.isEmpty() || ar.isEmpty() || regDate.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "location" to loc,
            "latitude" to lat,
            "area" to ar,
            "land_use" to landUse,
            "registration_date" to regDate,
            "timestamp" to System.currentTimeMillis()
        )


        db.collection("users").document(auth.currentUser!!.uid).collection("data")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully.", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        location.text.clear()
        latitude.text.clear()
        area.text.clear()
        spinnerLandUse.setSelection(0)
        registrationDate.text.clear()
    }
}
