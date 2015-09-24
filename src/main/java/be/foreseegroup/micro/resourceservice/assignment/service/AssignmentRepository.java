package be.foreseegroup.micro.resourceservice.assignment.service;

import be.foreseegroup.micro.resourceservice.assignment.model.Assignment;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Kaj on 24/09/15.
 */
public interface AssignmentRepository extends CrudRepository<Assignment, String> {
}
