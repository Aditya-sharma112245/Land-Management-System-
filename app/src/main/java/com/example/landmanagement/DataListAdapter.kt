package com.example.landmanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DataListAdapter(
    private var dataList: List<DataModel>,
    private val db: FirebaseFirestore,
    private val userId: String,
    private val onDeleteItem: (String) -> Unit
) : RecyclerView.Adapter<DataListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val landPocketNumberTextView: TextView = itemView.findViewById(R.id.land_pocket_number)
        private val locationTextView: TextView = itemView.findViewById(R.id.location)
        private val latitudeTextView: TextView = itemView.findViewById(R.id.latitude)
        private val areaTextView: TextView = itemView.findViewById(R.id.area)
        private val landUseTextView: TextView = itemView.findViewById(R.id.land_use)
        private val registrationDateTextView: TextView = itemView.findViewById(R.id.registration_date)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(data: DataModel) {
            landPocketNumberTextView.text = "Land Pocket ${adapterPosition + 1}"
            locationTextView.text = data.location
            latitudeTextView.text = data.latitude
            areaTextView.text = data.area
            landUseTextView.text = data.landUse
            registrationDateTextView.text = data.registrationDate

            deleteButton.setOnClickListener {
                onDeleteItem(data.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.land_pocket_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(newDataList: List<DataModel>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
}
