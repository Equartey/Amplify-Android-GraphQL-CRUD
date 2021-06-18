package com.example.androidamplifygraphqlexample

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.example.androidamplifygraphqlexample.datastore.generated.model.Todo


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val bar: ActionBar? = actionBar
        if(bar != null)
            bar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#4287f5")))
        var curTodo: Todo = Todo.builder().name("").description("").build()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val getTodoBtn = findViewById<Button>(R.id.get_todo_btn)
        val createTodoBtn = findViewById<Button>(R.id.create_todo_btn)
        val updateTodoBtn = findViewById<Button>(R.id.update_todo_btn)
        val titleInput = findViewById<EditText>(R.id.todo_title_input)
        val descInput = findViewById<EditText>(R.id.todo_desc_input)
        val lv = findViewById<ListView>(R.id.lt)
        val todos = ArrayList<Todo>()

        val adapter = TodoAdapter(this, android.R.layout.simple_list_item_1, todos)
        lv.adapter = adapter
        getTodos(adapter)

        lv.isClickable = true
        lv.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                val o = lv.getItemAtPosition(position) as Todo
                titleInput.setText(o.name)
                descInput.setText(o.description)
                curTodo = o
            }
        lv.onItemLongClickListener =
            OnItemLongClickListener { _, _, pos, _ ->
                val o = lv.getItemAtPosition(pos) as Todo
                deleteTodo(o, adapter)
                true
            }


        // set on-click listener
        createTodoBtn.setOnClickListener {
            // your code to perform when the user clicks on the button
            Toast.makeText(this@MainActivity, "Creating a todo...", Toast.LENGTH_SHORT).show()
            createTodo(adapter, titleInput, descInput)
        }

        getTodoBtn.setOnClickListener {
            // your code to perform when the user clicks on the button
            Toast.makeText(this@MainActivity, "Getting a todos...", Toast.LENGTH_SHORT).show()
            getTodos(adapter)
        }

        updateTodoBtn.setOnClickListener {
            // your code to perform when the user clicks on the button
            Toast.makeText(this@MainActivity, "Updated a todo...", Toast.LENGTH_SHORT).show()
            updateTodo(adapter, curTodo, titleInput, descInput)
        }
    }

    private class TodoAdapter(context: Context, layoutRes: Int, todos: ArrayList<Todo>): ArrayAdapter<Todo>(context, layoutRes, todos) {
        private val mContext: Context = context
        private val _todos: ArrayList<Todo> = todos
        override fun getCount(): Int {
            return _todos.size
        }

        override fun getItem(position: Int): Todo {
            return _todos[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val todo = this.getItem(position)
            val row  = layoutInflater.inflate(R.layout.todo_row, parent, false)
            val title = row.findViewById<TextView>(R.id.todo_title)
            val desc = row.findViewById<TextView>(R.id.todo_desc)

            title.setText(todo.name)
            desc.setText(todo.description)

            return row
        }

    }

    private fun createTodo(adapter: ArrayAdapter<Todo>, titleInput: EditText, descInput: EditText) {
        val todo = Todo.builder()
            .name(titleInput.text.toString())
            .description(descInput.text.toString())
            .build()

        Amplify.API.mutate(
            ModelMutation.create(todo),
            {
                Log.i("AndroidAmplifyGraphQL", "Added Todo with id: ${it.data.id}")
            },
            { Log.e("AndroidAmplifyGraphQL", "Create failed", it) }
        )

        getTodos(adapter)
    }

    private fun getTodos(adapter: ArrayAdapter<Todo>) {
        Amplify.API.query(
            ModelQuery.list(Todo::class.java),
            { response ->
                val todos = ArrayList<Todo>()

                response.data.forEach { todo ->
                    todos.add(todo)
                    Log.i("AndroidAmplifyGraphQL", todo.name)
                }

                runOnUiThread {
                    adapter.clear()
                    adapter.addAll(todos)
                }
            },
            { Log.e("MyAmplifyApp", "Query failure", it) }
        )
    }

    private fun updateTodo(adapter: ArrayAdapter<Todo>, todo: Todo, titleInput: EditText, descInput: EditText) {
        val todo = Todo.builder()
            .name(titleInput.text.toString())
            .description(descInput.text.toString())
            .id(todo.id)
            .build()

        Amplify.API.mutate(
            ModelMutation.update(todo),
            { Log.i("AndroidAmplifyGraphQL", "Todo with id: ${it.data.id}") },
            { Log.e("AndroidAmplifyGraphQL", "Create failed", it) }
        )

        getTodos(adapter)
    }

    private fun deleteTodo(item: Todo, adapter: ArrayAdapter<Todo>) {

        Amplify.API.mutate(
            ModelMutation.delete(item),
            { Log.i("AndroidAmplifyGraphQL", "Todo with id: ${it.data.id}") },
            { Log.e("AndroidAmplifyGraphQL", "Create failed", it) }
        )

        getTodos(adapter)
    }
}