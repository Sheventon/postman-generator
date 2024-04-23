package ru.itis.postmangenerator.processor;

import com.google.auto.service.AutoService;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.postmangenerator.generator.PostmanCollectionGenerator;
import ru.itis.postmangenerator.writer.CollectionWriter;
import ru.itis.postmangenerator.writer.impl.JsonCollectionWriter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes({
        "org.springframework.web.bind.annotation.RestController"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class PostmanCollectionAnnotationProcessor extends AbstractProcessor {

    private final CollectionWriter collectionWriter = new JsonCollectionWriter();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedControllers = roundEnv.getElementsAnnotatedWith(RestController.class);
        for (Element controller : annotatedControllers) {
            StringBuilder collection = new PostmanCollectionGenerator().generateCollection(controller);
            collectionWriter.writeCollectionToFile(
                    processingEnv,
                    collection.toString(),
                    controller.getSimpleName().toString()
            );
        }
        return true;
    }
}
