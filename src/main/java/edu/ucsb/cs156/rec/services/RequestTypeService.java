package edu.ucsb.cs156.rec.services;

import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.errors.EntityAlreadyExistsException;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This is a service that provides information about the current user.
 * 
 * This is the version of the service used in production.
 */

@Slf4j
@Service("RequestType")
public class RequestTypeService {

	@Autowired
	private RequestTypeRepository requestTypeRepository;

	public RequestType trySave(RequestType requestType) throws EntityAlreadyExistsException {
		Iterable<RequestType> typeList = requestTypeRepository.findAll();

		Optional<RequestType> alreadyContains = requestTypeRepository.findByRequestType(requestType.getRequestType());


		if (alreadyContains.isPresent()) {
			throw new EntityAlreadyExistsException(RequestType.class, requestType.getRequestType());
		}

		return requestTypeRepository.save(requestType);
	}

	public List<RequestType> trySaveTypes(List<RequestType> toSave) {
		List<RequestType> savedTypes = new ArrayList<RequestType>();
		Iterable<RequestType> typeList = requestTypeRepository.findAll();

		toSave.forEach((requestType) -> {
			Optional<RequestType> alreadyContains = requestTypeRepository.findByRequestType(requestType.getRequestType());

			if (!alreadyContains.isPresent()) {
				savedTypes.add(requestTypeRepository.save(requestType));
			}
		});

		return savedTypes;
	}
}