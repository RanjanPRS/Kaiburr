package com.kaiburr.taskmanager.repository;

import com.kaiburr.taskmanager.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByNameContaining(String name);
}
