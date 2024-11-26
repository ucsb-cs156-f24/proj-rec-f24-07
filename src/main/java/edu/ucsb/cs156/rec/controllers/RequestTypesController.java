package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;
import edu.ucsb.cs156.rec.services.RequestTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a REST controller for Request Types
 */

@Tag(name = "RequestTypes")
@RequestMapping("/api/requesttypes")
@RestController
@Slf4j
public class RequestTypesController extends ApiController {

    @Autowired
    RequestTypeRepository requestTypeRepository;

	@Autowired
	RequestTypeService requestTypeService;

    /**
     * This method returns a list of all request types.
     * @return a list of all request types
     */
    @Operation(summary = "List all request types")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RequestType> allRequestTypes() {
        Iterable<RequestType> requestTypes = requestTypeRepository.findAll();
        return requestTypes;
    }

    /**
     * This method returns a single request type.
     * @param id id of the request type to get
     * @return a single request type
     */
    @Operation(summary = "Get a single request type")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RequestType getById(
            @Parameter(name = "id") @RequestParam Long id) {
		RequestType requestType = requestTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RequestType.class, id));

        return requestType;
    }

    /**
     * This method creates a new request type. Accessible only to users with the role "ROLE_ADMIN" or "ROLE_INSTRUCTOR".
     * @param requestType description of the request type
     * @return the save request type (with it's id field set by the database)
     */
    @Operation(summary = "Create a new request type")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_INSTRUCTOR')")
    @PostMapping("/post")
    public RequestType postRequestType(
            @Parameter(name = "requestType") @RequestParam String requestType) {

        RequestType savingRequestType = new RequestType();
        savingRequestType.setRequestType(requestType);

        return requestTypeService.trySave(savingRequestType);
    }
}