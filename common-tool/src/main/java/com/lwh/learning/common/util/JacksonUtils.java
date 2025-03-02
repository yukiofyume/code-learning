package com.lwh.learning.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author lwh
 * @date 2025-03-01 14:19:46
 * @describe -
 */
public class JacksonUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(JacksonUtils.class);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static <T> T toPojo(Object entity, Class<T> valueType) {
        String json = toJson(entity);
        return toPojo(json, valueType);
    }

    public static <T> T toPojo(InputStream inputStream, Class<T> valueType) {
        try {
            return objectMapper.readValue(inputStream, valueType);
        } catch (IOException e) {
            throw new RuntimeException("deserialization fail", e);
        }
    }

    public static <T> T toPojo(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("deserialization fail", e);
        }
    }

    public static <T> List<T> listNodeToListPOJO(JsonNode listNode, Class<T> valueType) {
        return objectMapper.convertValue(listNode, objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
    }

    public static <T> List<T> toList(InputStream inputStream, Class<T> valueType) {
        try {
            return objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
        } catch (IOException e) {
            throw new RuntimeException("deSerialization fail", e);
        }
    }

    public static <T> List<T> toList(String json, Class<T> baseValueType, Class<T> trueValueType) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(baseValueType,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, trueValueType));
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException("deSerialization fail", e);
        }
    }

    /**
     * entity => json
     *
     * @param entity entity
     * @return String
     */
    public static String toJson(Object entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("serialization fail", e);
        }
    }

    public static <T> T toPojo(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("deSerialization fail", e);
        }
    }

    public static <T> T toPojo(JsonNode jsonNode, Class<T> valueType) {
        try {
            return objectMapper.treeToValue(jsonNode, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("node to object fail", e);
        }
    }

    public static <T> List<T> toList(JsonNode jsonNode, Class<T> clazz) {
        try {
            return objectMapper.readerForListOf(clazz).readValue(jsonNode);
        } catch (IOException e) {
            throw new RuntimeException("deSerialization fail", e);
        }
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("deSerialization fail", e);
        }
    }

    public static <T, R> List<T> toList(R object, Class<T> clazz) {
        String s = toJson(object);
        return toList(s, clazz);
    }

    public static <T> T toPojo(byte[] bytes, Class<T> valueType) {
        try {
            return objectMapper.readValue(bytes, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode toPojo(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode readTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectNode objectNode() {
        return objectMapper.createObjectNode();
    }
}

