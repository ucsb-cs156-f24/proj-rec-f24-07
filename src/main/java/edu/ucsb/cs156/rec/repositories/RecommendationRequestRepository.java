package edu.ucsb.cs156.rec.repositories; 

import edu.ucsb.cs156.rec.entities.RecommendationRequest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/*
 * The RecommendationRequestRepository is a repository for RecommendationRequest entities.
 */

@Repository
public interface RecommendationRequestRepository extends CrudRepository<RecommendationRequest, Long> {
    /**
   * This method returns all the RecommendationRequests with a given requesterId.
   * @param id id of the requester
   * @return Iterable of RecommendationRequests
   */
  Iterable<RecommendationRequest> findAllByRequesterId(long requesterId);
}
