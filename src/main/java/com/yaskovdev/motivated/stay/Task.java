package com.yaskovdev.motivated.stay;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Document
class Task {

    @Id
    private String id;

    @NotBlank
    private String userId;

    private String name;

    private String description;

    private LocalDateTime instantOfCreation;

    private LocalDateTime dueDate;

    private boolean closed;
}
