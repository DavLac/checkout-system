package io.davlac.checkoutsystem.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;

import static com.fasterxml.jackson.databind.cfg.ConstructorDetector.USE_PROPERTIES_BASED;

@Component
public class JsonUtils {

    @Autowired
    private ObjectMapper objectMapper;

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = JsonMapper.builder()
                    .addModule(new ParameterNamesModule())
                    .constructorDetector(USE_PROPERTIES_BASED)
                    .build();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object deserializeResult(ResultActions resultActions, Class clazz)
            throws JsonProcessingException, UnsupportedEncodingException {
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, clazz);
    }
}
