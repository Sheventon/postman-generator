package ru.itis.postmangenerator.analyzer;

import org.javatuples.Pair;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ProjectAnalyzer {

    default void clearAndFillUrlsParts(StringBuilder cleanValue, List<String> urlParts) {
        cleanValue.delete(0, 2);
        cleanValue.delete(cleanValue.length() - 2, cleanValue.length());
        String[] parts = cleanValue.toString().split("/");
        urlParts.addAll(
                Arrays.stream(
                                Arrays.copyOfRange(
                                        parts, 1, cleanValue.toString().split("/").length)
                        )
                        .toList()
        );
    }

    List<Element> getHttpMethodsFromController(Element controller);

    Optional<? extends AnnotationMirror> checkHttpMethodMappingAnnotationIsPresentOnMethod(Element method);

    String getHttpMethodFromAnnotation(Optional<? extends AnnotationMirror> optionalHttpAnnotation);

    TypeMirror returnRequestBodyFieldType(Element method);

    List<Element> getFieldsFromRequestBody(TypeMirror requestBodyFieldType);

    List<String> extractCommonUrlParts(Element controller);

    List<AnnotationMirror> extractFieldValidationAnnotations(Element field);

    Pair<Integer, Integer> getAnnotationValues(List<AnnotationMirror> annotations);

    boolean elementFromJavaLangPackage(Element element);

    boolean innerFieldHasSamePackageThatParentField(Element parent, Element field);
}
