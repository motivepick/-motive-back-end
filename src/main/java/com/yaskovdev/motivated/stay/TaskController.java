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

@RestController
public class TaskController {

    private final TaskRepository repo;

    @Autowired
    public TaskController(final TaskRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/tasks")
    ResponseEntity<List<Task>> listTasks(@RequestParam(name = "onlyOpen", defaultValue = "true") final boolean onlyOpen) {
        final Sort newestFirst = new Sort(new Order(DESC, "instantOfCreation"));
        if (onlyOpen) {
            final Task probe = new Task();
            probe.setClosed(false);
            return new ResponseEntity<>(repo.findAll(Example.of(probe), newestFirst), OK);
        } else {
            return new ResponseEntity<>(repo.findAll(newestFirst), OK);
        }
    }

    @GetMapping("/tasks/{id}")
    ResponseEntity<Task> getTask(@PathVariable("id") final String taskId) {
        if (!repo.exists(taskId)) {
            return new ResponseEntity<>(NOT_FOUND);
        }

        return new ResponseEntity<>(repo.findOne(taskId), OK);
    }

    @PostMapping("/tasks")
    ResponseEntity<Task> createTask(@RequestBody final Task task) {
        // TODO: add validation
        task.setInstantOfCreation(now());
        repo.insert(task);

        return new ResponseEntity<>(task, CREATED);
    }

    @PutMapping("/tasks/{id}")
    ResponseEntity<Task> updateTask(@PathVariable("id") final String taskId, @RequestBody final Task newTask) {
        // TODO: add validation

        Task task = repo.findOne(taskId);

        if (task == null) {
            return new ResponseEntity<>(NOT_FOUND);
        }

        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());

        repo.save(task);

        return new ResponseEntity<>(task, OK);
    }

    @PostMapping("/closed-tasks/{id}")
    ResponseEntity<Task> closeTask(@PathVariable("id") final String taskId) {
        final Task task = repo.findOne(taskId);
        if (task == null) {
            return new ResponseEntity<>(NOT_FOUND);
        } else {
            task.setClosed(true);
            repo.save(task);
            return new ResponseEntity<>(task, OK);
        }
    }

    @DeleteMapping("/tasks/{id}")
    ResponseEntity deleteTask(@PathVariable("id") final String taskId) {
        if (repo.exists(taskId)) {
            repo.delete(taskId);
            return new ResponseEntity(OK);
        } else {
            return new ResponseEntity(NOT_FOUND);
        }
    }
}
