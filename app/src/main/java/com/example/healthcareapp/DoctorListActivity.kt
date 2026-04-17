package com.example.healthcareapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DoctorListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

        val listView = findViewById<ListView>(R.id.listDoctor)

        val adapter = DoctorAdapter(DoctorDummy.doctors)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->

            android.widget.Toast.makeText(this, "CLICK WORKS", android.widget.Toast.LENGTH_SHORT).show()

            val doctor = DoctorDummy.doctors[position]

            val intent = Intent(this, DoctorDetailActivity::class.java)
            intent.putExtra("NAME", doctor.name)
            intent.putExtra("SPECIALIZATION", doctor.specialization)
            intent.putExtra("DESCRIPTION", doctor.description)
            intent.putStringArrayListExtra("SCHEDULE", ArrayList(doctor.schedule))

            startActivity(intent)
        }
    }

    // 🔥 Adapter di dalam activity
    inner class DoctorAdapter(private val doctorList: List<Doctor>) : BaseAdapter() {

        override fun getCount(): Int = doctorList.size

        override fun getItem(position: Int): Any = doctorList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val view = convertView ?: LayoutInflater.from(this@DoctorListActivity)
                .inflate(R.layout.item_doctor, parent, false)

            val doctor = doctorList[position]

            val name = view.findViewById<TextView>(R.id.tvDoctorName)
            val spec = view.findViewById<TextView>(R.id.tvDoctorSpec)

            name.text = "👨‍⚕️ ${doctor.name}"
            spec.text = doctor.specialization

            return view
        }
    }
}