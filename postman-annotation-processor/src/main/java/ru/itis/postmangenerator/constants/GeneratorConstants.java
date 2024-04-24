package ru.itis.postmangenerator.constants;

import java.util.List;

public class GeneratorConstants {

    public static final String POSTMAN_SCHEMA = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json";

    public static final String HEADER_TYPE = "text";

    public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    public static final String CONTENT_TYPE_HEADER_VALUE = "application/json";

    public static final String BODY_MODE = "raw";
    public static final String RAW_LANGUAGE = "json";

    public static final String DEFAULT_PROTOCOL = "http";
    public static final String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_PORT = "8080";

    public static final List<String> EMAIL_FIELDS = List.of("email", "mail");
    public static final List<String> FIRST_NAME_FIELDS = List.of("firstname","firstName", "name");
    public static final List<String> SUR_NAME_FIELDS = List.of("middlename", "middleName", "surname", "surName");
    public static final List<String> LAST_NAME_FIELDS = List.of("lastname", "lastName", "patronymic");
    public static final List<String> FULL_NAME_FIELDS = List.of("name", "fullname", "fullName");

    public static final String UUID_TYPE = "UUID";
    public static final String STRING_TYPE = "String";
    public static final String LOCAL_DATE_TYPE = "LocalDate";
    public static final String LOCAL_DATE_TIME_TYPE = "LocalDateTime";
    public static final List<String> INTEGERS_TYPES = List.of(
            "int", "Integer", "INT",
            "long", "Long", "LONG",
            "short", "Short", "SHORT",
            "byte", "Byte", "BYTE");
    public static final List<String> FRACTIONAL_TYPES = List.of(
            "float", "Float", "FLOAT",
            "double", "Double", "DOUBLE");
    public static final List<String> BOOLEAN_TYPES = List.of("boolean", "Boolean", "BOOLEAN");

    public static final int MIN_DEFAULT_VALUE = 0;
    public static final int MAX_DEFAULT_VALUE = 50;
    public static final int DEFAULT_DECIMALS_COUNT = 1;

    public static final int DEFAULT_WORDS_COUNT = 10000;

}
