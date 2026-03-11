package org.example.userservice.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.userservice.dto.PaymentCardRequestDto;
import org.example.userservice.dto.UserRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class PaymentCardControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setName("Ivan");
        userRequestDto.setSurname("Petrov");
        userRequestDto.setBirthDate(LocalDate.of(2000, 5, 12));
        userRequestDto.setEmail("ivan" + System.nanoTime() + "@test.com");
        userRequestDto.setActive(true);
    }

    @Test
    void createCardAndGetByUserId_shouldWork() throws Exception {
        String userResponse = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode userJson = objectMapper.readTree(userResponse);
        Long userId = userJson.get("id").asLong();

        assertThat(userId).isNotNull();

        PaymentCardRequestDto paymentCardRequestDto = new PaymentCardRequestDto();
        paymentCardRequestDto.setNumber("1111222233334444");
        paymentCardRequestDto.setHolder("IVAN PETROV");
        paymentCardRequestDto.setExpirationDate(LocalDate.of(2030, 12, 31));
        paymentCardRequestDto.setActive(true);
        paymentCardRequestDto.setUserId(userId);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentCardRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.holder").value("IVAN PETROV"))
                .andExpect(jsonPath("$.userId").value(userId));

        mockMvc.perform(get("/api/cards/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].holder").value("IVAN PETROV"));
    }
}