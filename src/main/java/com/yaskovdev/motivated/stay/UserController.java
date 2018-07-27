package com.yaskovdev.motivated.stay;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

    private final UserRepository repository;

    @PostMapping // TODO: can I use PUT
    ResponseEntity<User> create(@RequestBody final User user) {
        return repository.existsById(user.getId()) ? ok(user) : ok(repository.insert(user));
    }

    @GetMapping("/{id}")
    ResponseEntity<User> read(@PathVariable("id") final String id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(notFound().build());
    }

    @GetMapping
    ResponseEntity<List<User>> readAll() {
        return ok(repository.findAll());
    }

    @PostMapping("/{id}/deletion")
    ResponseEntity<User> delete(@PathVariable("id") final String id) {
        repository.deleteById(id);
        return ok().build();
    }
}
