package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.TodoItem

class AdapterTodolist (
    val context : Context,
    private val itemClickListener: (TodoItem) -> Unit,
    private val onCheckboxClick: (TodoItem, Boolean) -> Unit, // <-- Add this
    private var useAlternativeLayout: Boolean = false,  // Flag to switch layout
    private val onAlternativeClick: ((String, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<AdapterTodolist.ViewHolderClass>() {




    // Mutable list to allow dynamic updates
    private val datalist = mutableListOf<TodoItem>()

    fun updateData(newData: List<TodoItem>) {
        datalist.clear()
        datalist.addAll(newData)
        notifyDataSetChanged()
    }

    fun addItem(item: TodoItem) {
        datalist.add(item)
        notifyItemInserted(datalist.size - 1)
    }

    fun isEmpty(): Boolean {
        return datalist.isEmpty()
    }



    // Function to switch layout
    fun setAlternativeLayout(useAlternative: Boolean) {
        this.useAlternativeLayout = useAlternative
        notifyDataSetChanged()
    }



    // 👇 This tells RecyclerView to use different view type when layout changes
    override fun getItemViewType(position: Int): Int {
        return if (useAlternativeLayout) 1 else 0
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {

        val layoutRes = if (useAlternativeLayout) {
            R.layout.delete_todo_layout  // Your alternate layout XML
        } else {
            R.layout.todolist_layout  // Default layout XML
        }

        val itemView =
            LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = datalist[position]

        var isclick = false


        // Set text
        holder.text.text = item.title

        if(!useAlternativeLayout){
            if(item.status == "completed"){
                isclick = true
                holder.image.setImageResource(R.drawable.circlefulltick)
            }else{
                isclick = false
                holder.image.setImageResource(R.drawable.circlefortick)
            }

            holder.image.setOnClickListener{
                isclick =! isclick
                if(isclick){
                    holder.image.setImageResource(R.drawable.circlefulltick)
                    item.status = "completed"
                }
                else{
                    holder.image.setImageResource(R.drawable.circlefortick)
                    item.status = "pending"
                }

                onCheckboxClick(item, isclick)
            }
        }else{


            holder.image.setOnClickListener{
                isclick =! isclick
                if(isclick){
                    holder.image.setImageResource(R.drawable.circlefulltick)
//                    item.status = "completed"
                }
                else{
                    holder.image.setImageResource(R.drawable.circlefortick)
//                    item.status = "pending"
                }

//                onCheckboxClick(item, isclick)
                onAlternativeClick?.invoke(item._id, isclick)
            }

        }



        // Handle click
        holder.itemView.setOnClickListener {
            itemClickListener(item)
        }
    }


    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image =itemView.findViewById<ImageButton>(R.id.select)
        val text = itemView.findViewById<TextView>(R.id.text)
    }


}