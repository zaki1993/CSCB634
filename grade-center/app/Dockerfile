#First stage to copy the uploaded jar and extract it to specified WORKDIR
FROM docker.io/openjdk:12.0.2 as builder
WORKDIR application
ARG JAR_FILE=${ssh_deploy_directory}/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

#Copy extracted jar layered content to the docker image, which will now have layered structure
FROM docker.io/openjdk:12.0.2
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
EXPOSE ${docker_container_exposed_port}
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
