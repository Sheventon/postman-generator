package ru.itis.postmangenerator.generator;

import net.datafaker.Faker;
import org.javatuples.Pair;
import ru.itis.postmangenerator.analyzer.ProjectAnalyzer;
import ru.itis.postmangenerator.analyzer.impl.SpringProjectAnalyzer;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static ru.itis.postmangenerator.constants.GeneratorConstants.*;

public class PostmanCollectionGenerator {

    ProjectAnalyzer projectAnalyzer = new SpringProjectAnalyzer();

    public List<String> commonUrlParts;

    public StringBuilder generateCollection(Element controller) {
        StringBuilder collection = new StringBuilder("{\n");
        Faker faker = new Faker(Locale.ENGLISH);
        commonUrlParts = projectAnalyzer.extractCommonUrlParts(controller);
        generateInfo(collection, controller.getSimpleName().toString(), faker);
        generateItems(collection, controller, faker);
        collection.append("}");
        return collection;
    }

    private void generateInfo(StringBuilder collection, String className, Faker faker) {
        collection.append("\"info\": {\n")
                .append(String.format("\"_postman_id\": \"%s\",\n", UUID.randomUUID()))
                .append(String.format("\"name\": \"%s\",\n", className))
                .append(String.format("\"schema\": \"%s\",\n", POSTMAN_SCHEMA))
                .append(String.format("\"_exporter_id\": \"%s\"\n", faker.number().positive()))
                .append("},\n");
    }

    private void generateItems(StringBuilder collection, Element controller, Faker faker) {
        collection.append("\"item\": [\n");
        projectAnalyzer.getHttpMethodsFromController(controller)
                .forEach(method -> generateItem(collection, method, faker));
        collection.delete(collection.length() - 2, collection.length() - 1);
        collection.append("]\n");
    }

    private void generateItem(StringBuilder collection, Element method, Faker faker) {
        Optional<? extends AnnotationMirror> optionalHttpAnnotation =
                projectAnalyzer.checkHttpMethodMappingAnnotationIsPresentOnMethod(method);
        List<String> pathParts = new ArrayList<>();
        String httpMethod = projectAnalyzer.getHttpMethodFromAnnotation(optionalHttpAnnotation);
        if (httpMethod != null) {

            collection.append("{\n")
                    .append(String.format("\"name\": \"%s\",\n", method.getSimpleName()))
                    .append("\"request\": {\n")
                    .append(String.format("\"method\": \"%s\",\n", httpMethod))
                    .append("\"header\": [");

            TypeMirror requestBodyFieldType = projectAnalyzer.returnRequestBodyFieldType(method);
            if (requestBodyFieldType != null) {
                collection.append("\n{\n")
                        .append(String.format("\"key\": \"%s\",\n", CONTENT_TYPE_HEADER_NAME))
                        .append(String.format("\"value\": \"%s\",\n", CONTENT_TYPE_HEADER_VALUE))
                        .append(String.format("\"type\": \"%s\"\n", HEADER_TYPE))
                        .append("}\n");
            }
            if (requestBodyFieldType != null) {
                collection.append("],\n")
                        .append("\"body\": {\n")
                        .append(String.format("\"mode\": \"%s\",\n", BODY_MODE))
                        .append(String.format("\"raw\": \"%s\",\n", createRequestBodyContent(requestBodyFieldType, faker)))
                        .append("\"options\": {\n")
                        .append("\"raw\": {\n")
                        .append(String.format("\"language\": \"%s\"\n", RAW_LANGUAGE))
                        .append("}\n")
                        .append("}\n")
                        .append("},\n");
            } else {
                collection.append("],\n");
            }
            collection.append("\"url\": {\n")
                    .append(String.format("\"raw\": \"%s\",\n", createUrl(optionalHttpAnnotation, pathParts)))
                    .append(String.format("\"protocol\": \"%s\",\n", DEFAULT_PROTOCOL))
                    .append("\"host\": [\n")
                    .append(String.format(String.format("\"%s\"\n", DEFAULT_HOST)))
                    .append("],\n")
                    .append("\"path\": [\n");
            fillPathPart(collection, pathParts);
            collection.append("]\n")
                    .append("}\n")
                    .append("},\n")
                    .append("\"response\": []\n")
                    .append("},\n");
        }
    }

    private String createUrl(Optional<? extends AnnotationMirror> optionalHttpAnnotation, List<String> pathParts) {
        StringBuilder url = new StringBuilder();
        StringBuilder prefix = new StringBuilder(String.format("%s://%s:%s", DEFAULT_PROTOCOL, DEFAULT_HOST, DEFAULT_PORT));
        commonUrlParts.forEach(prefix::append);
        optionalHttpAnnotation.ifPresent(annotation ->
                annotation.getElementValues().forEach((key, value) -> {
                    if (key.getSimpleName().toString().equals("value") || key.isDefault()) {
                        StringBuilder cleanValue = new StringBuilder(String.valueOf(value));
                        if (!cleanValue.isEmpty()) {
                            projectAnalyzer.clearAndFillUrlsParts(cleanValue, pathParts);
                            url.append(cleanValue);
                        }
                    }
                })
        );
        return url.insert(0, prefix).toString();
    }

    private void fillPathPart(StringBuilder collection, List<String> pathParts) {
        List<String> allParts = new ArrayList<>(commonUrlParts);
        allParts.addAll(pathParts);
        allParts.forEach(part -> collection.append(String.format(String.format("\"%s\",\n", part))));
        collection.delete(collection.length() - 2, collection.length() - 1);
    }

    private String createRequestBodyContent(TypeMirror requestBodyFieldType, Faker faker) {
        StringBuilder raw = new StringBuilder();
        List<Element> fields = projectAnalyzer.getFieldsFromRequestBody(requestBodyFieldType);
        if (!fields.isEmpty()) {
            raw.append("{\\n");
            fillClearFieldValue(raw, faker, fields, raw.isEmpty());
        }
        raw.append("}");
        return raw.toString();
    }

    private void fillFieldValue(StringBuilder raw, Element field, Faker faker) {
        List<AnnotationMirror> annotations = projectAnalyzer.extractFieldValidationAnnotations(field);
        String fieldName = field.getSimpleName().toString();
        boolean filled = fillByFieldName(fieldName, raw, faker);
        if (!filled) {
            String fieldType;
            List<? extends TypeMirror> typeArguments;
            if (!field.asType().getKind().isPrimitive()) {
                fieldType = ((DeclaredType) field.asType()).asElement().getSimpleName().toString();
                typeArguments = ((DeclaredType) field.asType()).getTypeArguments();
            } else {
                fieldType = field.asType().getKind().name();
                typeArguments = Collections.emptyList();
            }
            filled = fillByFieldType(fieldType, raw, faker, annotations, typeArguments);
        }
        if (!filled) {

            if (((DeclaredType) field.asType()).asElement().getKind() == ElementKind.ENUM) {
                raw.append("\\\"")
                        .append(((DeclaredType) field.asType()).asElement().getEnclosedElements()
                                .stream()
                                .filter(element ->
                                        element.getKind() != ElementKind.METHOD &&
                                                element.getKind() != ElementKind.CONSTRUCTOR)
                                .toList()
                                .get(0))
                        .append("\\\"");
            } else if (!field.asType().getKind().isPrimitive() &&
                    checkFieldIsCollection(((DeclaredType) field.asType()).asElement().getSimpleName().toString())) {
                for (int i = 0; i < 3; i++) {
                    raw.append("{\\n");
                    List<? extends Element> enclosedFields = ((DeclaredType) ((DeclaredType) field.asType()).getTypeArguments().get(0)).asElement().getEnclosedElements()
                            .stream()
                            .filter(element ->
                                    element.getKind().isField() && projectAnalyzer.elementFromJavaLangPackage(element)
                            )
                            .toList();
                    fillClearFieldValue(raw, faker, enclosedFields, enclosedFields.isEmpty());
                    if (i != 2) {
                        raw.append("},\\n");
                    } else {
                        raw.append("}\\n");
                    }
                }
                raw.append("]");

            } else {
                raw.append("{\\n");
                List<? extends Element> enclosedFields = ((DeclaredType) field.asType()).asElement().getEnclosedElements()
                        .stream()
                        .filter(element ->
                                element.getKind().isField() &&
                                        (projectAnalyzer.elementFromJavaLangPackage(element) ||
                                                projectAnalyzer.innerFieldHasSamePackageThatParentField(element, field)
                                        )
                        )
                        .toList();
                fillClearFieldValue(raw, faker, enclosedFields, enclosedFields.isEmpty());
                raw.append("}");
            }
        }
    }

    private void fillClearFieldValue(StringBuilder raw, Faker faker, List<? extends Element> enclosedFields, boolean empty) {
        enclosedFields
                .forEach(element -> {
                    raw.append(String.format("\\\"%s\\\": ", element.getSimpleName()));
                    fillFieldValue(raw, element, faker);
                    raw.append(",\\n");
                });
        if (!empty) {
            raw.delete(raw.length() - 3, raw.length() - 2);
        }
    }

    private boolean fillByFieldType(String fieldType, StringBuilder raw, Faker faker, List<AnnotationMirror> annotations,
                                    List<? extends TypeMirror> typeArguments) {
        Pair<Integer, Integer> minAndMax = projectAnalyzer.getAnnotationValues(annotations);
        Integer min = minAndMax.getValue0();
        Integer max = minAndMax.getValue1();
        if (fieldType.equals(UUID_TYPE)) {
            raw.append(String.format("\\\"%s\\\"", UUID.randomUUID()));
            return true;
        } else if (checkFieldIsBoolean(fieldType)) {
            raw.append(String.format("\\\"%s\\\"", faker.bool().bool()));
            return true;
        } else if (checkFieldIsInteger(fieldType)) {
            raw.append(String.format("\\\"%s\\\"", faker.number()
                    .numberBetween(min != null ? min : MIN_DEFAULT_VALUE, max != null ? max : MAX_DEFAULT_VALUE)
            ));
            return true;
        } else if (checkFieldIsFractional(fieldType)) {
            raw.append(String.format("\\\"%s\\\"", faker.number()
                    .randomDouble(
                            DEFAULT_DECIMALS_COUNT,
                            min != null ? min : MIN_DEFAULT_VALUE,
                            max != null ? max : MAX_DEFAULT_VALUE
                    )
            ));
            return true;
        } else if (fieldType.equals(LOCAL_DATE_TYPE)) {
            raw.append(String.format("\\\"%s\\\"", LocalDate.now()));
            return true;
        } else if (fieldType.equals(LOCAL_DATE_TIME_TYPE)) {
            raw.append(String.format("\\\"%s\\\"", LocalDateTime.now()));
            return true;
        } else if (fieldType.equals(STRING_TYPE)) {
            Random random = new Random();
            String text = String.join(" ", faker.lorem().words(DEFAULT_WORDS_COUNT))
                    .substring(0, random.nextInt(
                                    min != null ? min : MIN_DEFAULT_VALUE, max != null ? max : MAX_DEFAULT_VALUE
                            )
                    );
            raw.append(String.format("\\\"%s\\\"", text));
            return true;
        } else if (checkFieldIsCollection(fieldType)) {
            raw.append("[");
            TypeMirror typeArgument = typeArguments.get(0);
            if (typeArgument != null) {
                String collectionInnerType = ((DeclaredType) typeArgument).asElement().getSimpleName().toString();
                for (int i = 0; i < 3; i++) {
                    boolean filled = fillByFieldType(collectionInnerType, raw, faker, annotations, typeArguments);
                    if (filled && i != 2) {
                        raw.append(", ");
                    }
                    if (!filled) {
                        return false;
                    }
                }
                raw.append("]");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean fillByFieldName(String fieldName, StringBuilder raw, Faker faker) {
        if (checkFieldIsEmail(fieldName)) {
            raw.append(String.format("\\\"%s\\\"", faker.internet().emailAddress()));
            return true;
        } else if (checkFieldIsFirstName(fieldName)) {
            raw.append(String.format("\\\"%s\\\"", faker.name().firstName()));
            return true;
        } else if (checkFieldIsSurName(fieldName)) {
            raw.append(String.format("\\\"%s\\\"", faker.name().lastName()));
            return true;
        } else if (checkFieldIsLastName(fieldName)) {
            raw.append(String.format("\\\"%s\\\"", faker.name().lastName()));
            return true;
        } else if (checkFieldIsFullName(fieldName)) {
            raw.append(String.format("\\\"%s\\\"", faker.name().name()));
            return true;
        }
        return false;
    }

    private boolean checkFieldIsEmail(String fieldName) {
        return EMAIL_FIELDS.contains(fieldName);
    }

    private boolean checkFieldIsFirstName(String fieldName) {
        return FIRST_NAME_FIELDS.contains(fieldName);
    }

    private boolean checkFieldIsSurName(String fieldName) {
        return SUR_NAME_FIELDS.contains(fieldName);
    }

    private boolean checkFieldIsLastName(String fieldName) {
        return LAST_NAME_FIELDS.contains(fieldName);
    }

    private boolean checkFieldIsFullName(String fieldName) {
        return FULL_NAME_FIELDS.contains(fieldName);
    }

    private boolean checkFieldIsBoolean(String fieldType) {
        return BOOLEAN_TYPES.contains(fieldType);
    }

    private boolean checkFieldIsInteger(String fieldType) {
        return INTEGERS_TYPES.contains(fieldType);
    }

    private boolean checkFieldIsFractional(String fieldType) {
        return FRACTIONAL_TYPES.contains(fieldType);
    }

    private boolean checkFieldIsCollection(String fieldType) {
        return COLLECTIONS_TYPES.contains(fieldType);
    }
}