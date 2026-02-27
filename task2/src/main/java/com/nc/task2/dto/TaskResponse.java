package com.nc.task2.dto;

import com.nc.task2.entity.Task;
import lombok.Data;

@Data
public class TaskResponse {

    // Getters and setters (or use Lombok @Getter/@Setter)
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Long userId; // only the user ID

    // Constructor mapping from Task entity
    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.completed = task.isCompleted();
        this.userId = task.getUser().getId();
    }

}