package com.yaskovdev.motivated.stay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {

    private final TaskRepository repo;

    @Autowired
    public TaskController(TaskRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/tasks")
    ResponseEntity listTasks() {
        return new ResponseEntity(repo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/tasks/{id}")
    ResponseEntity getTask(@PathVariable("id") final String taskId) {
        if (!repo.exists(taskId)) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(repo.findOne(taskId), HttpStatus.OK);
    }

    @PostMapping("/tasks")
    ResponseEntity createTask(@RequestBody final Task task) {
        // TODO: add validation
        repo.insert(task);

        return new ResponseEntity(task, HttpStatus.CREATED);
    }

    @PutMapping("/tasks/{id}")
    ResponseEntity updateTask(@PathVariable("id") final String taskId, @RequestBody final Task newTask) {
        // TODO: add validation

        Task task = repo.findOne(taskId);

        if (task == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());

        repo.save(task);

        return new ResponseEntity(task, HttpStatus.OK);
    }

    @DeleteMapping("/tasks/{id}")
    ResponseEntity deleteTask(@PathVariable("id") final String taskId) {
        if (!repo.exists(taskId)) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        repo.delete(taskId);

        return new ResponseEntity(HttpStatus.OK);
    }
}
