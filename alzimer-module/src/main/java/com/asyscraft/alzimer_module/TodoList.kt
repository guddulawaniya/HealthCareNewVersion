package com.asyscraft.alzimer_module

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.adapter.AdapterTodolist
import com.careavatar.core_model.alzimer.TodoItem
import com.asyscraft.alzimer_module.databinding.ActivityTodoListBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TodoList : BaseActivity() {

    private lateinit var binding: ActivityTodoListBinding

    private lateinit var adapter: AdapterTodolist

    private val viewModel: YourViewModel by viewModels()
    private var datalist: ArrayList<TodoItem> = ArrayList()
    private val selectedIds = mutableListOf<String>()

    private lateinit var dialog: BottomSheetDialog

    var edit = false
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTodoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.threedot.visibility = View.VISIBLE
        binding.deleteIcon.visibility = View.GONE

        setupRecyclerView()

        binding.add.setOnClickListener{
            selectImage()
        }

        binding.back.setOnClickListener{
            finish()
        }

        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        binding.date.text = currentDate

        binding.edit.setOnClickListener{

            binding.headingtext.setText("Select items")
            binding.edit.visibility = View.GONE
            binding.threedot.visibility = View.GONE
            binding.deleteIcon.visibility = View.VISIBLE

            // 🔁 Change layout here
            adapter.setAlternativeLayout(true)
        }

        binding.deleteIcon.setOnClickListener{

            viewModel.Deletetodos(selectedIds.joinToString(","))
        }



        binding.threedot.setOnClickListener{
            count++

            if(count %2 != 0){
                binding.edit.visibility = View.VISIBLE
                adapter.setAlternativeLayout(false)
            }else{
                binding.edit.visibility = View.GONE
                adapter.setAlternativeLayout(false)
            }
        }


        viewModel.GetallTodos()
        observeResponsegetalltodos()


        observeResponseCreatetodo()
        observeResponseUpdatetodolist()

        observeResponseDeletetodos()

    }



    override fun onResume() {
        super.onResume()
        viewModel.GetallTodos()
    }





    private fun selectImage() {
        dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.addtodolist_drawer, null)
        dialog.setContentView(view)

        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.findViewById<ImageButton>(R.id.cross).setOnClickListener {
            dialog.dismiss()
        }



        val save = view.findViewById<ImageButton>(R.id.save)



        save.setOnClickListener {
            val inputField = dialog.findViewById<EditText>(R.id.usertext)
            val text = inputField?.text.toString()

            viewModel.Createtodo(
                title = text
            )

            if (text.isNotEmpty()) {
//                val newItem = todolist_response(text)
//                adapter.addItem(newItem)

//                val newItem = datalist[0]
//                adapter.addItem(newItem)


                binding.recyclerview.scrollToPosition(adapter.itemCount - 1)
//                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter something", Toast.LENGTH_SHORT).show()
            }
        }



        dialog.show()
    }




    private fun setupRecyclerView() {


        adapter = AdapterTodolist(this,  { game ->
            Toast.makeText(this, "Clicked on: ${game.title}", Toast.LENGTH_SHORT).show()
        },
            onCheckboxClick = { item, isChecked ->
                if (isChecked) {

//                    val status = "completed"

                    viewModel.Updatetodos(id = item._id, title = item.title, status = item.status)
                    Toast.makeText(this, "Marked as completed: ${item.title}", Toast.LENGTH_SHORT).show()
                } else {
//                    val status = "completed"

                    viewModel.Updatetodos(id = item._id, title = item.title, status = item.status)
                    Toast.makeText(this, "Marked as pending: ${item.title}", Toast.LENGTH_SHORT).show()
                }
            },
            onAlternativeClick = { id, isSelected ->
                if (isSelected) {
                    selectedIds.add(id)
                } else {
                    selectedIds.remove(id)
                }
                // Debug print or API call trigger
                println("Selected IDs: $selectedIds")
            }




        )

        binding.recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.recyclerview.adapter = adapter

    }


    private fun observeResponseCreatetodo() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createtodo.observe(this@TodoList) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@TodoList)
                            Log.d("TodoList", "Creating new todo item...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    dialog.dismiss()
                                    viewModel.GetallTodos()
                                    Log.d("TodoList", "✅ Todo created successfully.")
                                } else {
                                    androidExtension.alertBox(data.msg ?: "Failed to create todo", this@TodoList)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("TodoList", "❌ Error creating todo: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@TodoList)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@TodoList)
                        }
                    }
                }
            }
        }
    }




    private fun observeResponsegetalltodos() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.GetallTodo.observe(this@TodoList) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@TodoList)
                            Log.d("TodoList", "🔄 Loading todo list...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    adapter.updateData(data.data)
                                    datalist.clear()
                                    datalist.addAll(data.data)
                                    adapter.notifyDataSetChanged()

                                    // Visibility handling
                                    if (datalist.isEmpty()) {
                                        binding.recyclerview.visibility = View.GONE
                                        binding.dummyImg.visibility = View.VISIBLE
                                    } else {
                                        binding.recyclerview.visibility = View.VISIBLE
                                        binding.dummyImg.visibility = View.GONE
                                    }

                                    Log.d("TodoList", "✅ Todo list loaded.")
                                } else {
                                    androidExtension.alertBox(data.data.toString() ?: "No data received", this@TodoList)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("TodoList", "❌ Error loading todos: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@TodoList)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected state", this@TodoList)
                        }
                    }
                }
            }
        }
    }





    private fun observeResponseUpdatetodolist() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.Updatetodos.observe(this@TodoList) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@TodoList)
                            Log.d("UpdateTodo", "🔄 Updating todo...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    Log.d("UpdateTodo", "✅ Todo updated: ${data.msg}")
                                } else {
                                    androidExtension.alertBox(data.msg ?: "Update failed", this@TodoList)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("UpdateTodo", "❌ Error updating todo: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@TodoList)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected state", this@TodoList)
                        }
                    }
                }
            }
        }
    }


    private fun observeResponseDeletetodos() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.Deletetodos.observe(this@TodoList) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@TodoList)
                            Log.d("DeleteTodo", "🔄 Deleting todo...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    Log.d("DeleteTodo", "✅ Deleted successfully")

                                    binding.deleteIcon.visibility = View.GONE
                                    binding.threedot.visibility = View.VISIBLE
                                    adapter.setAlternativeLayout(false)
                                    binding.headingtext.text = "To Do List"

                                    androidExtension.alertBox("Todo deleted successfully", this@TodoList)
                                    viewModel.GetallTodos()
                                } else {
                                    androidExtension.alertBox(data.msg ?: "Delete failed", this@TodoList)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("DeleteTodo", "❌ Error deleting todo: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@TodoList)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected state", this@TodoList)
                        }
                    }
                }
            }
        }
    }

}