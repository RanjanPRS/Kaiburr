package com.kaiburr.taskmanager.service;

import com.kaiburr.taskmanager.models.Task;
import com.kaiburr.taskmanager.models.TaskExecution;
import com.kaiburr.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public Task createOrUpdateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

    public List<Task> findTasksByName(String name) {
        return taskRepository.findByNameContaining(name);
    }

    public Task executeTask(String id) throws Exception {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            throw new Exception("Task not found");
        }

        Task task = optionalTask.get();
        TaskExecution execution = new TaskExecution();
        execution.setStartTime(new Date());

        // Execute shell command
        Process process = Runtime.getRuntime().exec(task.getCommand());
        process.waitFor();

        execution.setEndTime(new Date());
        execution.setOutput(new String(process.getInputStream().readAllBytes()));

        task.getTaskExecutions().add(execution);
        return taskRepository.save(task);
    }
}
