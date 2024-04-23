package ru.itis.postmangenerator.analyzer.impl;

import ru.itis.postmangenerator.analyzer.ProjectAnalyzer;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpringProjectAnalyzer implements ProjectAnalyzer {

    private static final String INIT_METHOD_NAME = "<init>";

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
}
