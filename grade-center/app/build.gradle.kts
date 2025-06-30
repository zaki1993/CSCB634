plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")

    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(project(":controller"))
    implementation(project(":model"))
    implementation(project(":service"))

    /*******************************************************************************************************
     *   Persistence
     ******************************************************************************************************/
    runtimeOnly("com.h2database:h2:2.2.224")
    implementation("org.postgresql:postgresql:42.7.3")

    // Spring Boot dependencies for testing
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        // Exclude JUnit Vintage to avoid conflicts
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    // JUnit 5 (Jupiter) dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")

    // For Kotlin Reflection
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10") // Use the version you are using

    // Spring Test dependencies for mocking and other utilities
    testImplementation("org.springframework:spring-test")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

var graduationSystemImageName = findProperty("container_registry")
    .toString()
    .plus("${rootProject.name}.${project.name}:")
    .toLowerCase()
    .plus("${project.findProperty("version").toString()}")

var containerName = "${rootProject.name}.${project.name}-${project.findProperty("version").toString()}".replace(".", "-").replace("_", "-")

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    imageName = graduationSystemImageName
}

tasks {
    task<Exec>("dockerRun") {
        group = "grad-system"
        val args = mutableListOf<String>()

        args.add("docker")
        args.add("run")
        args.add("-d")
        args.add("--name")
        args.add(containerName)
        args.add("--memory=4g")
        args.add("--restart")
        args.add("on-failure:3")
        args.add("-e")
        // Allow docker to access the local database on the same machine
        args.add("GRAD_SYSTEM_URL=jdbc:postgresql://host.docker.internal:5432/graduation-system-db")
        args.add("-p")
        args.add("8080:8080")
        args.add(graduationSystemImageName)
        commandLine = args
        println("Running container: $args")
    }
}