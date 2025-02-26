package com.kaiburr.taskmanager.controller;

import com.kaiburr.taskmanager.models.Task;
import com.kaiburr.taskmanager.models.TaskExecution;
import com.kaiburr.taskmanager.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:3000")  // Allow frontend access
@RestController
@RequestMapping("/tasks")  // Base endpoint for task management
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // List of Dangerous Commands
    private static final List<String> DANGEROUS_COMMANDS = Arrays.asList(
        "rm -rf", "shutdown", "poweroff", "reboot", "mkfs", "wget", "curl", 
        "dd", "mv /", "cp /", "echo >", "chmod 777", "chown root", 
        "killall", "kill -9", "iptables", "nano /etc/passwd", "vim /etc/shadow"
    );
    
    // Test API availability
    @GetMapping("/ping")
    public String ping() {
        return "Task Manager API is running!";
    }

    // Fetch all tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Get a task by ID
    @GetMapping("/{taskId}")
    public Task getTaskById(@PathVariable String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    // Search tasks by name
    @GetMapping("/search")
    public List<Task> searchTasksByName(@RequestParam String name) {
        return taskRepository.findByNameContaining(name);
    }

    // Create and Execute a new task
    @PostMapping
    public Task createAndExecuteTask(@RequestBody Task task) {
        // Validate input constraints
        validateTask(task);

        // Ensure taskExecutions list is initialized
        if (task.getTaskExecutions() == null) {
            task.setTaskExecutions(new ArrayList<>());
        }

        // Create a TaskExecution immediately upon task creation
        TaskExecution execution = new TaskExecution();
        execution.setStartTime(new Date());

        // Execute the command and capture output
        String output = executeShellCommand(task.getCommand());
        execution.setOutput(output);

        execution.setEndTime(new Date());

        // Add execution record
        task.getTaskExecutions().add(execution);

        // Save task with execution details
        return taskRepository.save(task);
    }

    // Update an existing task
    @PutMapping("/{taskId}")
public Task updateTask(@PathVariable String taskId, @RequestBody Task updatedTask) {
    Optional<Task> optionalTask = taskRepository.findById(taskId);
    if (optionalTask.isPresent()) {
        validateTask(updatedTask);
        Task existingTask = optionalTask.get();
        existingTask.setName(updatedTask.getName());
        existingTask.setOwner(updatedTask.getOwner());
        existingTask.setCommand(updatedTask.getCommand());

        // Execute the updated command and capture output
        TaskExecution execution = new TaskExecution();
        execution.setStartTime(new Date());
        execution.setOutput(executeShellCommand(updatedTask.getCommand()));
        execution.setEndTime(new Date());

        // Ensure executions list is initialized
        if (existingTask.getTaskExecutions() == null) {
            existingTask.setTaskExecutions(new ArrayList<>());
        }

        // Add execution record
        existingTask.getTaskExecutions().add(execution);

        return taskRepository.save(existingTask);
    } else {
            throw new RuntimeException("Task not found");
        }
    }

    // Delete a task
    @DeleteMapping("/{taskId}")
    public String deleteTask(@PathVariable String taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return "Task deleted successfully.";
        } else {
            throw new RuntimeException("Task not found");
        }
    }

    // Execute Shell Command Securely
    private String executeShellCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read command output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            return "Error executing command: " + e.getMessage();
        }

        return output.toString().trim();
    }

    // Method to validate task and prevent dangerous commands
    private void validateTask(Task task) {
        if (task.getId() == null || task.getId().trim().isEmpty()) {
            throw new RuntimeException("Task ID cannot be empty");
        }
        if (task.getName() == null || task.getName().trim().isEmpty()) {
            throw new RuntimeException("Task name cannot be empty");
        }
        if (task.getOwner() == null || task.getOwner().trim().isEmpty()) {
            throw new RuntimeException("Task owner cannot be empty");
        }
        if (task.getCommand() == null || task.getCommand().trim().isEmpty()) {
            throw new RuntimeException("Task command cannot be empty");
        }

        // Check if command is dangerous
        for (String dangerousCmd : DANGEROUS_COMMANDS) {
            if (task.getCommand().toLowerCase().contains(dangerousCmd)) {
                throw new RuntimeException("Unsafe command detected!");
            }
        }
    }
}
