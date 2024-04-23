package ru.itis.postmangenerator.analyzer.impl;

import org.javatuples.Pair;
import org.springframework.web.bind.annotation.RequestBody;
import ru.itis.postmangenerator.analyzer.ProjectAnalyzer;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Collectors;

public class SpringProjectAnalyzer implements ProjectAnalyzer {

    private static final String INIT_METHOD_NAME = "<init>";
    private static final String ANNOTATION_DEFAULT_PARAMETER_NAME = "value";

    public static final String JAVAX_VALIDATION_PACKAGE = "javax.validation.constraints";
    public static final String JAVA_LANG_PACKAGE = "java.lang";

    public static final String MIN_ANNOTATION_NAME = "Min";
    public static final String MAX_ANNOTATION_NAME = "Max";
    public static final String SIZE_ANNOTATION_NAME = "Size";

    public static final String MIN_PARAMETER_NAME = "min";
    public static final String MAX_PARAMETER_NAME = "max";

    private static final Map<String, String> MAPPING_ANNOTATIONS = new HashMap<>();

    static {
        MAPPING_ANNOTATIONS.put("GetMapping", "GET");
        MAPPING_ANNOTATIONS.put("PostMapping", "POST");
        MAPPING_ANNOTATIONS.put("PutMapping", "PUT");
        MAPPING_ANNOTATIONS.put("DeleteMapping", "DELETE");
        MAPPING_ANNOTATIONS.put("PatchMapping", "PATCH");
    }

    @Override
    public List<Element> getHttpMethodsFromController(Element controller) {
        return controller.getEnclosedElements()
                .stream()
                .filter(element ->
                        !element.getKind().isClass() &&
                                !element.getKind().isField() &&
                                !element.getKind().isInterface() &&
                                !element.getSimpleName().contentEquals(INIT_METHOD_NAME)
                )
                .collect(Collectors.toList());
    }

    @Override
    public Optional<? extends AnnotationMirror> checkHttpMethodMappingAnnotationIsPresentOnMethod(Element method) {
        return method.getAnnotationMirrors()
                .stream()
                .filter(annotationMirror ->
                        MAPPING_ANNOTATIONS.containsKey(
                                annotationMirror
                                        .getAnnotationType()
                                        .asElement()
                                        .getSimpleName()
                                        .toString()
                        )
                )
                .findFirst();
    }

    @Override
    public String getHttpMethodFromAnnotation(Optional<? extends AnnotationMirror> optionalHttpAnnotation) {
        return optionalHttpAnnotation.map(annotationMirror -> MAPPING_ANNOTATIONS.get(
                        annotationMirror.getAnnotationType().asElement().getSimpleName().toString()
                ))
                .orElse(null);
    }

    @Override
    public TypeMirror returnRequestBodyFieldType(Element method) {
        ExecutableElement methodElement = (ExecutableElement) method;
        List<? extends VariableElement> parameters = methodElement.getParameters();
        for (VariableElement parameter : parameters) {
            if (parameter.getAnnotation(RequestBody.class) != null) {
                return parameter.asType();
            }
        }
        return null;
    }

    @Override
    public List<Element> getFieldsFromRequestBody(TypeMirror requestBodyFieldType) {
        if (requestBodyFieldType != null) {
            DeclaredType requestBodyClass = (DeclaredType) requestBodyFieldType;
            return requestBodyClass.asElement().getEnclosedElements()
                    .stream()
                    .filter(element -> element.getKind().isField())
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> extractCommonUrlParts(Element controller) {
        List<String> commonUrlParts = new ArrayList<>();
        controller.getAnnotationMirrors().forEach(annotationMirror ->
                annotationMirror.getElementValues().forEach((key, value) -> {
                    if (key.getSimpleName().toString().equals(ANNOTATION_DEFAULT_PARAMETER_NAME) || key.isDefault()) {
                        StringBuilder cleanValue = new StringBuilder(String.valueOf(value));
                        if (!cleanValue.isEmpty()) {
                            clearAndFillUrlsParts(cleanValue, commonUrlParts);
                        }
                    }
                })
        );
        return commonUrlParts;
    }

    @Override
    public List<AnnotationMirror> extractFieldValidationAnnotations(Element field) {
        return filterAnnotationsByPackage(field.getAnnotationMirrors(), JAVAX_VALIDATION_PACKAGE);
    }

    @Override
    public Pair<Integer, Integer> getAnnotationValues(List<AnnotationMirror> annotations) {
        Integer min = null;
        Integer max = null;
        for (AnnotationMirror annotation : annotations) {
            if (annotation.getAnnotationType().asElement().getSimpleName().toString().equals(SIZE_ANNOTATION_NAME)) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
                    ExecutableElement key = entry.getKey();
                    AnnotationValue value = entry.getValue();
                    if (key.getSimpleName().toString().equals(MIN_PARAMETER_NAME)) {
                        min = (Integer) value.getValue();
                    }
                    if (key.getSimpleName().toString().equals(MAX_PARAMETER_NAME))
                        max = (Integer) value.getValue();
                }
            } else {
                min = getValueFromMinOrMaxAnnotation(min, annotation, MIN_ANNOTATION_NAME);
                max = getValueFromMinOrMaxAnnotation(max, annotation, MAX_ANNOTATION_NAME);
            }
        }
        return Pair.with(min, max);
    }

    @Override
    public boolean elementFromJavaLangPackage(Element element) {
        PackageElement annotationPackage;
        if (!element.asType().getKind().isPrimitive()) {
            annotationPackage = getPackage(((DeclaredType) element.asType()).asElement());
        } else {
            return true;
        }
        return annotationPackage != null && (annotationPackage.getQualifiedName().toString().startsWith(JAVA_LANG_PACKAGE));
    }

    private Integer getValueFromMinOrMaxAnnotation(Integer min, AnnotationMirror annotation, String minAnnotationName) {
        if (annotation.getAnnotationType().asElement().getSimpleName().toString().equals(minAnnotationName)) {
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
                String value = entry.getValue().toString().replace("L", "");
                min = Integer.valueOf(value);
            }
        }
        return min;
    }

    private List<AnnotationMirror> filterAnnotationsByPackage(List<? extends AnnotationMirror> annotations, String packageName) {
        List<AnnotationMirror> filteredAnnotations = new ArrayList<>();
        annotations.forEach(annotationMirror -> {
            Element annotationElement = annotationMirror.getAnnotationType().asElement();
            PackageElement annotationPackage = getPackage(annotationElement);
            if (annotationPackage != null && annotationPackage.getQualifiedName().toString().startsWith(packageName)) {
                filteredAnnotations.add(annotationMirror);
            }
        });
        return filteredAnnotations;
    }

    private PackageElement getPackage(Element element) {
        while (!(element instanceof PackageElement) && element != null) {
            element = element.getEnclosingElement();
        }
        return (PackageElement) element;
    }
}
