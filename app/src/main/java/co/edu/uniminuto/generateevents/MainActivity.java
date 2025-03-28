package co.edu.uniminuto.generateevents;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private EditText editTitle, editDescription;
    private ListView listViewTasks;
    public static ArrayList<Task> taskList = new ArrayList<>();
    private TaskAdapter adapter;
    private static final int REQUEST_EDIT_TASK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTitle = findViewById(R.id.editTaskTitle);
        editDescription = findViewById(R.id.editTaskDescription);
        listViewTasks = findViewById(R.id.listViewTasks);
        Button btnAddTask = findViewById(R.id.btnAddTask);

        // Cargar tareas guardadas
        loadTasks();

        // Inicializar adaptador con lista cargada
        adapter = new TaskAdapter(this, taskList);
        listViewTasks.setAdapter(adapter);

        // Botón para agregar nueva tarea
        btnAddTask.setOnClickListener(v -> addTask());

        // Editar tarea al hacer clic en una tarea
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
            intent.putExtra("taskId", position);
            startActivityForResult(intent, REQUEST_EDIT_TASK);
        });
    }


    private void addTask() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (!title.isEmpty() && !description.isEmpty()) {
            Task task = new Task(taskList.size(), title, description);
            taskList.add(task);
            adapter.notifyDataSetChanged();
            saveTasks();  // Guardar después de agregar tarea
            editTitle.setText("");
            editDescription.setText("");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_TASK && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();  // Actualizar la lista después de eliminar
        }
    }

    // Guardar lista en SharedPreferences
    private void saveTasks() {
        SharedPreferences prefs = getSharedPreferences("TaskPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        // Convertir lista a JSON
        String json = gson.toJson(taskList);
        editor.putString("task_list", json);
        editor.apply();
    }

    // Cargar lista desde SharedPreferences
    private void loadTasks() {
        SharedPreferences prefs = getSharedPreferences("TaskPrefs", MODE_PRIVATE);
        Gson gson = new Gson();

        // Obtener JSON almacenado
        String json = prefs.getString("task_list", null);

        if (json != null) {
            // Convertir JSON a lista de tareas
            taskList = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {}.getType());
        } else {
            taskList = new ArrayList<>();
        }
    }





}