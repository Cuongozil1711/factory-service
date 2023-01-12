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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Converter
public class ListHashMapConverter implements AttributeConverter<List<Map<String, Integer>>, String>{
    private static final Logger log = LogManager.getLogger(ListHashMapConverter.class);

    public ListHashMapConverter() {
    }

    @Override
    public String convertToDatabaseColumn(List<Map<String, Integer>> content) {
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

    public List<Map<String, Integer>> convertToEntityAttribute(String content) {
        List<Map<String, Integer>> result = null;
        if (StringUtils.isEmpty(content)) {
            return new ArrayList();
        } else {
            try {
                result = (List)(new ObjectMapper()).readValue(content, new TypeReference<List<Map<String, Integer>>>() {
                });
            } catch (IOException var4) {
                log.error("JSON reading error", var4);
            }

            return result;
        }
    }
}
