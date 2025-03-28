package co.edu.uniminuto.generateevents;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


public class EditTaskActivity extends Activity {
    private int taskId;
    private EditText editTitle, editDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        editTitle = findViewById(R.id.editTaskTitle);
        editDescription = findViewById(R.id.editTaskDescription);
        Button btnSave = findViewById(R.id.btnSaveTask);
        Button btnDelete = findViewById(R.id.btnDeleteTask);

        taskId = getIntent().getIntExtra("taskId", -1);
        if (taskId != -1) {
            Task task = MainActivity.taskList.get(taskId);
            editTitle.setText(task.getTitle());
            editDescription.setText(task.getDescription());
        }

        btnSave.setOnClickListener(v -> saveChanges());

        btnDelete.setOnClickListener(v -> confirmDelete());
    }


    private void saveChanges() {
        String updatedTitle = editTitle.getText().toString().trim();
        String updatedDescription = editDescription.getText().toString().trim();

        if (!updatedTitle.isEmpty() && !updatedDescription.isEmpty()) {
            Task taskToUpdate = MainActivity.taskList.get(taskId);
            int taskIdToUpdate = taskToUpdate.getId();

            for (int i = 0; i < MainActivity.taskList.size(); i++) {
                if (MainActivity.taskList.get(i).getId() == taskIdToUpdate) {
                    MainActivity.taskList.get(i).setTitle(updatedTitle);
                    MainActivity.taskList.get(i).setDescription(updatedDescription);
                    MainActivity.saveTasks(EditTaskActivity.this);  // Pasa el contexto
                    break;
                }
            }

            setResult(RESULT_OK);
            finish();
        }

    }
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Tarea")
                .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    Task taskToDelete = MainActivity.taskList.get(taskId);
                    int taskIdToDelete = taskToDelete.getId();

                    for (int i = 0; i < MainActivity.taskList.size(); i++) {
                        if (MainActivity.taskList.get(i).getId() == taskIdToDelete) {
                            MainActivity.taskList.remove(i);
                            MainActivity.saveTasks(EditTaskActivity.this);  // Pasa contexto aquí
                            break;
                        }
                    }

                    setResult(RESULT_OK);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }


}