package com.yaskovdev.motivated.stay;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;

    public UserController(final UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping // TODO: can I use PUT
    public ResponseEntity<User> create(@RequestBody final User user) {
        return repository.exists(user.getId()) ? ok(user) : ok(repository.insert(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> read(@PathVariable("id") final String id) {
        return ok(repository.findOne(id));
    }

    @GetMapping
    public ResponseEntity<List<User>> readAll() {
        return ok(repository.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable("id") final String id) {
        repository.delete(id);
        return ok().build();
    }
}
