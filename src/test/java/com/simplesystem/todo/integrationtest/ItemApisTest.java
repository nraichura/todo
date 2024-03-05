package com.simplesystem.todo.integrationtest;

import com.simplesystem.todo.dto.ChangeDescriptionRequestDto;
import com.simplesystem.todo.dto.ChangeStatusRequestDto;
import com.simplesystem.todo.dto.CreateItemRequestDto;
import com.simplesystem.todo.dto.ItemDto;
import com.simplesystem.todo.entity.ItemStatus;
import com.simplesystem.todo.exception.GenericErrorModel;
import com.simplesystem.todo.repository.ItemRepository;
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
    public void createItem_ShouldGiveInternalServerError_WhenTheItemIsCreatedWithSameDescriptionAgain() {
        // Prepare - create an item
        createItem("desc");

        // Act
        ResponseEntity<GenericErrorModel> response = template.postForEntity(BASE_URL + CREATE_ITEM_ENDPOINT_URL, new CreateItemRequestDto("desc", Instant.now().plus(1, ChronoUnit.HOURS)), GenericErrorModel.class);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void updateDescription_ShouldRunSuccessfully() throws UnsupportedEncodingException {
        // Prepare - create an item
        ItemDto newItem = createItem("desc");

        // Extract the created item's ID
        long itemId = newItem.getId();

        // Act - update the description of the created item
        ResponseEntity<ItemDto> response = template.exchange(BASE_URL + DESCRIPTION_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeDescriptionRequestDto("newDesc")), ItemDto.class, urlEncode(String.valueOf(itemId)));

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
        ResponseEntity<GenericErrorModel> response = template.exchange(BASE_URL + DESCRIPTION_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeDescriptionRequestDto("newDesc")), GenericErrorModel.class, urlEncode(String.valueOf(itemId)));

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
        ItemDto newItem = createItem("desc");

        // Extract the created item's ID
        long itemId = newItem.getId();

        // Act - update the description of the created item

        ResponseEntity<ItemDto> response = template.exchange(BASE_URL + STATUS_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeStatusRequestDto(ItemStatus.DONE)), ItemDto.class, urlEncode(String.valueOf(itemId)));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ItemDto item = Objects.requireNonNull(response.getBody());
        assertEquals(ItemStatus.DONE, item.getStatus());
        assertNotNull(item.getMarkedDoneAt());
    }

    @Test
    public void updateStatus_ShouldNullifyMarkedDoneAt_WhenStatusUpdateFromDoneToNotDone() throws UnsupportedEncodingException {
        // Prepare - create an item
        ItemDto newItem = createItem("desc");
        // Extract the created item's ID
        long itemId = newItem.getId();
        ResponseEntity<ItemDto> response = template.exchange(BASE_URL + STATUS_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeStatusRequestDto(ItemStatus.DONE)), ItemDto.class, urlEncode(String.valueOf(itemId)));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ItemDto item = Objects.requireNonNull(response.getBody());
        assertEquals(ItemStatus.DONE, item.getStatus());
        assertNotNull(item.getMarkedDoneAt());

        // Act - update the description of the created item
        ResponseEntity<ItemDto> response2 = template.exchange(BASE_URL + STATUS_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeStatusRequestDto(ItemStatus.NOT_DONE)), ItemDto.class, urlEncode(String.valueOf(itemId)));

        // Assert
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        ItemDto sameModifiedItem = Objects.requireNonNull(response2.getBody());
        assertEquals(ItemStatus.NOT_DONE, sameModifiedItem.getStatus());
        assertNull(sameModifiedItem.getMarkedDoneAt());
    }

    @Test
    public void getItems_ShouldRunSuccessfully(){
        // Prepare - create an item
        ItemDto newItem = createItem("desc");
        ItemDto newItem2 = createItem("desc2");
        ItemDto newItem3 = createItem("desc3");

        // Act - update the description of the created item
        ParameterizedTypeReference<List<ItemDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<ItemDto>> response = template.exchange(BASE_URL + GET_ITEMS_ENDPOINT_URL, HttpMethod.GET, null, responseType);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ItemDto> items = Objects.requireNonNull(response.getBody());
        assertEquals(3, items.size());
        assertEquals(List.of(newItem, newItem2, newItem3), items);
    }

    @Test
    public void getItems_ShouldReturnSpecificItems_WhenFilterCriteriaIsProvided() throws UnsupportedEncodingException {
        // Prepare - create an item
        ItemDto newItem = createItem("desc");
        ItemDto newItem2 = createItem("desc2");
        ItemDto newItem3 = createItem("desc3");
        ResponseEntity<ItemDto> response = template.exchange(BASE_URL + STATUS_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeStatusRequestDto(ItemStatus.DONE)), ItemDto.class, urlEncode(String.valueOf(newItem.getId())));
        ResponseEntity<ItemDto> response2 = template.exchange(BASE_URL + STATUS_UPDATE_ENDPOINT_URL, HttpMethod.PATCH, new HttpEntity<>(new ChangeStatusRequestDto(ItemStatus.DONE)), ItemDto.class, urlEncode(String.valueOf(newItem2.getId())));

        // Act - update the description of the created item
        ParameterizedTypeReference<List<ItemDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<ItemDto>> getAllResp = template.exchange(String.format(BASE_URL + GET_ITEMS_ENDPOINT_URL + "?statusToExclude=%s", ItemStatus.NOT_DONE), HttpMethod.GET, null, responseType);

        // Assert
        assertEquals(HttpStatus.OK, getAllResp.getStatusCode());
        List<ItemDto> items = Objects.requireNonNull(getAllResp.getBody());
        assertEquals(2, items.size());
        assertEquals(List.of(Objects.requireNonNull(response.getBody()), Objects.requireNonNull(response2.getBody())), items);
    }

    @Test
    public void getItem_ShouldRunSuccessfully() throws UnsupportedEncodingException {
        // Prepare - create an item
        ItemDto newItem = createItem("desc");
        Long itemId = newItem.getId();

        // Act - update the description of the created item
        ParameterizedTypeReference<ItemDto> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<ItemDto> response = template.exchange(BASE_URL + GET_ITEM_ENDPOINT_URL, HttpMethod.GET, null, responseType, urlEncode(String.valueOf(itemId)));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ItemDto item = Objects.requireNonNull(response.getBody());
        assertEquals(newItem, item);
    }

    private ItemDto createItem(String desc) {
        ResponseEntity<ItemDto> createResponse = template.postForEntity(BASE_URL + CREATE_ITEM_ENDPOINT_URL, new CreateItemRequestDto(desc, Instant.now().plus(1, ChronoUnit.HOURS)), ItemDto.class);
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
}
