package vn.clmart.manager_service.model.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.*;

@Converter
public class ListHashMapConverterString implements AttributeConverter<List<Map<String, String>>, String> {
    private static final Logger log = LogManager.getLogger(ListHashMapConverter.class);

    public ListHashMapConverterString() {
    }

    @Override
    public String convertToDatabaseColumn(List<Map<String, String>> content) {
        if (content != null && !content.isEmpty()) {
            String result = null;

            try {
                result = (new ObjectMapper()).writeValueAsString(content);
            } catch (JsonProcessingException var4) {
                log.error("JSON writing error", var4);
            }

            return result;
        } else {
            return null;
        }
    }

    public List<Map<String, String>> convertToEntityAttribute(String content) {
        List<Map<String, String>> result = null;
        if (StringUtils.isEmpty(content)) {
            return new ArrayList();
        } else {
            try {
                result = (List)(new ObjectMapper()).readValue(content, new TypeReference<List<Map<String, String>>>() {
                });
            } catch (IOException var4) {
                log.error("JSON reading error", var4);
            }

            return result;
        }
    }
}
