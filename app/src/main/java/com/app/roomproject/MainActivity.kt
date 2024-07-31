package com.app.roomproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

    class MainActivity : AppCompatActivity() {

        private lateinit var itemDao: ItemDao
        private lateinit var adapter: ItemAdapter

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val editTextName = findViewById<EditText>(R.id.editTextName)
            val buttonAdd = findViewById<Button>(R.id.buttonAdd)
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

            val database = AppDatabase.getDatabase(this)
            itemDao = database.itemDao()

            adapter = ItemAdapter { item, action -> onItemAction(item, action) }

            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)

            loadData()

            buttonAdd.setOnClickListener {
                val name = editTextName.text.toString()
                if (name.isNotEmpty()) {
                    val newItem = Item(name = name)
                    GlobalScope.launch(Dispatchers.IO) {
                        itemDao.insert(newItem)
                        loadData()
                    }
                    editTextName.text.clear()
                }
            }
        }

        private fun loadData() {
            GlobalScope.launch(Dispatchers.IO) {
                val items = itemDao.getAllItems()
                runOnUiThread {
                    adapter.submitList(items)
                }
            }
        }

        private fun onItemAction(item: Item, action: String) {
            when (action) {
                "editer" -> {
                    val editTextName = findViewById<EditText>(R.id.editTextName)
                    val name = editTextName.text.toString()
                    if(name.isNotEmpty())
                    {
                        val updatedItem = item.copy(name = name)
                        GlobalScope.launch(Dispatchers.IO) {
                            itemDao.update(updatedItem)
                            loadData()
                        }
                    }

                }
                "supprimer" -> GlobalScope.launch(Dispatchers.IO) {
                    itemDao.delete(item)
                    loadData()
                }
            }
        }
    }
