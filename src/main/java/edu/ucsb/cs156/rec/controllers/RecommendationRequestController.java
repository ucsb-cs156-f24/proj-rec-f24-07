package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.LocalDateTime;

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/recommendationrequest")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {
    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    /**
     * List all recommendation requests
     * 
     * @return an iterable of RecommendationRequest
     */
    @Operation(summary= "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRecommendationRequests() {
        Iterable<RecommendationRequest> requests = recommendationRequestRepository.findAll();
        return requests;
    }

    /**
     * Create a new request
     * 
     * @param requesterEmail   
     * @param requesterName     
     * @param professorEmail  
     * @param professorName
     * @param requestType      
     * @param details           
     * @param submissionDate
     * @param completionDate
     * @param status
     * @return                  
     */
    @Operation(summary= "Create a new request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendationRequest(
            @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name="requesterName") @RequestParam String requesterName,
            @Parameter(name="professorEmail") @RequestParam String professorEmail,
            @Parameter(name="professorName") @RequestParam String professorName,
            @Parameter(name="requestType") @RequestParam String requestType,
            @Parameter(name="details") @RequestParam String details,
            @Parameter(name="submissionDate", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("submissionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime submissionDate,
            @Parameter(name="completionDate", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("completionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime completionDate,
            @Parameter(name="status") @RequestParam String status)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        // log.info("dateRequested={}", dateRequested);
        // log.info("dateNeeded={}", dateNeeded);

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setRequesterEmail(requesterEmail);
        recommendationRequest.setRequesterName(requesterName);
        recommendationRequest.setProfessorEmail(professorEmail);
        recommendationRequest.setProfessorName(professorName);
        recommendationRequest.setRequestType(requestType);
        recommendationRequest.setDetails(details);
        recommendationRequest.setSubmissionDate(submissionDate);
        recommendationRequest.setCompletionDate(completionDate);
        recommendationRequest.setStatus(status);

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return savedRecommendationRequest;
    }

    /**
     * Get a single request by id
     * 
     * @param id the id of the request
     * @return a RecommendationRequest
     */
    @Operation(summary= "Get a single request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        return recommendationRequest;
    }

    /**
     * Get a single request by professorName
     * 
     * @param professorName the professorName of the request
     * @return a RecommendationRequest
     */
    @Operation(summary= "Get a single request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getByProfessorName(
            @Parameter(name="professorName") @RequestParam String professorName) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findByProfessorName(professorName)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, professorName));

        return recommendationRequest;
    }

    /**
     * Get a single request by studentName
     * 
     * @param studentName the studentName of the request
     * @return a RecommendationRequest
     */
    @Operation(summary= "Get a single request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getByStudentName(
            @Parameter(name="studentName") @RequestParam String studentName) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findByStudentName(studentName)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, studentName));

        return recommendationRequest;
    }

    /**
     * Delete a RecommendationRequest
     * 
     * @param id the id of the request to delete
     * @return a message indicating the request was deleted
     */
    @Operation(summary= "Delete a request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRecommendationRequest(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

                recommendationRequestRepository.delete(recommendationRequest);
        return genericMessage("RecommendationRequest with id %s deleted".formatted(id));
    }

    /**
     * Update a single request
     * 
     * @param id       id of the request to update
     * @param incoming the new date
     * @return the updated date object
     */
    @Operation(summary= "Update a single request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updateRecommendationRequest(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequest.setRequesterEmail(incoming.getRequesterEmail());
        recommendationRequest.setRequesterName(incoming.getRequesterName());
        recommendationRequest.setProfessorEmail(incoming.getProfessorEmail());
        recommendationRequest.setProfessorName(incoming.getProfessorName());
        recommendationRequest.setRequestType(incoming.getRequestType());
        recommendationRequest.setDetails(incoming.getDetails());
        recommendationRequest.setSubmissionDate(incoming.getSubmissionDate());
        recommendationRequest.setCompletionDate(incoming.getCompletionDate());
        recommendationRequest.setStatus(incoming.getStatus());

        recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequest;
    }
}