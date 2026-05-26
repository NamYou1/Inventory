package yoyo.inventory.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import yoyo.inventory.dto.request.TransferRequest;
import yoyo.inventory.dto.response.TransferResponse;
import yoyo.inventory.services.TransferService;
import yoyo.inventory.config.JwtService;
import yoyo.inventory.constants.ErrorCode;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransferController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtService jwtService;

    @Test
    void testGetById_ShouldReturnTransfer() throws Exception {
        // Arrange
        Long transferId = 1L;
        TransferResponse response = new TransferResponse();
        response.setId(transferId);
        response.setTransferNo("TRF-1001");
        response.setTotal(BigDecimal.valueOf(200));
        response.setGrandTotal(BigDecimal.valueOf(200));
        response.setStatus("PENDING");
        response.setItems(new ArrayList<>());

        when(transferService.getById(transferId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/transfers/{id}", transferId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(ErrorCode.SUCCESS))
                .andExpect(jsonPath("$.payload.id").value(transferId))
                .andExpect(jsonPath("$.payload.transferNo").value("TRF-1001"))
                .andExpect(jsonPath("$.payload.total").value(200));

        verify(transferService).getById(transferId);
    }

    @Test
    void testCreate_ShouldReturnCreatedTransfer() throws Exception {
        // Arrange
        TransferRequest request = new TransferRequest();
        request.setFromStoreId(1L);
        request.setToStoreId(2L);
        request.setItems(new ArrayList<>());

        TransferResponse response = new TransferResponse();
        response.setId(10L);
        response.setTransferNo("TRF-1002");
        response.setTotal(BigDecimal.ZERO);
        response.setGrandTotal(BigDecimal.ZERO);
        response.setStatus("PENDING");
        response.setItems(new ArrayList<>());

        when(transferService.create(any(TransferRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(ErrorCode.SUCCESS))
                .andExpect(jsonPath("$.payload.id").value(10))
                .andExpect(jsonPath("$.payload.transferNo").value("TRF-1002"));

        verify(transferService).create(any(TransferRequest.class));
    }

    @Test
    void testApprove_ShouldReturnApprovedTransfer() throws Exception {
        // Arrange
        Long id = 5L;
        TransferResponse response = new TransferResponse();
        response.setId(id);
        response.setTransferNo("TRF-1005");
        response.setStatus("APPROVED");

        when(transferService.approve(eq(id), anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(patch("/api/transfers/{id}/approve", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(ErrorCode.SUCCESS))
                .andExpect(jsonPath("$.payload.status").value("APPROVED"));

        verify(transferService).approve(eq(id), anyString());
    }

    @Test
    void testComplete_ShouldReturnCompletedTransfer() throws Exception {
        // Arrange
        Long id = 5L;
        TransferResponse response = new TransferResponse();
        response.setId(id);
        response.setTransferNo("TRF-1005");
        response.setStatus("COMPLETED");

        when(transferService.complete(eq(id), anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(patch("/api/transfers/{id}/complete", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(ErrorCode.SUCCESS))
                .andExpect(jsonPath("$.payload.status").value("COMPLETED"));

        verify(transferService).complete(eq(id), anyString());
    }
}
