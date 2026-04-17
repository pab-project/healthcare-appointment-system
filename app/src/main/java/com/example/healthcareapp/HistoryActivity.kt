package com.example.healthcareapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val rvHistory = findViewById<RecyclerView>(R.id.rvHistory)
        rvHistory.layoutManager = LinearLayoutManager(this)
        
        val historyList = listOf(
            HistoryItem("Dr. Sarah Johnson", "Konsultasi Umum", "12 Okt 2023", "Selesai"),
            HistoryItem("Dr. Michael Chen", "Cek Gigi", "05 Okt 2023", "Selesai"),
            HistoryItem("Klinik Sehat", "Cek Lab", "28 Sep 2023", "Selesai"),
            HistoryItem("Dr. Amanda", "Spesialis Anak", "15 Sep 2023", "Selesai")
        )
        
        rvHistory.adapter = HistoryAdapter(historyList)
    }

    data class HistoryItem(
        val doctorName: String,
        val service: String,
        val date: String,
        val status: String
    )

    class HistoryAdapter(private val items: List<HistoryItem>) :
        RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvDoctorName: TextView = view.findViewById(R.id.tvDoctorName)
            val tvService: TextView = view.findViewById(R.id.tvService)
            val tvDate: TextView = view.findViewById(R.id.tvDate)
            val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvDoctorName.text = item.doctorName
            holder.tvService.text = item.service
            holder.tvDate.text = item.date
            holder.tvStatus.text = item.status
        }

        override fun getItemCount() = items.size
    }
}
