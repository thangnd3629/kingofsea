package com.supergroup.core.converter;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.stereotype.Service;

@Service
@Converter
public class LongBlobConverter implements AttributeConverter<List<Long>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<Long> longList) {
        return longList != null ? longList.stream().map(String::valueOf).collect(Collectors.joining(SPLIT_CHAR)) : "";
    }

    @Override
    public List<Long> convertToEntityAttribute(String string) {
        if (Objects.isNull(string) || string.isEmpty()) {
            return emptyList();
        }
        String[] numberArray = string.split(SPLIT_CHAR);
        return Arrays.stream(numberArray).map(Long::parseLong).collect(Collectors.toList());
    }
}
