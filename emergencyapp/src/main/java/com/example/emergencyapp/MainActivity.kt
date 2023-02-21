package com.example.emergencyapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.emergencyapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editButton.setOnClickListener {
            // explicit intent
            Intent(this@MainActivity, InputActivity::class.java).run {
                startActivity(this)
            }
        }

        binding.deleteButton.setOnClickListener {
            deleteData()
        }

        binding.contactLayer.setOnClickListener {
            // implicit intent
            with(Intent(Intent.ACTION_VIEW)) {
                val phoneNumber = binding.contactValueTextView.text.toString().replace("-", "")
                data = Uri.parse("tel:$phoneNumber")
                startActivity(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getDataUiUpdate()
    }

    private fun getDataUiUpdate() {
        with(getSharedPreferences(USER_INFORMATION, Context.MODE_PRIVATE)) {
            binding.nameValueTextView.text = getString(NAME, "")
            binding.birthdateValueTextView.text = getString(BIRTHDATE, "")
            binding.bloodTypeValueTextView.text = getString(BLOOD_TYPE, "")
            binding.contactValueTextView.text = getString(CONTACT, "")

            val warning = getString(WARNING, "")
            binding.cautionValueTextView.isVisible = warning.isNullOrEmpty().not()
            binding.cautionTextView.isVisible = warning.isNullOrEmpty().not()
            if(!warning.isNullOrEmpty()) {
                binding.cautionValueTextView.text = warning
            }
        }
    }

    private fun deleteData() {
        with(getSharedPreferences(USER_INFORMATION, MODE_PRIVATE).edit()) {
            clear()
        }.apply()
        getDataUiUpdate()
        Toast.makeText(this, "초기화 완료", Toast.LENGTH_SHORT).show()
    }
}