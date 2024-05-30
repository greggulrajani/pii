package com.humannative.api.pii.controller;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.humannative.api.pii.model.CorpusType;
import com.humannative.api.pii.model.ImageSource;
import com.humannative.api.pii.model.Reason;
import com.humannative.api.pii.model.SubmissionDTO;
import com.humannative.api.pii.model.SubmissionReceipt;
import com.humannative.api.pii.model.SubmissionState;
import com.humannative.api.pii.model.TextSource;
import com.humannative.api.pii.model.Violation;
import com.humannative.api.pii.service.SubmissionService;
import com.humannative.security.model.Role;
import com.humannative.security.model.UserDAO;
import com.humannative.security.model.UserRole;
import com.humannative.security.service.JWTService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PiiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PiiController controller;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private JWTService jwtService;

	@MockBean
	private UserDAO userDAO;

	@MockBean
	private SubmissionService submissionService;

	public static final String ADMIN_USER_NAME = "adminUser";

	private static final UserRole ADMIN_USER = new UserRole(ADMIN_USER_NAME,
			EnumSet.of(Role.ROLE_USER, Role.ROLE_ADMIN));

	public static final String API_USER_NANE = "apiUser";

	private static final UserRole API_USER = new UserRole("apiUser", EnumSet.of(Role.ROLE_USER));

	private static final TextSource TEXT_SOURCE_1 = new TextSource(123L, 456L, Reason.BIOMETRIC_DATA, Violation.CCPA,
			123L, 444L);

	@Test
	void verify_getSubmissions_returns_json_for_admin_user_with_correct_token() throws Exception {
		setUpAdminUser();

		List<SubmissionDTO> returnSubmissionDTOs = List
				.of(new SubmissionDTO("apiUser", LocalDate.of(2001, 2, 3), SubmissionState.PENDING, TEXT_SOURCE_1));

		when(submissionService.getSubmissions(CorpusType.TEXT)).thenReturn(returnSubmissionDTOs);

		mockMvc
				.perform(get("/pii/getSubmissions").header("Authorization", "Bearer 123")
						.queryParam("corpusType", CorpusType.TEXT.name()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(mapper.writeValueAsString(returnSubmissionDTOs)));
	}

	@Test
	void verify_getSubmissions_returns_403_for_api_user_with_correct_token() throws Exception {
		setUpApiUser();

		mockMvc
				.perform(get("/pii/getSubmissions").header("Authorization", "Bearer 123")
						.queryParam("corpusType", CorpusType.TEXT.name()))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	void verify_getSubmissions_returns_validation_error_for_malformed_param_with_api_user() throws Exception {
		setUpAdminUser();

		List<SubmissionDTO> returnSubmissionDTOs = List
				.of(new SubmissionDTO("apiUser", LocalDate.of(2001, 2, 3), SubmissionState.PENDING, TEXT_SOURCE_1));
		when(submissionService.getSubmissions(CorpusType.TEXT)).thenReturn(returnSubmissionDTOs);

		mockMvc
				.perform(get("/pii/getSubmissions").header("Authorization", "Bearer 123")
						.queryParam("bogusParm", CorpusType.TEXT.name()))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("ProblemDetail")));
	}

	@Test
	void verify_updateState_returns_json_for_admin_user_with_correct_token() throws Exception {
		setUpAdminUser();

		SubmissionReceipt receipt = new SubmissionReceipt(22L, SubmissionState.PENDING);
		when(submissionService.updateSubmission(anyLong(), any(SubmissionState.class))).thenReturn(receipt);

		mockMvc
				.perform(patch("/pii/updateState").header("Authorization", "Bearer 123")
						.queryParam("submissionId", "11")
						.queryParam("submissionState", SubmissionState.PENDING.name()))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void verify_updateState_returns_403_for_api_user_with_correct_token() throws Exception {
		setUpApiUser();

		mockMvc
				.perform(patch("/pii/updateState").header("Authorization", "Bearer 123")
						.queryParam("submissionId", "11")
						.queryParam("submissionState", SubmissionState.PENDING.name()))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	void verify_updateState_is_passed_correct_username_from() throws Exception {
		setUpAdminUser();

		SubmissionReceipt receipt = new SubmissionReceipt(22L, SubmissionState.PENDING);
		when(submissionService.updateSubmission(anyLong(), any(SubmissionState.class))).thenReturn(receipt);

		mockMvc.perform(patch("/pii/updateState").header("Authorization", "Bearer 123"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void verify_updateState_returns_validation_error_for_malformed_param_with_api_user() throws Exception {
		setUpAdminUser();

		SubmissionReceipt receipt = new SubmissionReceipt(22L, SubmissionState.PENDING);
		when(submissionService.updateSubmission(anyLong(), any(SubmissionState.class))).thenReturn(receipt);

		mockMvc.perform(patch("/pii/updateState").header("Authorization", "Bearer 123"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void verify_postImage_returns_json_for_api_user_with_correct_token() throws Exception {
		setUpApiUser();

		SubmissionReceipt receipt = new SubmissionReceipt(12, SubmissionState.PENDING);

		when(submissionService.processSubmission(any(UserRole.class), any(ImageSource.class))).thenReturn(receipt);

		ImageSource imageSource = new ImageSource(11L, 22L, Reason.BIOMETRIC_DATA, Violation.CCPA, 1L, 2L, 100L, 200L);
		mockMvc
				.perform(post("/pii/image").header("Authorization", "Bearer 123")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(imageSource)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(mapper.writeValueAsString(receipt)));
	}

	@Test
	void verify_postImage_returns_validation_error_for_malformed_param_with_api_user() throws Exception {
		setUpApiUser();

		String imageSourceString = "{\"dataId\":22,\"violation\":\"CCPA\",\"x\":1,\"y\":2,\"width\":100,\"height\":200}";
		mockMvc
				.perform(post("/pii/image").header("Authorization", "Bearer 123")
						.contentType(MediaType.APPLICATION_JSON)
						.content(imageSourceString))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("must not be null")));
	}

	@Test
	void verify_postImage_returns_validation_error_for_wrong_source_with_api_user() throws Exception {
		setUpApiUser();

		TextSource textSource = new TextSource(1L, 2L, Reason.BIOMETRIC_DATA, Violation.CCPA, 23L, 1232L);
		mockMvc
				.perform(post("/pii/image").header("Authorization", "Bearer 123")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(textSource)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("must not be null")));
	}

	/*
	 * Other source post tests omitted for brevity ...
	 */

	@Test
	void verify_getSubmittedRequest_returns_json_for_admin_user_with_correct_token() throws Exception {
		setUpAdminUser();

		SubmissionReceipt submissionReceipt = new SubmissionReceipt(12L, SubmissionState.PENDING);
		when(submissionService.getSubmission(any(UserRole.class), anyLong())).thenReturn(submissionReceipt);
		mockMvc.perform(get("/pii").header("Authorization", "Bearer 123").queryParam("submissionId", "11"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(mapper.writeValueAsString(submissionReceipt)));
	}

	@Test
	void verify_getSubmittedRequest_returns_json_for_api_user_with_correct_token() throws Exception {
		setUpApiUser();

		SubmissionReceipt submissionReceipt = new SubmissionReceipt(12L, SubmissionState.PENDING);
		when(submissionService.getSubmission(any(UserRole.class), anyLong())).thenReturn(submissionReceipt);
		mockMvc.perform(get("/pii").header("Authorization", "Bearer 123").queryParam("submissionId", "11"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(mapper.writeValueAsString(submissionReceipt)));
	}

	@Test
	void verify_getSubmittedRequest_validation_error_for_malformed_param_with_api_user() throws Exception {
		setUpApiUser();

		SubmissionReceipt submissionReceipt = new SubmissionReceipt(12L, SubmissionState.PENDING);
		when(submissionService.getSubmission(any(UserRole.class), anyLong())).thenReturn(submissionReceipt);
		mockMvc.perform(get("/pii").header("Authorization", "Bearer 123").queryParam("subonId", "11"))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("ProblemDetail")));
	}

	@Test
	void verify_deleteSubmittedRequest_returns_json_for_admin_user_with_correct_token() throws Exception {
		setUpAdminUser();

		SubmissionReceipt submissionReceipt = new SubmissionReceipt(12L, SubmissionState.PENDING);
		when(submissionService.deleteSubmission(any(UserRole.class), anyLong())).thenReturn(submissionReceipt);
		mockMvc.perform(delete("/pii").header("Authorization", "Bearer 123").queryParam("submissionId", "11"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(mapper.writeValueAsString(submissionReceipt)));
	}

	@Test
	void verify_deleteSubmittedRequest_returns_json_for_api_user_with_correct_token() throws Exception {
		setUpApiUser();

		SubmissionReceipt submissionReceipt = new SubmissionReceipt(12L, SubmissionState.PENDING);
		when(submissionService.deleteSubmission(any(UserRole.class), anyLong())).thenReturn(submissionReceipt);
		mockMvc.perform(delete("/pii").header("Authorization", "Bearer 123").queryParam("submissionId", "11"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(mapper.writeValueAsString(submissionReceipt)));
	}

	@Test
	void verify_InternalServerError_is_returned_for_api_user_with_correct_token() throws Exception {
		setUpApiUser();

		when(submissionService.deleteSubmission(any(UserRole.class), anyLong()))
				.thenThrow(new NullPointerException("npe!"));
		mockMvc.perform(delete("/pii").header("Authorization", "Bearer 123").queryParam("submissionId", "11"))
				.andDo(print())
				.andExpect(status().isInternalServerError());
	}

	@Test
	void verify_BadRequest_is_returned_for_HttpMessageNotReadableException_exception_and_api_user_with_correct_token()
			throws Exception {
		setUpApiUser();

		when(submissionService.deleteSubmission(any(UserRole.class), anyLong()))
				.thenThrow(new HttpMessageNotReadableException("hmnoe!"));
		mockMvc.perform(delete("/pii").header("Authorization", "Bearer 123").queryParam("submissionId", "11"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void verify_404_is_returned_for_ResponseStatusException_and_api_user_with_correct_token() throws Exception {
		setUpApiUser();

		when(submissionService.deleteSubmission(any(UserRole.class), anyLong()))
				.thenThrow(new ResponseStatusException(HttpStatusCode.valueOf(500)));
		mockMvc.perform(delete("/pii").header("Authorization", "Bearer 123").queryParam("submissionId", "11"))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	private void setUpAdminUser() {
		when(jwtService.validateAndGetUserFromToken(any())).thenReturn(ADMIN_USER_NAME);
		when(userDAO.getUser(any())).thenReturn(ADMIN_USER);
	}

	private void setUpApiUser() {
		when(jwtService.validateAndGetUserFromToken(any())).thenReturn(API_USER_NANE);
		when(userDAO.getUser(any())).thenReturn(API_USER);
	}

}
