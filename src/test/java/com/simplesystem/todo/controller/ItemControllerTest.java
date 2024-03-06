package com.simplesystem.todo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplesystem.todo.dto.CreateItemRequestDto;
import com.simplesystem.todo.dto.ItemDto;
import com.simplesystem.todo.entity.Item;
import com.simplesystem.todo.entity.ItemStatus;
import com.simplesystem.todo.exception.GenericErrorModel;
import com.simplesystem.todo.repository.ItemRepository;
import com.simplesystem.todo.service.ItemService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.SQLDataException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void addItem_ShouldAddItemSuccessfully() throws Exception {
        // Prepare
        Instant dueAt = Instant.now().plus(1, ChronoUnit.MINUTES);
        Item item = new Item(1L, "open bank account", ItemStatus.NOT_DONE, Instant.now(), dueAt, null);
        ItemDto itemDto = item.toItemDto();
        when(itemService.save(any(Item.class))).thenReturn(item);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/items")
                        .content(asJsonString(new CreateItemRequestDto("open bank account", dueAt)))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();

        // Assert
        assertEquals(201, mvcResult.getResponse().getStatus());
        ItemDto result = fromJsonString(mvcResult.getResponse().getContentAsString(), ItemDto.class);
        assertEquals(itemDto, result);
    }

    @Test
    void addItem_ShouldThrowErrorWhenDescriptionIsKeptNullOrBlank() throws Exception {
        // Prepare
        Instant dueAt = Instant.now().plus(1, ChronoUnit.MINUTES);
        GenericErrorModel errorModel = new GenericErrorModel(List.of(new GenericErrorModel.GenericErrorModelBody(List.of("Description cannot be blank"))));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/items")
                        .content(asJsonString(new CreateItemRequestDto("", dueAt)))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();

        // Assert
        assertEquals(400, mvcResult.getResponse().getStatus());
        assertEquals(errorModel, fromJsonString(mvcResult.getResponse().getContentAsString(), GenericErrorModel.class));
    }

    @Test
    void addItem_ShouldThrow500ErrorWhenUnknownExceptionIsThrown() throws Exception {
        // Prepare
        Instant dueAt = Instant.now().plus(1, ChronoUnit.MINUTES);
        when(itemService.save(any(Item.class))).thenThrow(new RuntimeException("Some problem with data save"));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/items")
                        .content(asJsonString(new CreateItemRequestDto("abc", dueAt)))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();

        // Assert
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getContentAsString());
    }

    private <T> T fromJsonString(String content, Class<T> tClass){
        try {
            return objectMapper.readValue(content, tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
