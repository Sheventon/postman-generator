package ru.itis.postmangenerator.analyzer;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.util.List;
import java.util.Optional;

public interface ProjectAnalyzer {

    List<Element> getHttpMethodsFromController(Element controller);

    Optional<? extends AnnotationMirror> checkHttpMethodMappingAnnotationIsPresentOnMethod(Element method);
}
