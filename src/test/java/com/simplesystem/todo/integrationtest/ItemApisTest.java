package com.simplesystem.todo.integrationtest;

import com.simplesystem.todo.dto.ChangeDescriptionRequestDto;
import com.simplesystem.todo.dto.ChangeStatusRequestDto;
import com.simplesystem.todo.dto.CreateItemRequestDto;
import com.simplesystem.todo.dto.ItemDto;
import com.simplesystem.todo.entity.ItemStatus;
import com.simplesystem.todo.exception.GenericErrorModel;
import com.simplesystem.todo.repository.ItemRepository;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemApisTest {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private ItemRepository itemRepository;

    private static final String BASE_URL = "/api/v1";
    private static final String STATUS_UPDATE_ENDPOINT_URL = "/items/{itemId}/status";
    private static final String DESCRIPTION_UPDATE_ENDPOINT_URL = "/items/{itemId}/description";
    private static final String CREATE_ITEM_ENDPOINT_URL = "/items";
    private static final String GET_ITEMS_ENDPOINT_URL = "/items";
    private static final String GET_ITEM_ENDPOINT_URL = "/items/{itemId}";

    @AfterEach
    public void deleteAllData(){
        itemRepository.deleteAll();
    }

    @Test
    public void updateDescription_ShouldRunSuccessfully() throws UnsupportedEncodingException {
        // Prepare - create an item
        ItemDto newItem = createItem("desc", Instant.now().plus(1, ChronoUnit.HOURS));

        // Extract the created item's ID
        long itemId = newItem.getId();

        // Act
        ResponseEntity<ItemDto> response = updateDescription(itemId, ItemDto.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("newDesc", Objects.requireNonNull(response.getBody()).getDescription());
    }

    @Test
    public void updateDescription_NotFoundResponse() throws UnsupportedEncodingException {
        // Prepare
        long itemId = 12;
        List<String> expectedResponse = List.of(String.format("Item with id %s not found", itemId));

        // Act
        ResponseEntity<GenericErrorModel> response = updateDescription(itemId, GenericErrorModel.class);

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
        ItemDto newItem = createItem("desc", Instant.now().plus(1, ChronoUnit.HOURS));

        // Extract the created item's ID
        long itemId = newItem.getId();

        // Act
        ResponseEntity<ItemDto> response = updateStatus(ItemStatus.DONE, String.valueOf(itemId));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ItemDto item = Objects.requireNonNull(response.getBody());
        assertEquals(ItemStatus.DONE, item.getStatus());
        assertNotNull(item.getMarkedDoneAt());
    }

    @Test
    public void updateStatus_ShouldNullifyMarkedDoneAt_WhenStatusUpdateFromDoneToNotDone() throws UnsupportedEncodingException {
        // Prepare - create an item
        ItemDto newItem = createItem("desc", Instant.now().plus(1, ChronoUnit.HOURS));
        // Extract the created item's ID
        long itemId = newItem.getId();
        ResponseEntity<ItemDto> response = updateStatus(ItemStatus.DONE, String.valueOf(itemId));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ItemDto item = Objects.requireNonNull(response.getBody());
        assertEquals(ItemStatus.DONE, item.getStatus());
        assertNotNull(item.getMarkedDoneAt());

        // Act
        ResponseEntity<ItemDto> response2 = updateStatus(ItemStatus.NOT_DONE, String.valueOf(itemId));

        // Assert
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        ItemDto sameModifiedItem = Objects.requireNonNull(response2.getBody());
        assertEquals(ItemStatus.NOT_DONE, sameModifiedItem.getStatus());
        assertNull(sameModifiedItem.getMarkedDoneAt());
    }

    @Test
    public void getItems_ShouldRunSuccessfully(){
        // Prepare - create an item
        ItemDto newItem = createItem("desc", Instant.now().plus(1, ChronoUnit.HOURS));
        ItemDto newItem2 = createItem("desc2", Instant.now().plus(1, ChronoUnit.HOURS));
        ItemDto newItem3 = createItem("desc3", Instant.now().plus(1, ChronoUnit.HOURS));

        // Act
        String url = BASE_URL + GET_ITEMS_ENDPOINT_URL;
        ResponseEntity<List<ItemDto>> response = getItems(url);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ItemDto> items = Objects.requireNonNull(response.getBody());
        assertEquals(3, items.size());
        assertEquals(List.of(newItem, newItem2, newItem3), items);
    }

    @Test
    public void getItems_ShouldReturnSpecificItems_WhenFilterCriteriaIsProvided() throws UnsupportedEncodingException {
        // Prepare - create an item
        ItemDto newItem = createItem("desc", Instant.now().plus(1, ChronoUnit.HOURS));
        ItemDto newItem2 = createItem("desc2", Instant.now().plus(1, ChronoUnit.HOURS));
        ItemDto newItem3 = createItem("desc3", Instant.now().plus(1, ChronoUnit.HOURS));
        ResponseEntity<ItemDto> response = updateStatus(ItemStatus.DONE, String.valueOf(newItem.getId()));
        ResponseEntity<ItemDto> response2 = updateStatus(ItemStatus.DONE, String.valueOf(newItem2.getId()));

        // Act
        String url = String.format(BASE_URL + GET_ITEMS_ENDPOINT_URL + "?statusToExclude=%s", ItemStatus.NOT_DONE);
        ResponseEntity<List<ItemDto>> getAllResp = getItems(url);

        // Assert
        assertEquals(HttpStatus.OK, getAllResp.getStatusCode());
        List<ItemDto> items = Objects.requireNonNull(getAllResp.getBody());
        assertEquals(2, items.size());
        assertEquals(List.of(Objects.requireNonNull(response.getBody()), Objects.requireNonNull(response2.getBody())), items);
    }

    @Test
    public void getItem_ShouldRunSuccessfully() throws UnsupportedEncodingException {
        // Prepare - create an item
        ItemDto newItem = createItem("desc", Instant.now().plus(1, ChronoUnit.HOURS));
        Long itemId = newItem.getId();

        // Act
        ResponseEntity<ItemDto> response = getItem(itemId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ItemDto item = Objects.requireNonNull(response.getBody());
        assertEquals(newItem, item);
    }

    @Test
    public void scheduler_shouldUpdateItemStatusSuccessfully() {
        ItemDto newItem = createItem("desc", Instant.now().plus(10, ChronoUnit.SECONDS));
        Long itemId = newItem.getId();
        await()
                .atMost(new Duration(15, SECONDS))
                .untilAsserted(() -> {
                    ResponseEntity<ItemDto> response = getItem(itemId);
                    assertEquals(ItemStatus.PAST_DUE, Objects.requireNonNull(response.getBody()).getStatus());
                });
    }

    private ResponseEntity<ItemDto> getItem(Long itemId) throws UnsupportedEncodingException {
        return template.exchange(BASE_URL + GET_ITEM_ENDPOINT_URL, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        }, urlEncode(String.valueOf(itemId)));
    }

    private ItemDto createItem(String desc, Instant dueAt) {
        ResponseEntity<ItemDto> createResponse = template.postForEntity(BASE_URL + CREATE_ITEM_ENDPOINT_URL, new CreateItemRequestDto(desc, dueAt), ItemDto.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        ItemDto newItem = createResponse.getBody();
        assertNotNull(newItem);
        assertEquals(newItem.getStatus(), ItemStatus.NOT_DONE);
        assertNull(newItem.getMarkedDoneAt());
        return newItem;
    }

    private String urlEncode(String itemId) throws UnsupportedEncodingException {
        return URLEncoder.encode(itemId, StandardCharsets.UTF_8.toString());
    }

    private ResponseEntity<ItemDto> updateStatus(ItemStatus status, String itemId) throws UnsupportedEncodingException {
        return template.exchange(BASE_URL + STATUS_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeStatusRequestDto(status)), ItemDto.class, urlEncode(itemId));
    }

    private <T> ResponseEntity<T> updateDescription(long itemId, Class<T> responseType) throws UnsupportedEncodingException {
        return template.exchange(BASE_URL + DESCRIPTION_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeDescriptionRequestDto("newDesc")), responseType, urlEncode(String.valueOf(itemId)));
    }

    private ResponseEntity<List<ItemDto>> getItems(String url) {
        return template.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
    }
}
