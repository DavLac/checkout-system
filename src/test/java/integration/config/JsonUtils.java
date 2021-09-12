package integration.config;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface JsonUtils {
    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
