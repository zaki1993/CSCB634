plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
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