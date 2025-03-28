package co.edu.uniminuto.generateevents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private EditText editTitle, editDescription;
    private ListView listViewTasks;
    public static ArrayList<Task> taskList = new ArrayList<>();
    private TaskAdapter adapter;
    private static final int REQUEST_EDIT_TASK = 1;
    private SearchView searchViewTasks;
    private ArrayList<Task> filteredList;  // Lista para resultados filtrados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        editTitle = findViewById(R.id.editTaskTitle);
        editDescription = findViewById(R.id.editTaskDescription);
        listViewTasks = findViewById(R.id.listViewTasks);
        Button btnAddTask = findViewById(R.id.btnAddTask);
        searchViewTasks = findViewById(R.id.searchViewTasks);
        filteredList = new ArrayList<>(taskList);

        // Cargar tareas desde SharedPreferences
        loadTasks();

        // Inicializar adaptador
        adapter = new TaskAdapter(this, taskList);
        listViewTasks.setAdapter(adapter);

        // Agregar tarea
        btnAddTask.setOnClickListener(v -> {
            addTask();
            saveTasks(MainActivity.this);  // Pasar contexto aquí
        });

        // Editar tarea al hacer clic en una tarea
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
            intent.putExtra("taskId", position);
            startActivityForResult(intent, REQUEST_EDIT_TASK);
        });

        // Configurar SearchView
        searchViewTasks.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTasks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTasks(newText);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_TASK && resultCode == RESULT_OK) {
            loadTasks();  // Recargar tareas desde SharedPreferences

            // Sincronizar filteredList con taskList después de editar/eliminar
            filteredList.clear();
            filteredList.addAll(taskList);

            // Actualizar adaptador para reflejar cambios
            adapter = new TaskAdapter(this, filteredList);
            listViewTasks.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }




    private void addTask() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (!title.isEmpty() && !description.isEmpty()) {
            Task task = new Task(taskList.size(), title, description);
            taskList.add(task);  // Agregar tarea a taskList

            // Actualizar lista filtrada SIEMPRE después de agregar tarea
            filteredList.clear();
            filteredList.addAll(taskList);

            // Actualizar adaptador para reflejar cambios
            adapter.notifyDataSetChanged();
            saveTasks(MainActivity.this);

            // Limpiar inputs después de agregar
            editTitle.setText("");
            editDescription.setText("");
            // Limpiar SearchView
            searchViewTasks.setQuery("", false);  // Limpiar búsqueda después de agregar
            filterTasks("");  // Restablecer lista completa

        }
    }



    // Guardar lista de tareas
    public static void saveTasks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(taskList);
        editor.putString("task_list", json);
        editor.apply();
    }

    // Cargar tareas guardadas
    private void loadTasks() {
        SharedPreferences prefs = getSharedPreferences("TaskPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("task_list", null);

        if (json != null) {
            taskList = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {}.getType());
        } else {
            taskList = new ArrayList<>();
        }
    }

    // Filtrar tareas

    private void filterTasks(String query) {
        filteredList.clear();  // Limpiar resultados previos

        if (query.isEmpty()) {
            filteredList.addAll(taskList);  // Si no hay búsqueda, mostrar todas las tareas
        } else {
            for (Task task : taskList) {
                if (task.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        task.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(task);
                }
            }
        }

        // Actualizar adaptador después de filtrar
        adapter = new TaskAdapter(this, filteredList);
        listViewTasks.setAdapter(adapter);
        adapter.notifyDataSetChanged();  // Actualizar lista visible
    }



}
