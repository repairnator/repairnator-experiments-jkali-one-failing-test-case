package app.models.repository;

import app.models.TimeSlot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TimeSlotRepository extends CrudRepository<TimeSlot, Long> {
    TimeSlot findFirstByOrderById();
}
