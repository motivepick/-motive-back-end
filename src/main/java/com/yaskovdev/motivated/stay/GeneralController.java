package com.yaskovdev.motivated.stay;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class GeneralController {

    @GetMapping("/tasks")
    List<Task> tasks() {
        return Arrays.asList(new Task("Move to California", "You need this to earn much money."),
                new Task("Learn Eckhart Tolle", "He has a new book, you can buy it via Amazon."),
                new Task("Become the Oracle Certified Enterprise Architect", "You need to pass 3 exams for this, good luck."),
                new Task("Create the application for the motivation", "Here is is, basically."),
                new Task("Join the AI guild", "You need to write an email to the guild boss."));
    }

    @PostMapping("/tasks")
    Task tasks(@RequestBody final Task task) {
        return task;
    }
}
