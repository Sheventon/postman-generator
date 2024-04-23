package ru.itis.postmangenerator.writer.impl;

import ru.itis.postmangenerator.writer.CollectionWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;

public class JsonCollectionWriter implements CollectionWriter {

    @Override
    public void writeCollectionToFile(ProcessingEnvironment processingEnv, String collection, String string) {
        try {
            FileObject file = processingEnv.getFiler()
                    .createResource(
                            StandardLocation.SOURCE_OUTPUT,
                            "resources",
                            string + ".json"
                    );
            Writer writer = file.openWriter();
            writer.write(collection);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
