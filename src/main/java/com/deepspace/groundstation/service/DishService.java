package com.deepspace.groundstation.service;

import com.deepspace.groundstation.dto.DishRequest;
import com.deepspace.groundstation.dto.DishResponse;
import com.deepspace.groundstation.entity.Dish;
import com.deepspace.groundstation.entity.Vendor;
import com.deepspace.groundstation.exception.ResourceNotFoundException;
import com.deepspace.groundstation.repository.DishRepository;
import com.deepspace.groundstation.repository.VendorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DishService {
    private final DishRepository dishRepository;
    private final VendorRepository vendorRepository;

    public DishService(DishRepository dishRepository, VendorRepository vendorRepository) {
        this.dishRepository = dishRepository;
        this.vendorRepository = vendorRepository;
    }

    public DishResponse create(DishRequest request) {
        Dish dish = new Dish();
        apply(request, dish);
        return toResponse(dishRepository.save(dish));
    }

    @Transactional(readOnly = true)
    public List<DishResponse> getAll() {
        return dishRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DishResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public DishResponse update(Long id, DishRequest request) {
        Dish dish = findEntity(id);
        apply(request, dish);
        return toResponse(dishRepository.save(dish));
    }

    public void delete(Long id) {
        dishRepository.delete(findEntity(id));
    }

    private Dish findEntity(Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish with id " + id + " not found"));
    }

    private void apply(DishRequest request, Dish dish) {
        dish.setDishCode(request.dishCode());
        dish.setDishName(request.dishName());
        dish.setLocation(request.location());
        dish.setDiameterMeters(request.diameterMeters());
        dish.setFrequencyBand(request.frequencyBand());
        dish.setStatus(request.status() == null ? Dish.Status.AVAILABLE : request.status());
        dish.setMaximumElevation(request.maximumElevation());
        dish.setMinimumElevation(request.minimumElevation());
        dish.setActive(request.active() == null || request.active());
        Vendor manufacturer = request.servoManufacturerId() == null ? null
                : vendorRepository.findById(request.servoManufacturerId()).orElseThrow(() ->
                        new ResourceNotFoundException("Vendor with id " + request.servoManufacturerId() + " not found"));
        dish.setServoManufacturer(manufacturer);
    }

    private DishResponse toResponse(Dish dish) {
        Vendor manufacturer = dish.getServoManufacturer();
        return new DishResponse(dish.getId(), dish.getDishCode(), dish.getDishName(), dish.getLocation(),
                dish.getDiameterMeters(), dish.getFrequencyBand(), dish.getStatus(), dish.getMaximumElevation(),
                dish.getMinimumElevation(), manufacturer == null ? null : manufacturer.getId(),
                manufacturer == null ? null : manufacturer.getVendorName(), dish.isActive());
    }
}
