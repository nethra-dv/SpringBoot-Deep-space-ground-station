package com.deepspace.groundstation.repository;

import com.deepspace.groundstation.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<Dish, Long> {
}
