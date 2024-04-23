package ru.itis.postmangenerator.writer;

import javax.annotation.processing.ProcessingEnvironment;

public interface CollectionWriter {


    void writeCollectionToFile(ProcessingEnvironment processingEnv, String collection, String string);
}
