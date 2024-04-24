# Postman Collection Generator project

Данный проект предназначен для генерации коллекций для Postman в compile time.
Проект представляет из себя Annotation Processor, который во время компиляции классов приложения генерирует файлы коллекций.
Далее сгенеририванный файл можно ипортировать в Postman и работать с сгенерированной коллекцией.

## Project Structure
Проект представляет из себя тестовое Spring Boot приложение с слоем котроллеров и дто, для проверки работы annotation processor-а.
Внутри проекта присутствует модуль ```postman-annotation-processor```, содержащий в себе Annotation Processor,
который обрабатывает контроллеры приложения и на их основе генерирует Postman коллекции в формате json.

## Installation
Для работы модуля postman-annotation-processor необходимо подключить его в build.gradle в корне проекта:
```groovy
annotationProcessor project(":postman-annotation-processor")
```
Далее для проверки работы подключенного annotation processor-а необходимо скомпилировать проект ```postman-generator```.

## Usage
ПОсле подключения annotation processor в проект необходимо скомпилировать его. Для этого нужно вызвать команду 
assemble для gradle. После компиляции проекта в папке build появится папка generated/sources/annotationProcessor/resources, 
внутри которой будут находиться сгенерированные коллекции для Postman. Для каждого контроллера будет генерироваться 
отдельная коллекция.

Коллекция представляет из себя json файл, внутри которого по стандарту для Postman будут содержаться все методы 
контроллеров приложения, с предзаполненными URL, методами HTTP запроса, а также с предзаполнненным телом запроса, 
если таковое необходимо.

## Technologies

* java 17
* spring boot 3.2.0