package com.simplesystem.todo.integrationtest;

import com.simplesystem.todo.dto.ChangeDescriptionRequestDto;
import com.simplesystem.todo.dto.ChangeStatusRequestDto;
import com.simplesystem.todo.dto.CreateItemRequestDto;
import com.simplesystem.todo.dto.ItemDto;
import com.simplesystem.todo.entity.ItemStatus;
import com.simplesystem.todo.exception.GenericErrorModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemApisTest {

    @Autowired
    private TestRestTemplate template;

    private static final String BASE_URL = "/api/v1";
    private static final String STATUS_UPDATE_ENDPOINT_URL = "/items/{itemId}/status";
    private static final String DESCRIPTION_UPDATE_ENDPOINT_URL = "/items/{itemId}/description";
    private static final String CREATE_ITEM_ENDPOINT_URL = "/items";

    @Test
    public void updateDescription_ShouldRunSuccessfully() throws UnsupportedEncodingException {
        // Prepare - create an item
        ItemDto newItem = createItem();

        // Extract the created item's ID
        long itemId = newItem.getId();

        // Act - update the description of the created item
        ResponseEntity<ItemDto> response = template.exchange(BASE_URL + DESCRIPTION_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeDescriptionRequestDto("newDesc")), ItemDto.class, getEncode(itemId));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("newDesc", Objects.requireNonNull(response.getBody()).getDescription());
    }

    @Test
    public void updateDescription_NotFoundResponse() throws UnsupportedEncodingException {
        // Prepare
        long itemId = 12;
        List<String> expectedResponse = List.of(String.format("Item with id %s not found", itemId));

        // Act - update the description of the created item
        ResponseEntity<GenericErrorModel> response = template.exchange(BASE_URL + DESCRIPTION_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeDescriptionRequestDto("newDesc")), GenericErrorModel.class, getEncode(itemId));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        List<String> errors = Objects.requireNonNull(response.getBody())
                .errors().stream()
                .flatMap(e -> e.body().stream())
                .toList();
        assertEquals(expectedResponse, errors);
    }

    @Test
    public void updateStatus_ShouldRunSuccessfully() throws UnsupportedEncodingException {
        // Prepare - create an item
        ItemDto newItem = createItem();

        // Extract the created item's ID
        long itemId = newItem.getId();

        // Act - update the description of the created item

        ResponseEntity<ItemDto> response = template.exchange(BASE_URL + STATUS_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeStatusRequestDto(ItemStatus.DONE)), ItemDto.class, getEncode(itemId));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ItemDto item = Objects.requireNonNull(response.getBody());
        assertEquals(ItemStatus.DONE, item.getStatus());
        assertNotNull(item.getMarkedDoneAt());
    }

    @Test
    public void updateStatus_ShouldNullifyMarkedDoneAt_WhenStatusUpdateFromDoneToNotDone() throws UnsupportedEncodingException {
        // Prepare - create an item
        ItemDto newItem = createItem();
        // Extract the created item's ID
        long itemId = newItem.getId();
        ResponseEntity<ItemDto> response = template.exchange(BASE_URL + STATUS_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeStatusRequestDto(ItemStatus.DONE)), ItemDto.class, getEncode(itemId));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ItemDto item = Objects.requireNonNull(response.getBody());
        assertEquals(ItemStatus.DONE, item.getStatus());
        assertNotNull(item.getMarkedDoneAt());

        // Act - update the description of the created item
        ResponseEntity<ItemDto> response2 = template.exchange(BASE_URL + STATUS_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeStatusRequestDto(ItemStatus.NOT_DONE)), ItemDto.class, getEncode(itemId));

        // Assert
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        ItemDto sameModifiedItem = Objects.requireNonNull(response2.getBody());
        assertEquals(ItemStatus.NOT_DONE, sameModifiedItem.getStatus());
        assertNull(sameModifiedItem.getMarkedDoneAt());
    }

    private ItemDto createItem() {
        ResponseEntity<ItemDto> createResponse = template.postForEntity(BASE_URL + CREATE_ITEM_ENDPOINT_URL, new CreateItemRequestDto("desc", Instant.now().plus(1, ChronoUnit.HOURS)), ItemDto.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        ItemDto newItem = createResponse.getBody();
        assertNotNull(newItem);
        assertEquals(newItem.getStatus(), ItemStatus.NOT_DONE);
        assertNull(newItem.getMarkedDoneAt());
        return newItem;
    }

    private String getEncode(long itemId) throws UnsupportedEncodingException {
        return URLEncoder.encode(String.valueOf(itemId), StandardCharsets.UTF_8.toString());
    }
}
