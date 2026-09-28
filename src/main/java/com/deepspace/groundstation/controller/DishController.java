package com.deepspace.groundstation.controller;

import com.deepspace.groundstation.dto.DishRequest;
import com.deepspace.groundstation.dto.DishResponse;
import com.deepspace.groundstation.service.DishService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/space/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping
    public ResponseEntity<DishResponse> create(@Valid @RequestBody DishRequest request) {
        DishResponse response = dishService.create(request);
        return ResponseEntity.created(URI.create("/api/space/dishes/" + response.id())).body(response);
    }

    @GetMapping
    public List<DishResponse> getAll() {
        return dishService.getAll();
    }

    @GetMapping("/{id}")
    public DishResponse getById(@PathVariable Long id) {
        return dishService.getById(id);
    }

    @PutMapping("/{id}")
    public DishResponse update(@PathVariable Long id, @Valid @RequestBody DishRequest request) {
        return dishService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dishService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
