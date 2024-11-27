package edu.ucsb.cs156.rec.repositories;

import edu.ucsb.cs156.rec.entities.RequestType;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The RequestTypeRepository is a repository for RequestType entities
 */
@Repository
public interface RequestTypeRepository extends CrudRepository<RequestType, Long> {
	Optional<RequestType> findByRequestType(String requestType);
}