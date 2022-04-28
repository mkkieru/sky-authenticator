//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.UTILS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Converter {
    public Converter() {
    }

    public static String toJson(Object obj) {
        return serialize(obj, "application/json");
    }

    public static String toXml(Object obj) {
        return serialize(obj, "application/xml");
    }

    public static String serialize(Object obj, String contentType) {
        ObjectMapper objectMapper = getObjectMapper(contentType);

        try {
            String objStr = objectMapper.writeValueAsString(obj);
            return objStr;
        } catch (JsonProcessingException var4) {
            var4.printStackTrace();
            return "";
        }
    }

    public Object[] fromJson(String data, Class<?> clazz) {
        ObjectMapper objectMapper = getObjectMapper("application/json");

        try {
            Object obj = objectMapper.readValue(data, clazz);
            return new Object[]{1, obj};
        } catch (Exception var5) {
            var5.printStackTrace();
            return new Object[]{-1, "MARSHALL ERROR", var5.getMessage()};
        }
    }

    public static Object getObject(String data, Class clazz, String contentType) {
        ObjectMapper objectMapper = getObjectMapper(contentType);

        try {
            Object obj = objectMapper.readValue(data, clazz);
            return obj;
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static Object toHashMap(String objStr, TypeReference T) {
        ObjectMapper objectMapper = new ObjectMapper();
        Object map = new HashMap();

        try {
            map = (Map)objectMapper.readValue(objStr, T);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return map;
    }

    public static ObjectMapper getObjectMapper(String contentType) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (contentType.equals("application/xml")) {
            JacksonXmlModule xmlModule = new JacksonXmlModule();
            xmlModule.setDefaultUseWrapper(false);
            XmlMapper mapper = new XmlMapper(xmlModule);
            mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
            mapper.setDateFormat(df);
            mapper.getDeserializationConfig().with(df);
            return mapper;
        } else {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
            mapper.setDateFormat(df);
            mapper.getDeserializationConfig().with(df);
            return mapper;
        }
    }
}
