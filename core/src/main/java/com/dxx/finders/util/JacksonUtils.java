package com.dxx.finders.util;

import com.dxx.finders.exception.FindersRuntimeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Json utils.
 *
 * @author dxx
 */
public class JacksonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Convert object to json string.
     */
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new FindersRuntimeException(e);
        }
    }

    /**
     * Convert json string to Object.
     */
    public static <T> T toObject(String json, Class<T> cls) {
        try {
            return mapper.readValue(json, cls);
        } catch (IOException e) {
            throw new FindersRuntimeException(e);
        }
    }

    /**
     * Convert bytes to Object.
     */
    public static <T> T toObject(byte[] bytes, Class<T> cls) {
        return toObject(new String(bytes, StandardCharsets.UTF_8), cls);
    }

    /**
     * Convert InputStream to Object.
     */
    public static <T> T toObject(InputStream is, Class<T> cls) {
        try {
            return mapper.readValue(is, cls);
        } catch (IOException e) {
            throw new FindersRuntimeException(e);
        }
    }

    /**
     * Convert json string to JsonNode.
     */
    public static JsonNode toJsonNode(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            throw new FindersRuntimeException(e);
        }
    }
}
