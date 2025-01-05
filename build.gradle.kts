import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.9.25"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion

    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"

    id("com.google.cloud.tools.jib") version "3.4.4"
}

group = "com.planverse"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

val kotlinSdkVersion = "1.3.104"
val jwtVersion = "0.12.6"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.cloud:spring-cloud-starter-config:4.2.0")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
    implementation("org.bgee.log4jdbc-log4j2:log4jdbc-log4j2-jdbc4.1:1.16")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.postgresql:postgresql")
    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    implementation("aws.sdk.kotlin:s3:$kotlinSdkVersion")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    compileOnly("org.projectlombok:lombok")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jwtVersion")

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    testCompileOnly("org.projectlombok:lombok")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testAnnotationProcessor("org.projectlombok:lombok")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
    annotation("com.planverse.server.common.annotation.MyBatisResponse")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    from {
        image = "amazoncorretto:17"
    }
    to {
        image = System.getenv("JIB_IMAGE")
        tags = setOf("latest")
        auth {
            username = System.getenv("TOKEN_USER")
            password = System.getenv("TOKEN_PWD")
        }
    }
    container {
        jvmFlags = listOf(
            "-Dspring.profiles.active=" + System.getenv("PROFILE_ACTIVE"),
            "-Dspring.jwt.secret=" + System.getenv("JWT_ENC_PWD"),
            "-Djasypt.encryptor.password=" + System.getenv("JASYPT_ENCRYPTOR_PASSWORD"),
        )
        ports = listOf("50051")
    }
}
