package ru.itis.postmangenerator.generator;


import net.datafaker.Faker;
import ru.itis.postmangenerator.analyzer.ProjectAnalyzer;
import ru.itis.postmangenerator.analyzer.impl.SpringProjectAnalyzer;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static ru.itis.postmangenerator.constants.GeneratorConstants.POSTMAN_SCHEMA;

public class PostmanCollectionGenerator {

    private final ProjectAnalyzer projectAnalyzer = new SpringProjectAnalyzer();

    public List<String> commonUrlParts;

    public StringBuilder generateCollection(Element controller) {
        StringBuilder collection = new StringBuilder("{\n");
        Faker faker = new Faker(Locale.forLanguageTag("RU"));
        generateInfo(collection, controller.getSimpleName().toString(), faker);
        generateItems(collection, controller, faker);
        collection.append("}");
        return collection;
    }

    private void generateInfo(StringBuilder collection, String className, Faker faker) {
        collection.append("\t\"info\": {\n")
                .append(String.format("\t\t\"_postman_id\": \"%s\",\n", UUID.randomUUID()))
                .append(String.format("\t\t\"name\": \"%s\",\n", className))
                .append(String.format("\t\t\"schema\": \"%s\",\n", POSTMAN_SCHEMA))
                .append(String.format("\t\t\"_exporter_id\": \"%s\"\n", faker.number().positive()))
                .append("\t},\n");
    }

    private void generateItems(StringBuilder collection, Element controller, Faker faker) {
        collection.append("\t\"item\": [\n");
        projectAnalyzer.getHttpMethodsFromController(controller)
                .forEach(method -> generateItem(collection, method, faker));
        collection.delete(collection.length() - 2, collection.length() - 1);
        collection.append("\t]\n");
    }

    private void generateItem(StringBuilder collection, Element method, Faker faker) {

    }
}