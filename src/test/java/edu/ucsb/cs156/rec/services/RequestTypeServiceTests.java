package edu.ucsb.cs156.rec.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.errors.EntityAlreadyExistsException;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;

import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestTypeServiceTests extends ControllerTestCase {

	@Mock
	RequestTypeRepository requestTypeRepository;

	@InjectMocks
	RequestTypeService requestTypeService = mock(RequestTypeService.class, Answers.CALLS_REAL_METHODS);

	@Test
	void test_trySave_already_exists_throws_exception() {
		RequestType req = RequestType.builder().requestType("Colloquia").build();
		RequestType requestType1 = RequestType.builder().requestType("Colloquia").build();

		when(requestTypeRepository.findByRequestType("Colloquia")).thenReturn(Optional.of(req));

		// Make sure that the bad request throws an exception
		assertThrows(EntityAlreadyExistsException.class, () -> requestTypeService.trySave(requestType1));

		verify(requestTypeRepository, times(1)).findAll();
		verify(requestTypeRepository, times(0)).save(requestType1);
		
	}

	@Test
	void test_trySave_not_exists() {
		RequestType req = RequestType.builder().requestType("CS Major").build();
		RequestType requestType1 = RequestType.builder().requestType("Colloquia").build();

		when(requestTypeRepository.findByRequestType("Colloquia")).thenReturn(Optional.empty());
		when(requestTypeRepository.save(eq(requestType1))).thenReturn(requestType1);

		// Make it a list in order to extract the return value of trySave from the function
		List<RequestType> requestSaved = new ArrayList<RequestType>();
		assertDoesNotThrow(() -> requestSaved.add(requestTypeService.trySave(requestType1)));

		verify(requestTypeRepository, times(1)).findAll();
		verify(requestTypeRepository, times(1)).save(requestType1);
		assertEquals(1, requestSaved.size());
		assertEquals(requestType1, requestSaved.get(0));
	}

	@Test
	void test_trySaveList() {
		RequestType req0 = RequestType.builder().requestType("CS Major").build();
		RequestType req1 = RequestType.builder().requestType("BS/MS").build();
		RequestType req2 = RequestType.builder().requestType("Office Hours").build();
		RequestType requestType0 = RequestType.builder().id(0L).requestType("Colloquia").build();
		RequestType requestType1 = RequestType.builder().requestType("Office Hours").build();
		RequestType requestType2 = RequestType.builder().requestType("PhD").build();

		when(requestTypeRepository.findByRequestType("Colloquia")).thenReturn(Optional.empty());
		when(requestTypeRepository.findByRequestType("Office Hours")).thenReturn(Optional.of(req2));
		when(requestTypeRepository.findByRequestType("PhD")).thenReturn(Optional.empty());
		when(requestTypeRepository.save(eq(requestType0))).thenReturn(requestType0);
		when(requestTypeRepository.save(eq(requestType2))).thenReturn(requestType2);

		List<RequestType> requestSaved = requestTypeService.trySaveTypes(List.of(requestType0,requestType1,requestType2));

		verify(requestTypeRepository, times(1)).findAll();
		verify(requestTypeRepository, times(1)).save(requestType0);
		verify(requestTypeRepository, times(0)).save(requestType1);
		verify(requestTypeRepository, times(1)).save(requestType2);
		assertEquals(List.of(requestType0,requestType2), requestSaved);
	}

	@Test
	void test_constructor() {
		RequestTypeService service = new RequestTypeService();
		assertEquals(RequestTypeService.class, service.getClass());
	}
}