// DataListActivity.kt
package com.example.landmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class DataListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var dataListAdapter: DataListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataEntryButton: Button
    private lateinit var totalAreaText: TextView
    private lateinit var residentialAreaText: TextView
    private lateinit var commercialAreaText: TextView
    private lateinit var industrialAreaText: TextView
    private lateinit var agriculturalAreaText: TextView
    private lateinit var transportationAreaText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_list2)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dataListAdapter = DataListAdapter(emptyList(), db, auth.currentUser!!.uid) { documentId ->
            deleteItem(documentId)
        }
        recyclerView.adapter = dataListAdapter

        totalAreaText = findViewById(R.id.total_area_text)
        residentialAreaText = findViewById(R.id.residential_area_text)
        commercialAreaText = findViewById(R.id.commercial_area_text)
        industrialAreaText = findViewById(R.id.industrial_area_text)
        agriculturalAreaText = findViewById(R.id.agricultural_area_text)
        transportationAreaText = findViewById(R.id.transportation_area_text)

        dataEntryButton = findViewById(R.id.btn_data)
        dataEntryButton.setOnClickListener {
            startActivity(Intent(this, DataEntryActivity::class.java))
        }

        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        db.collection("users").document(auth.currentUser!!.uid).collection("data")
            .get()
            .addOnSuccessListener { documents ->
                val dataList = mutableListOf<DataModel>()
                var totalArea = 0.0
                var residentialArea = 0.0
                var commercialArea = 0.0
                var industrialArea = 0.0
                var agriculturalArea = 0.0
                var transportationArea = 0.0

                for (document in documents) {
                    val dataModel = DataModel(
                        document.id,
                        document.getString("location") ?: "",
                        document.getString("latitude") ?: "",
                        document.getString("area") ?: "",
                        document.getString("land_use") ?: "",
                        document.getString("registration_date") ?: ""
                    )
                    dataList.add(dataModel)

                    Log.d(TAG, "DataModel: $dataModel")

                    val area = document.getString("area")?.toDoubleOrNull() ?: 0.0
                    val landUse = dataModel.landUse.trim().lowercase(Locale.getDefault())

                    Log.d(TAG, "Area: $area, Land Use: $landUse")

                    when (landUse) {
                        "residential" -> residentialArea += area
                        "commercial" -> commercialArea += area
                        "industrial" -> industrialArea += area
                        "agricultural" -> agriculturalArea += area
                        "transportation" -> transportationArea += area
                        else -> {
                            Log.w(TAG, "Unknown land use category: $landUse")
                        }
                    }

                    totalArea += area
                }

                dataListAdapter.updateData(dataList)

                totalAreaText.text = "Total Area: $totalArea ha"
                residentialAreaText.text = "Residential Area: $residentialArea ha"
                commercialAreaText.text = "Commercial Area: $commercialArea ha"
                industrialAreaText.text = "Industrial Area: $industrialArea ha"
                agriculturalAreaText.text = "Agricultural Area: $agriculturalArea ha"
                transportationAreaText.text = "Transportation Area: $transportationArea ha"
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to fetch data: ${e.message}")
                Toast.makeText(this, "Failed to load data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteItem(documentId: String) {
        db.collection("users").document(auth.currentUser!!.uid).collection("data").document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                fetchDataFromFirestore()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to delete item: ${e.message}")
                Toast.makeText(this, "Failed to delete item.", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "DataListActivity"
    }
}
