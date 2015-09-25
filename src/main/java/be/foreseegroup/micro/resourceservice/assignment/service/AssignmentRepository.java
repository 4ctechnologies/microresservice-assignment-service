package be.foreseegroup.micro.resourceservice.assignment.service;

import be.foreseegroup.micro.resourceservice.assignment.model.Assignment;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Kaj on 24/09/15.
 */
public interface AssignmentRepository extends MongoRepository<Assignment, String> {
    Iterable<Assignment> findByConsultantId(String consultantId);
    Iterable<Assignment> findByCustomerId(String customerId);
}
