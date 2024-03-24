package com.example.todo.converter;

import com.example.todo.entity.ItemStatus;
import org.springframework.core.convert.converter.Converter;

import static com.example.todo.entity.ItemStatus.*;

public class StringToEnumValue implements Converter<String, ItemStatus> {

    @Override
    public ItemStatus convert(String source) {
        return switch (source){
            case "Not Done" -> NOT_DONE;
            case "Done" -> DONE;
            case "Past Due" -> PAST_DUE;
            default -> throw new IllegalArgumentException(String.format("Unsupported status value of %s is provided", source));
        };
    }
}
