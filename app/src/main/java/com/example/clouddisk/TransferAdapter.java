package com.example.clouddisk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.ViewHolder> {

    private List<TransferTask> taskList;

    public TransferAdapter(List<TransferTask> taskList) {
        this.taskList = taskList;
    }

    public void updateList(List<TransferTask> newList) {
        this.taskList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transfer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransferTask task = taskList.get(position);
        holder.tvName.setText(task.getName());
        holder.pbTask.setProgress(task.getProgress());
        holder.tvPercent.setText(task.getProgress() + "%");
        holder.tvStatus.setText(task.getStatusText());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPercent, tvStatus;
        ProgressBar pbTask;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_task_name);
            tvPercent = itemView.findViewById(R.id.tv_task_percent);
            tvStatus = itemView.findViewById(R.id.tv_task_status);
            pbTask = itemView.findViewById(R.id.pb_task);
        }
    }
}