package com.deepspace.groundstation.service;

import com.deepspace.groundstation.dto.SatellitePassRequest;
import com.deepspace.groundstation.dto.SatellitePassResponse;
import com.deepspace.groundstation.entity.Customer;
import com.deepspace.groundstation.entity.Dish;
import com.deepspace.groundstation.entity.SatellitePass;
import com.deepspace.groundstation.exception.ResourceNotFoundException;
import com.deepspace.groundstation.repository.CustomerRepository;
import com.deepspace.groundstation.repository.DishRepository;
import com.deepspace.groundstation.repository.SatellitePassRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SatellitePassService {
    private final SatellitePassRepository satellitePassRepository;
    private final CustomerRepository customerRepository;
    private final DishRepository dishRepository;

    public SatellitePassService(SatellitePassRepository satellitePassRepository,
                                CustomerRepository customerRepository,
                                DishRepository dishRepository) {
        this.satellitePassRepository = satellitePassRepository;
        this.customerRepository = customerRepository;
        this.dishRepository = dishRepository;
    }

    public SatellitePassResponse create(SatellitePassRequest request) {
        SatellitePass satellitePass = new SatellitePass();
        apply(request, satellitePass);
        return toResponse(satellitePassRepository.save(satellitePass));
    }

    @Transactional(readOnly = true)
    public List<SatellitePassResponse> getAll() {
        return satellitePassRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SatellitePassResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public SatellitePassResponse update(Long id, SatellitePassRequest request) {
        SatellitePass satellitePass = findEntity(id);
        apply(request, satellitePass);
        return toResponse(satellitePassRepository.save(satellitePass));
    }

    public void delete(Long id) {
        satellitePassRepository.delete(findEntity(id));
        }

        private SatellitePass findEntity(Long id) {
        return satellitePassRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Satellite pass with id " + id + " not found"));
        }

        private void apply(SatellitePassRequest request, SatellitePass satellitePass) {
        Customer customer = customerRepository.findById(request.customerId()).orElseThrow(() ->
            new ResourceNotFoundException("Customer with id " + request.customerId() + " not found"));
        Dish dish = dishRepository.findById(request.dishId()).orElseThrow(() ->
            new ResourceNotFoundException("Dish with id " + request.dishId() + " not found"));
        satellitePass.setSatelliteName(request.satelliteName());
        satellitePass.setCustomer(customer);
        satellitePass.setDish(dish);
        satellitePass.setPassStart(request.passStart());
        satellitePass.setPassEnd(request.passEnd());
        satellitePass.setFrequency(request.frequency());
        satellitePass.setExpectedDurationMinutes(request.expectedDurationMinutes());
        satellitePass.setStatus(request.status() == null ? SatellitePass.Status.SCHEDULED : request.status());
        }

        private SatellitePassResponse toResponse(SatellitePass satellitePass) {
        return new SatellitePassResponse(satellitePass.getId(), satellitePass.getSatelliteName(),
            satellitePass.getCustomer().getId(), satellitePass.getCustomer().getOperatorName(),
            satellitePass.getDish().getId(), satellitePass.getDish().getDishCode(),
            satellitePass.getPassStart(), satellitePass.getPassEnd(), satellitePass.getFrequency(),
            satellitePass.getExpectedDurationMinutes(), satellitePass.getStatus());
    }
}
