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
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    //implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(project(":service"))
    implementation(project(":model"))


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