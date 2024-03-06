FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/todo
# Add a non-root user
RUN addgroup -S todo && adduser -S todo -G todo
# Switch to the non-root user
COPY build.gradle settings.gradle gradlew .
COPY gradle gradle
COPY src src
RUN chown -R todo:todo /workspace/todo
USER todo
RUN ./gradlew build -x test
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../../build/libs/todo-0.0.1-SNAPSHOT.jar)

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/todo/target/dependency
# Add a non-root user
RUN addgroup -S todo && adduser -S todo -G todo
# Switch to the non-root user
USER todo
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /todo/lib
COPY --from=build ${DEPENDENCY}/META-INF /todo/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /todo
ENTRYPOINT ["java","-cp","todo:todo/lib/*","com.simplesystem.todo.TodoApplication"]