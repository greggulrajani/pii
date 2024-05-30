package com.humannative.api.pii.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;

import com.humannative.api.pii.model.CorpusSource;
import com.humannative.api.pii.model.CorpusType;
import com.humannative.api.pii.model.ImageSource;
import com.humannative.api.pii.model.SubmissionDTO;
import com.humannative.api.pii.model.SubmissionReceipt;
import com.humannative.api.pii.model.SubmissionState;
import com.humannative.api.pii.model.TemporalSource;
import com.humannative.api.pii.model.TextSource;
import com.humannative.api.pii.service.SubmissionService;
import com.humannative.security.model.Role;
import com.humannative.security.model.UserRole;
import com.humannative.security.service.SessionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/pii")
public class PiiController {

	private final SessionService sessionService;

	private final SubmissionService submissionService;

	private static final Logger logger = LoggerFactory.getLogger(PiiController.class);

	public PiiController(SessionService sessionService, SubmissionService submissionService) {
		this.sessionService = sessionService;
		this.submissionService = submissionService;
	}

	@GetMapping(value = "/getSubmissions", produces = "application/json")
	@Secured({Role.ADMIN})
	ResponseEntity<List<SubmissionDTO>> getSubmissions(@Valid @NotNull CorpusType corpusType) {
		return ResponseEntity.ok(submissionService.getSubmissions(corpusType));
	}

	@PatchMapping(value = "/updateState", produces = "application/json")
	@Secured({Role.ADMIN})
	ResponseEntity<SubmissionReceipt> updateState(@Valid @NotNull Long submissionId,
			@Valid @NotNull SubmissionState submissionState) {
		return ResponseEntity.ok(submissionService.updateSubmission(submissionId, submissionState));
	}

	@PostMapping(value = "/image", produces = "application/json")
	@Secured({Role.ADMIN, Role.USER})
	ResponseEntity<SubmissionReceipt> submitRequestImage(@RequestBody @Valid ImageSource imageSource) {
		return handlesSubmitRequest(imageSource);
	}

	@PostMapping(value = "/text", produces = "application/json")
	@Secured({Role.ADMIN, Role.USER})
	ResponseEntity<SubmissionReceipt> submitRequstText(@RequestBody @Valid TextSource textSource) {
		return handlesSubmitRequest(textSource);
	}

	@PostMapping(value = "/temporal", produces = "application/json")
	@Secured({Role.ADMIN, Role.USER})
	ResponseEntity<SubmissionReceipt> submitRequstTemporal(@RequestBody @Valid TemporalSource temporalSource) {
		return handlesSubmitRequest(temporalSource);
	}

	@GetMapping(produces = "application/json")
	@Secured({Role.ADMIN, Role.USER})
	ResponseEntity<SubmissionReceipt> getSubmittedRequest(@Valid @NotNull Long submissionId) {
		UserRole userRole = sessionService.getUserRoleFromContext();
		return ResponseEntity.ok(submissionService.getSubmission(userRole, submissionId));
	}

	@DeleteMapping(produces = "application/json")
	@Secured({Role.ADMIN, Role.USER})
	ResponseEntity<SubmissionReceipt> deleteSubmittedRequest(@Valid @NotNull Long submissionId) {
		UserRole userRole = sessionService.getUserRoleFromContext();
		return ResponseEntity.ok(submissionService.deleteSubmission(userRole, submissionId));
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<String> handlerMethodValidationHandler(ResponseStatusException exception) {
		return ResponseEntity.status(exception.getStatusCode()).body(exception.getBody().toString());
	}

	@ExceptionHandler(ResponseStatusException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<String> handleNoSuchElementFoundException(ResponseStatusException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult()
				.getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return errors;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleNullPointer(HttpMessageNotReadableException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> notFound(NoSuchElementException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> handleAccessIsDenied(Throwable ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Throwable.class)
	public ResponseEntity<String> handleThrowable(Throwable ex) {
		logger.error("Null pointer exception", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	private ResponseEntity<SubmissionReceipt> handlesSubmitRequest(CorpusSource corpusSource) {
		UserRole userRole = sessionService.getUserRoleFromContext();
		return ResponseEntity.ok(submissionService.processSubmission(userRole, corpusSource));
	}
}
