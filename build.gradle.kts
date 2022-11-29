import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("org.springframework.boot") version "2.7.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

group = "me.geso"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:1.17.6")
        mavenBom("io.ktor:ktor-bom:2.1.3")
        mavenBom("com.amazonaws:aws-java-sdk-bom:1.12.351")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-servlet")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("io.ktor:ktor-server-default-headers")
    implementation("io.ktor:ktor-server-call-logging")

    implementation("com.amazonaws:aws-java-sdk-s3")

    implementation("io.kweb:kweb-core:1.2.0")

    implementation("javax.xml.bind:jaxb-api:2.3.1")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.github.ben-manes.caffeine:caffeine:3.1.2")

    // NLP
    implementation("com.atilika.kuromoji:kuromoji-ipadic:0.9.0")

    // markdown
    implementation("com.vladsch.flexmark:flexmark-all:0.64.0")

    // mybatis, mysql
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.2")
    runtimeOnly("mysql:mysql-connector-java")

    implementation("io.github.microutils:kotlin-logging:3.0.4")

    // feed generation
    implementation("com.rometools:rome:1.18.0")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("io.mockk:mockk-jvm:1.13.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        // Make sure output from standard out or error is shown in Gradle output.
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks.withType<BootBuildImage> {
    environment = mapOf("BPL_JVM_THREAD_COUNT" to "30")
}

detekt {
    config = files("config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}

