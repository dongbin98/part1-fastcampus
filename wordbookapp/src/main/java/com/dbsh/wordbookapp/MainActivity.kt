package com.dbsh.wordbookapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dbsh.wordbookapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), WordAdapter.ItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var wordAdapter: WordAdapter
    private var selectedWord: Word? = null
    private var selectedWordPosition = 0

    private val updateAddWordResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val isUpdated = result.data?.getBooleanExtra("isUpdated", false) ?: false

        if (result.resultCode == RESULT_OK && isUpdated) {
            updateAddWord()
        }
    }

    private val updateEditWordResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Tiramisu (Android OS 13에서 Deprecated 됨)
        val editWord = result.data?.getParcelableExtra<Word>("editWord")
//        val editWord = result.data?.getParcelableExtra("updatedWord", Word::class.java)

        Log.d("editWord", editWord?.text.toString())
        if (result.resultCode == RESULT_OK && editWord != null) {
            updateEditWord(editWord)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        binding.addButton.setOnClickListener {
            Intent(this, AddActivity::class.java).run {
                updateAddWordResult.launch(this)
            }
//            Intent(this, AddActivity::class.java).run {
//                startActivity(this)
//            }
        }

        binding.deleteImageView.setOnClickListener {
            delete()
        }

        binding.editImageView.setOnClickListener {
            edit()
        }
    }

    private fun initRecyclerView() {
        wordAdapter = WordAdapter(mutableListOf(), this)
        binding.wordRecyclerView.apply {
            adapter = wordAdapter
            layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            val dividerItemDecoration =
                DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }

        Thread {
            val list = AppDatabase.getInstance(this)?.wordDao()?.getAll() ?: emptyList()
            wordAdapter.list.addAll(list)
            runOnUiThread {
                wordAdapter.notifyDataSetChanged()
            }
        }.start()
    }

    private fun updateAddWord() {
        Thread {
            AppDatabase.getInstance(this)?.wordDao()?.getLatestWord()?.let {
                // index 관련 에러가 있음
                wordAdapter.list.add(0, it)
                runOnUiThread {
                    wordAdapter.notifyDataSetChanged()
                }
            }
        }.start()
    }

    private fun updateEditWord(word: Word) {
        val index = wordAdapter.list.indexOfFirst { it.id == word.id }
        wordAdapter.list[index] = word
        runOnUiThread {
            selectedWord = word
            wordAdapter.notifyItemChanged(index)
            binding.textTextView.text = word.text
            binding.meanTextView.text = word.mean
        }
    }

    private fun delete() {
        if (selectedWord == null) return
        Thread {
            selectedWord?.let {
                AppDatabase.getInstance(this)?.wordDao()?.delete(it)
                runOnUiThread {
                    wordAdapter.list.remove(it)
                    wordAdapter.notifyDataSetChanged()
                    binding.textTextView.text = ""
                    binding.meanTextView.text = ""
                    Toast.makeText(this, "삭제 완료", Toast.LENGTH_SHORT).show()
                }

            }
        }.start()
    }

    private fun edit() {
        if (selectedWord == null) return
        Intent(this, AddActivity::class.java).run {
            putExtra("originWord", selectedWord)
            updateEditWordResult.launch(this)
        }
    }

    override fun onClick(word: Word, position: Int) {
        selectedWord = word
        selectedWordPosition = position
        binding.textTextView.text = selectedWord?.text
        binding.meanTextView.text = selectedWord?.mean
    }
}