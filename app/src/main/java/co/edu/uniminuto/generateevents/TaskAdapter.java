package co.edu.uniminuto.generateevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Task task = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        TextView txtTitle = convertView.findViewById(R.id.txtTaskTitle);
        TextView txtDescription = convertView.findViewById(R.id.txtTaskDescription);

        assert task != null;
        txtTitle.setText(task.getTitle());
        txtDescription.setText(task.getDescription());

        return convertView;
    }
}
