plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation(files("libs/paapi5-java-sdk-1.0.0.jar"))

    implementation("io.swagger:swagger-annotations:1.5.15")
    implementation("com.squareup.okhttp:okhttp:2.7.5")
    implementation("com.squareup.okhttp:logging-interceptor:2.7.5")
    implementation("com.google.code.gson:gson:2.8.1")
    implementation("io.gsonfire:gson-fire:1.8.0")
    implementation("org.threeten:threetenbp:1.3.5")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit4 test framework
            useJUnit("4.13.2")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "org.example.App"
}
