package com.yaskovdev.motivated.stay;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController("/users")
public class UserController {

    private final UserRepository repository;

    public UserController(final UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/{id}") // TODO: can I use PUT
    public ResponseEntity<User> create(@PathVariable("id") final String id, @RequestBody final User user) {
        user.setId(id);
        return ok(repository.insert(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> read(@PathVariable("id") final String id) {
        return ok(repository.findOne(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable("id") final String id) {
        repository.delete(id);
        return ok().build();
    }
}
