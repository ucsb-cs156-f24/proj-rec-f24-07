package edu.ucsb.cs156.rec.services;

import edu.ucsb.cs156.rec.entities.RequestType;
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

	private boolean requestTypeExists(String type) {
		boolean alreadyContains = false;
		Iterable<RequestType> typeList = requestTypeRepository.findAll();

		for (RequestType requestType : typeList) {
			if (requestType.getRequestType().equals(type)) {
				alreadyContains = true;
				break;
			}
		}
		return alreadyContains;
	}

	public Optional<RequestType> trySave(RequestType requestType) {
		boolean alreadyContains = requestTypeExists(requestType.getRequestType());

		if (alreadyContains) {
			return Optional.empty();
		}

		return Optional.of(requestTypeRepository.save(requestType));
	}

	public List<RequestType> trySaveTypes(List<RequestType> toSave) {
		List<RequestType> savedTypes = new ArrayList<RequestType>();

		toSave.forEach((requestType) -> {
			Optional<RequestType> saved = trySave(requestType);
			if (saved.isPresent()) {
				savedTypes.add(saved.get());
			}
		});

		return savedTypes;
	}
}