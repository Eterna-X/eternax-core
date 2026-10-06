// Framework-free shared library: domain model, exception hierarchy, event contracts (HLD 16.8).
// It must never depend on Spring, a database driver, Kafka or the AWS SDK.
plugins {
    `java-library`
    `maven-publish`
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.2.1"
}

repositories {
    if (providers.gradleProperty("useMavenLocal").isPresent) mavenLocal()   // local verification only
    mavenCentral()
}

dependencyManagement { imports { mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.16") } }

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-parameters", "-Xlint:all,-processing,-serial"))
}
tasks.withType<Test> {
    useJUnitPlatform()
    testLogging { events("failed", "skipped"); exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL }
}

spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat("1.28.0").aosp().reflowLongStrings()
        removeUnusedImports()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            // write resolved versions into the POM so consumers never depend on our BOM setup
            versionMapping { allVariants { fromResolutionResult() } }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Eterna-X/eternax-core")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
