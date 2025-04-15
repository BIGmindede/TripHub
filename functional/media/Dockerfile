# Используйте образ Maven с OpenJDK 21
FROM maven:3-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

# Скачайте зависимости
RUN mvn dependency:go-offline -B

COPY src ./src

# Соберите проект
RUN mvn clean package -DskipTests

# Используйте образ OpenJDK 21 для запуска приложения
FROM openjdk:21-jdk-slim

WORKDIR $APP_HOME

# Копируйте собранный JAR-файл из предыдущего слоя
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

CMD ["--spring.profiles.active=prod"]

ONBUILD RUN echo "Сборка завершена"
