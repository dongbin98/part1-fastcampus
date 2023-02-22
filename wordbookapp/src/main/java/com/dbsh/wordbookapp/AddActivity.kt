package com.dbsh.wordbookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import com.dbsh.wordbookapp.databinding.ActivityAddBinding
import com.google.android.material.chip.Chip

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private var originWord: Word? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        binding.addButton.setOnClickListener {
            if(originWord == null) add() else edit()
        }
    }

    private fun initView() {
        val types = listOf(
            "명사", "동사", "대명사", "형용사", "부사", "감탄사", "전치사", "접속사"
        )
        binding.typeChipGroup.apply {
            types.forEach { text ->
                addView(createChip(text))
            }
        }

        binding.textTextInputEditText.addTextChangedListener {
            it?.let { text ->
                binding.textTextInputLayout.error = when(text.length) {
                    0 -> "값을 입력해주세요"
                    1 -> "2자 이상을 입력해주세요"
                    else -> null
                }
            }
        }

        originWord = intent.getParcelableExtra("originWord")
        originWord?.let { word ->
            binding.textTextInputEditText.setText(word.text)
            binding.meanTextInputEditText.setText(word.mean)
            val selectedChip = binding.typeChipGroup.children.firstOrNull { (it as Chip).text == word.type } as? Chip
            selectedChip?.isChecked = true
        }
    }

    private fun createChip(text: String): Chip {
        return Chip(this).apply {
            setText(text)
            isCheckable = true
            isClickable = true
        }
    }

    private fun add() {
        val text = binding.textTextInputEditText.text.toString()
        val mean = binding.meanTextInputEditText.text.toString()
        val type = findViewById<Chip>(binding.typeChipGroup.checkedChipId).text.toString()
        val word = Word(text, mean, type)

        // ANR (Database IO작업 시, UI Thread와 별개로 가져가야 함)
        Thread {
            AppDatabase.getInstance(this)?.wordDao()?.insert(word)
            runOnUiThread {
                Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent().putExtra("isUpdated", true)
            setResult(RESULT_OK, intent)
            finish()
        }.start()
    }

    private fun edit() {
        val text = binding.textTextInputEditText.text.toString()
        val mean = binding.meanTextInputEditText.text.toString()
        val type = findViewById<Chip>(binding.typeChipGroup.checkedChipId).text.toString()
        val editWord = originWord?.copy(text = text, mean = mean, type= type)

        Thread {
            editWord?.let { word ->
                AppDatabase.getInstance(this)?.wordDao()?.update(word)
                runOnUiThread {
                    Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show()
                }
            }
            Log.d("editWord", editWord?.text.toString())
            val intent = Intent().putExtra("editWord", editWord)
            setResult(RESULT_OK, intent)
            finish()
        }.start()
    }
}