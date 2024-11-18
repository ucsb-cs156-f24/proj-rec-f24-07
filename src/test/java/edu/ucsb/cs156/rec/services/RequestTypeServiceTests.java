package edu.ucsb.cs156.rec.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RequestType;
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
	void test_trySave_already_exists() {
		RequestType req = RequestType.builder().requestType("Colloquia").build();
		RequestType requestType1 = RequestType.builder().requestType("Colloquia").build();

		when(requestTypeRepository.findAll()).thenReturn(List.of(req));

		Optional<RequestType> requestSaved = requestTypeService.trySave(requestType1);

		verify(requestTypeRepository, times(1)).findAll();
		verify(requestTypeRepository, times(0)).save(requestType1);
		assertTrue(requestSaved.isEmpty());
	}

	@Test
	void test_trySave_not_exists() {
		RequestType req = RequestType.builder().requestType("CS Major").build();
		RequestType requestType1 = RequestType.builder().requestType("Colloquia").build();

		when(requestTypeRepository.findAll()).thenReturn(List.of(req));
		when(requestTypeRepository.save(eq(requestType1))).thenReturn(requestType1);

		Optional<RequestType> requestSaved = requestTypeService.trySave(requestType1);

		verify(requestTypeRepository, times(1)).findAll();
		verify(requestTypeRepository, times(1)).save(requestType1);
		assertTrue(requestSaved.isPresent());
		assertEquals(requestType1, requestSaved.get());
	}

	@Test
	void test_trySaveList() {
		RequestType req0 = RequestType.builder().requestType("CS Major").build();
		RequestType req1 = RequestType.builder().requestType("BS/MS").build();
		RequestType req2 = RequestType.builder().requestType("Office Hours").build();
		RequestType requestType0 = RequestType.builder().id(0L).requestType("Colloquia").build();
		RequestType requestType1 = RequestType.builder().requestType("Office Hours").build();
		RequestType requestType2 = RequestType.builder().requestType("PhD").build();

		when(requestTypeRepository.findAll()).thenReturn(List.of(req0, req1, req2));
		when(requestTypeRepository.save(eq(requestType0))).thenReturn(requestType0);
		when(requestTypeRepository.save(eq(requestType2))).thenReturn(requestType2);

		List<RequestType> requestSaved = requestTypeService.trySaveTypes(List.of(requestType0,requestType1,requestType2));

		verify(requestTypeRepository, times(3)).findAll();
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
