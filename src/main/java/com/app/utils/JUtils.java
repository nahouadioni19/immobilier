package com.app.utils;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JUtils {

    private JUtils() {
    }

    /**
     * String
     */

    public static String firstNonBlank(String... args) {
        return StringUtils.firstNonBlank(args);
    }

    public static String allNonBlank(String separator, boolean isDeep, String... args) {
        if (isDeep)
            return allNonBlankDeepCase(separator, args);
        else
            return new ArrayList<>(Arrays.asList(args)).stream().filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(separator));
    }

    private static String allNonBlankDeepCase(String separator, String... args) {
        return new ArrayList<>(Arrays.asList(args))
                .stream().filter(StringUtils::isNotBlank).map(x -> Arrays.asList(x.split(separator)).stream()
                        .map(String::trim).collect(Collectors.joining(separator)))
                .collect(Collectors.joining(separator));
    }

    public static boolean notNullString(String stringValue) {
        return StringUtils.isNotBlank(stringValue) && !stringValue.equalsIgnoreCase("undefined");
    }

    /**
     * Date
     */

    public static String reverseStringDate(String dateStr) {
        String[] arr = dateStr.split("/");
        return arr[2] + "/" + arr[1] + "/" + arr[0];
    }

    public static String reverseStringDateNum(String dateStr) {
        if (StringUtils.isBlank(dateStr))
            return null;
        String[] arr = dateStr.split("/");
        return arr[2] + arr[1] + arr[0];
    }

    public static String dateToStringFormat(LocalDate date, String format) {
        return (date != null) ? date.format(DateTimeFormatter.ofPattern(format)) : null;
    }

    public static String dateToString(LocalDate date) {
        return dateToStringFormat(date, Constants.FORMAT_DATE_DEFAULT);
    }

    public static String dateTimeToString(LocalDate date) {
        return dateToStringFormat(date, Constants.FORMAT_DATE_TIME_DEFAULT);
    }

    public static String dateTimeToStringFormat(LocalDateTime date, String format) {
        return (date != null) ? date.format(DateTimeFormatter.ofPattern(format)) : null;
    }

    public static String dateTimeToString(LocalDateTime date) {
        return dateTimeToStringFormat(date, Constants.FORMAT_DATE_TIME_DEFAULT);
    }

    public static String dateShortToString(LocalDateTime date) {
        return dateTimeToStringFormat(date, Constants.FORMAT_DATE_DEFAULT);
    }

    public static LocalDate stringToDateFormat(String date, String format) {
        return (StringUtils.isNotBlank(date)) ? LocalDate.parse(date, DateTimeFormatter.ofPattern(format)) : null;
    }

    public static LocalDateTime stringToDateTimeFormat(String date, String format) {
        return (StringUtils.isNotBlank(date)) ? LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format)) : null;
    }

    public static LocalDate stringToDate(String date) {
        return stringToDateFormat(date, Constants.FORMAT_DATE_DEFAULT);
    }

    public static LocalDateTime stringToDateTime(String date) {
        return stringToDateTimeFormat(date, Constants.FORMAT_DATE_TIME_DEFAULT);
    }

    /***
     * objectmapper
     */
    public static <K, V> Map<K, V> jsonToMap(String json) {
        Map<K, V> item = new LinkedHashMap<>();
        try {
            item = getObjectMapper().readValue(json, new TypeReference<Map<K, V>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    public static <K, V> Map<K, V> jsonToMap(String json, K key, V value) {
        Map<K, V> item = new HashMap<>();
        try {
            item = getObjectMapper().readValue(json, new TypeReference<Map<K, V>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    public static <T> T fromJSON(String json, Class<T> klass) {
        T t = null;
        try {
            t = getObjectMapper().readValue(json, klass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return t;
    }

    public static <T> T fromJSONTo(String json, TypeReference<T> klass) {
        T t = null;
        try {
            t = getObjectMapper().readValue(json, klass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return t;
    }

    public static <T> String toJSON(T t) {
        String json = t instanceof List ? "[]" : "{}";
        try {
            json = getObjectMapper().writeValueAsString(t);
        } catch (Exception e) {
         //   log.info(e.getMessage(), e);
        }

        return json;
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    /**
     * Cete methode recupere l'adresse ip de l'acteur connecté
     *
     * @param request
     * @return
     */
    public static String getIpAdresseInfo(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null)
            ipAddress = request.getRemoteAddr();
        return ipAddress;
    }

    /***
     * Map
     * 
     */
    public static <K, E> Map<K, E> doPut(Map<K, E> map, K key, E value) {
        if (map == null)
            map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static <K, V> V getMapValue(Map<K, V> params, String key) {
        if (!params.isEmpty() && params.containsKey(key))
            return params.get(key);
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <E> E getModelMapValue(Model model, String key) {
        return (E) getMapValue(model.asMap(), key);
    }

    public static <E> E getModelAttribute(Model model, String key) {
        return getModelMapValue(model, key);
    }

    public static boolean setModelAttribute(Model model, String attributeName, Object value, Object nullValue) {
        boolean isCorrect = value != null;
        model.addAttribute(attributeName, isCorrect ? value : nullValue);
        return isCorrect;
    }

    public static boolean setModelAttribute(Model model, String attributeName, Object value) {
        return setModelAttribute(model, attributeName, value, "");
    }

    /**
     * Cette methode retrouve les elements constituees dans le Spring requestMapping
     * (ex: SIB_module,
     * SIB_directory...etc)
     *
     * @param request
     * @param key
     * @return String
     */
    public static String pathParams(HttpServletRequest request, String key) {
        Map<?, ?> pathVariables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return (String) getMapValue(pathVariables, key);
    }

    /***
     * 
     */

    public static String HashStr(String str) throws NoSuchAlgorithmException {
        MessageDigest md;

        md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    public static <E> String printOut(E e) {
        return ToStringBuilder.reflectionToString(e, ToStringStyle.MULTI_LINE_STYLE);
    }

    @SuppressWarnings("all")
    public static <E> JSONArray listToJSONArray(List<E> list, String[] keyNames, String[] valueMethods, String sep)
            throws JSONException {
        JSONArray array = new JSONArray();
        for (E elt : list) {
            JSONObject object = entityToJSONObject(elt, keyNames, valueMethods, sep);
            array.put(object);
        }
        return array;
    }

    @SuppressWarnings("all")
    public static <E> JSONObject entityToJSONObject(E entity, String[] keyNames, String[] valueMethods, String sep)
            throws JSONException {
        JSONObject object = new JSONObject();
        sep = nullToEmpty(sep);
        int length = Math.min(keyNames.length, valueMethods.length);
        for (int i = 0; i < length; i++) {
            String value = "";
            String[] methods = valueMethods[i].split(",");
            for (int j = 0; j < methods.length; j++)
                value += getFieldEntityValue(entity, methods[j]) + sep;
            object.put(nullToEmpty(keyNames[i]), value.substring(0, value.length() - sep.length()));
        }
        return object;
    }

    public static <E> JSONObject entityToJSONObject(E entity, String[] keyNames, String[] valueMethods)
            throws JSONException {
        String sep = ";";
        return entityToJSONObject(entity, keyNames, valueMethods, sep);
    }

    public static String nullToEmpty(String str) {
        if (!notNullString(str))
            return "";
        else
            return str;
    }

    public static <E> Object getFieldEntityValue(E object, String getMethod) {
        Class<?>[] noparams = {};
        try {
            if (object != null) {
                String methods[] = getMethod.split("\\.");
                if (1 == methods.length)
                    return getFieldValueFromEntity(object, methods[0], noparams);
                else {
                    Object result = getFieldValueFromEntity(object, methods[0], noparams);
                    for (int i = 1; i < methods.length; i++)
                        result = getFieldValueFromEntity(result, methods[i], noparams);
                    return result;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <E> Object getFieldValueFromEntity(E object, String fieldMethod, Class<?>[] params, Object... value) {
        String fieldGetMethod = getMethodName(fieldMethod); // Pour mettre le préfix "get" si nécessaire
        try {
            if (object != null) {
                // Vérification de la méthode telle que précisé (dans la classe de l'objet)
                Method method = object.getClass().getMethod(fieldGetMethod, params);
                return method.invoke(object, value);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String getMethodName(String fieldOrMethod) {
        return getMethodName(fieldOrMethod, "get");
    }

    private static String getMethodName(String fieldOrMethod, String methodPrefix) {
        String meth = fieldOrMethod;

        if (!meth.startsWith(methodPrefix)) {
            meth = methodPrefix + StringUtils.capitalize(fieldOrMethod);
        }

        return meth;
    }
}
