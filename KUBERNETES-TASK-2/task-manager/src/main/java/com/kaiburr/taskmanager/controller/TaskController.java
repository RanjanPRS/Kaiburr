package com.kaiburr.taskmanager.controller;

import com.kaiburr.taskmanager.models.Task;
import com.kaiburr.taskmanager.models.TaskExecution;
import com.kaiburr.taskmanager.repository.TaskRepository;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

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

    // Create or Update a Task and Execute Command in a New Pod
    @PostMapping
    @PutMapping("/{taskId}")
    public Task createOrUpdateAndExecuteTask(@RequestBody Task task, @PathVariable(required = false) String taskId) 
        throws IOException, ApiException, InterruptedException {
        
        // Validate input
        validateTask(task);

        // Ensure Task ID for PUT requests
        if (taskId != null) task.setId(taskId);

        // Check if the task already exists
        Optional<Task> existingTask = taskRepository.findById(task.getId());
        Task storedTask;
        if (existingTask.isPresent()) {
            // If task exists, update it
            storedTask = existingTask.get();
            storedTask.setCommand(task.getCommand());
        } else {
            // If task does not exist, create a new one
            storedTask = task;
            storedTask.setTaskExecutions(new ArrayList<>());
        }

        // Save the task before execution
        taskRepository.save(storedTask);

        // Setup Kubernetes API Client
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();

        // Define Unique Pod Name (Naming Convention: task-execution-<id>-<owner>-<timestamp>)
        String safeOwner = storedTask.getOwner().replaceAll("[^a-zA-Z0-9]", "").toLowerCase(); // Remove special chars
        String uniqueId = UUID.randomUUID().toString().substring(0, 5); // Generate unique suffix
        String podName = String.format("task-execution-%s-%s-%s", storedTask.getId(), safeOwner, uniqueId);

        // Pod Specs (Keep alive for 30s)

        V1Pod pod = new V1Pod()
            .apiVersion("v1")
            .kind("Pod")
            .metadata(new V1ObjectMeta()
                .name(podName)
                .namespace("default")
                .labels(Collections.singletonMap("task", storedTask.getId())))
            .spec(new V1PodSpec()
                .restartPolicy("Never")
                .containers(Collections.singletonList(
                    new V1Container()
                        .name("executor")
                        .image("busybox")
                        .command(Arrays.asList("sh", "-c", storedTask.getCommand() + "; sleep 30"))
                ))
            );

        // Deploy pod
        api.createNamespacedPod("default", pod, null, null, null, null);

        // Wait for pod to complete execution
        String podStatus = "Running";
        while (podStatus.equals("Running") || podStatus.equals("Pending")) {
            Thread.sleep(3000); // Wait for 3 seconds
            V1Pod updatedPod = api.readNamespacedPod(podName, "default", null);
            podStatus = updatedPod.getStatus().getPhase();  // "Succeeded" or "Failed"
        }

        // Capture logs of the pod
        String podLogs;
        try {
            podLogs = api.readNamespacedPodLog(
                podName,        
                "default",      
                "executor",     
                false,          
                false,          
                null,           
                null,           
                false,          
                null,         
                null,          
                false          
            );
        } catch (ApiException e) {
            podLogs = "Error retrieving logs: " + e.getMessage();
        }

        // Store logs in mongodb
        TaskExecution execution = new TaskExecution();
        execution.setStartTime(new Date());
        execution.setEndTime(new Date());
        execution.setOutput(podLogs);

        // Append logs to task
        storedTask.getTaskExecutions().add(execution);
        taskRepository.save(storedTask);

        // Delete Completed Pod
        api.deleteNamespacedPod(podName, "default", null, null, null, null, null, null);

        return storedTask;
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

    // Method to validate task
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
