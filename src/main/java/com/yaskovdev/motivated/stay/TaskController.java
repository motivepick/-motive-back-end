package com.yaskovdev.motivated.stay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.time.Instant.now;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class TaskController {

    private final TaskRepository repo;

    @Autowired
    public TaskController(final TaskRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/users/{userId}/tasks")
    ResponseEntity<List<Task>> listTasks(@PathVariable("userId") final String userId,
            @RequestParam(name = "onlyOpen", defaultValue = "true") final boolean onlyOpen) {
        final Sort newestFirst = new Sort(new Order(DESC, "instantOfCreation"));
        if (onlyOpen) {
            final Task probe = new Task();
            probe.setUserId(userId);
            probe.setClosed(false);
            return ok(repo.findAll(Example.of(probe), newestFirst));
        } else {
            final Task probe = new Task();
            probe.setUserId(userId);
            return ok(repo.findAll(Example.of(probe), newestFirst));
        }
    }

    @GetMapping("/tasks/{id}")
    ResponseEntity<Task> getTask(@PathVariable("id") final String taskId) {
        return repo.findById(taskId).map(ResponseEntity::ok).orElse(notFound().build());
    }

    @PostMapping("/tasks")
    ResponseEntity<Task> createTask(@RequestBody final Task task) {
        task.setInstantOfCreation(now());
        repo.insert(task);
        return new ResponseEntity<>(task, CREATED);
    }

    @PutMapping("/tasks/{id}")
    ResponseEntity<Task> updateTask(@PathVariable("id") final String taskId, @RequestBody final Task newTask) {
        return repo.findById(taskId).map(t -> ok(save(t, newTask))).orElse(notFound().build());
    }

    @PostMapping("/closed-tasks/{id}")
    ResponseEntity<Task> closeTask(@PathVariable("id") final String taskId) {
        return repo.findById(taskId).map(t -> ok(close(t))).orElse(notFound().build());
    }

    @DeleteMapping("/tasks/{id}")
    ResponseEntity deleteTask(@PathVariable("id") final String taskId) {
        if (repo.existsById(taskId)) {
            repo.deleteById(taskId);
            return new ResponseEntity(OK);
        } else {
            return new ResponseEntity(NOT_FOUND);
        }
    }

    private Task save(final Task task, final Task newTask) {
        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
        return repo.save(task);
    }

    private Task close(final Task task) {
        task.setClosed(true);
        return repo.save(task);
    }
}
