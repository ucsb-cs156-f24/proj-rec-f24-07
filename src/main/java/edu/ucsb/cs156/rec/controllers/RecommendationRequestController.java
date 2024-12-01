package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.entities.User;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;
import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.services.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/recommendationrequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {
    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;
    @Autowired
    CurrentUserService currentUserService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RequestTypeRepository requestTypeRepository;
    @Autowired
    ObjectMapper mapper;

    /**
     * List all recommendation requests
     * @return a list of all recommendation requests
     * @throws JsonProcessingException if there is an error processing the JSON
     */
    @Operation(summary= "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/alladmin")
    public ResponseEntity<String> allRecommendationRequests() 
        throws JsonProcessingException {
            Iterable<RecommendationRequest> requests = recommendationRequestRepository.findAll();
            String body = mapper.writeValueAsString(requests);
            return ResponseEntity.ok().body(body);
    }
	
    /**
     * List all recommendation requests created by a user with requesterId
     * 
     * @return an iterable of RecommendationRequest
     */
    @Operation(summary= "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> getAllCurrentUser() {
        User currentUser = currentUserService.getUser();
        Long requesterId = currentUser.getId();
        Iterable<RecommendationRequest> requests = recommendationRequestRepository.findAllByRequesterId(requesterId);

        return requests;
    }

    /**
     * Create a new request
     * 
     * @param requesterId   
     * @param professorEmail     
     * @param requestType      
     * @param details          
     * @param neededByDate 
     * @param submissionDate
     * @param completionDate
     * @param status
     * @return                  
     */
    @Operation(summary= "Create a new request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")

    public RecommendationRequest postRecommendationRequest(
            @Parameter(name="professorId") @RequestParam Long professorId,
            @Parameter(name="requestType") @RequestParam String requestType,
            @Parameter(name="details") @RequestParam String details,
            @Parameter(name="neededByDate", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("neededByDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime neededByDate
            )
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        User currentUser = currentUserService.getUser();
        Long requesterId = currentUser.getId();

        User professor = userRepository.findById(professorId)
            .orElseThrow(() -> new EntityNotFoundException(User.class, professorId));

        if (!professor.getAdmin()) {
            throw new IllegalArgumentException("Requested professor is not an admin.");
        }

        requestTypeRepository.findByRequestType(requestType)
                .orElseThrow(() -> new EntityNotFoundException(RequestType.class, requestType));

        recommendationRequest.setRequesterId(requesterId);
        recommendationRequest.setProfessorId(professorId);
        recommendationRequest.setRequestType(requestType);
        recommendationRequest.setDetails(details);

        // completionDate is unassigned until completed, so we set that as null
        LocalDateTime submissionDate = LocalDateTime.now();
        submissionDate = submissionDate.minusNanos(submissionDate.getNano());

        String status = "Pending";

        recommendationRequest.setNeededByDate(neededByDate);
        recommendationRequest.setSubmissionDate(submissionDate);
        recommendationRequest.setStatus(status);

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return savedRecommendationRequest;
    }

    /**
     * Get a single request by id
     * 
     * CHECK if recommendation request belongs to user
     * 
     * @param id the id of the request
     * @return a RecommendationRequest
     */
    @Operation(summary= "Get a single request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name="id") @RequestParam Long id
        ) {
        User currentUser = currentUserService.getUser();
        Long requesterId = currentUser.getId();

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

		if (requesterId != recommendationRequest.getRequesterId()) {
			// throw entity not found to reveal less information to any malicious user
			throw new EntityNotFoundException(RecommendationRequest.class, id);
		}

        return recommendationRequest;
    }
}