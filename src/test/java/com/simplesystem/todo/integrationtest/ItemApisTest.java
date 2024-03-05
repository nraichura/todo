package com.simplesystem.todo.integrationtest;

import com.simplesystem.todo.dto.ChangeDescriptionRequestDto;
import com.simplesystem.todo.dto.CreateItemRequestDto;
import com.simplesystem.todo.dto.ItemDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemApisTest {

    @Autowired
    private TestRestTemplate template;

    @Test
    public void updateDescription_ShouldRunSuccessfully(){
        // Prepare - create an item
        ResponseEntity<ItemDto> createResponse = template.postForEntity("/api/v1/items", new CreateItemRequestDto("oldDesc", Instant.now().plus(1, ChronoUnit.HOURS)), ItemDto.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        // Extract the created item's ID
        long itemId = createResponse.getBody().getId();

        // Act - update the description of the created item
        ResponseEntity<ItemDto> response = template.exchange("/api/v1/items/{itemId}", HttpMethod.PUT, new HttpEntity<>(new ChangeDescriptionRequestDto("newDesc")), ItemDto.class, itemId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("newDesc", Objects.requireNonNull(response.getBody()).getDescription());
    }

}
