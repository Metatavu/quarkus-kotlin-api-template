import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.allopen") version "1.5.31"
    id("io.quarkus")
    id("org.openapi.generator") version "5.3.0"
    id("org.jetbrains.kotlin.kapt") version "1.5.31"
}

repositories {
    mavenLocal()
    mavenCentral()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val jaxrsFunctionalTestBuilderVersion: String by project
val testContainersVersion: String by project
val testContainersKeycloakVersion: String by project
val moshiVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-container-image-docker")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("io.quarkus:quarkus-liquibase")
    implementation("io.quarkus:quarkus-jdbc-mysql")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("org.apache.commons:commons-lang3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("com.squareup.okhttp3:okhttp")
    testImplementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    testImplementation("com.squareup.moshi:moshi-adapters:$moshiVersion")
    testImplementation("org.testcontainers:localstack:1.16.0")
    testImplementation("fi.metatavu.jaxrs.testbuilder:jaxrs-functional-test-builder:$jaxrsFunctionalTestBuilderVersion")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:mysql:$testContainersVersion")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("com.github.dasniko:testcontainers-keycloak:$testContainersKeycloakVersion")
    kapt("org.hibernate:hibernate-jpamodelgen:5.5.7.Final")
}

group = "fi.metatavu.example"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

sourceSets["main"].java {
    srcDir("build/generated/api-spec/src/gen/java")
}
sourceSets["test"].java {
    srcDir("build/generated/api-client/src/main/kotlin")
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("javax.enterprise.context.RequestScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
    annotation("javax.persistence.Entity")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    kotlinOptions.javaParameters = true
}

val generateApiSpec = tasks.register("generateApiSpec",GenerateTask::class){
    setProperty("generatorName", "jaxrs-spec")
    setProperty("inputSpec",  "$rootDir/api-spec-template/swagger.yaml")
    setProperty("outputDir", "$buildDir/generated/api-spec")
    setProperty("apiPackage", "fi.metatavu.example.api.spec")
    setProperty("invokerPackage", "fi.metatavu.example.api.invoker")
    setProperty("modelPackage", "fi.metatavu.example.api.model")
    this.configOptions.put("dateLibrary", "java8")
    this.configOptions.put("interfaceOnly", "true")
    this.configOptions.put("returnResponse", "true")
    this.configOptions.put("useSwaggerAnnotations", "false")
    this.configOptions.put("additionalModelTypeAnnotations", "@io.quarkus.runtime.annotations.RegisterForReflection")
}

val generateApiClient = tasks.register("generateApiClient",GenerateTask::class){
    setProperty("generatorName", "kotlin")
    setProperty("library", "jvm-okhttp3")
    setProperty("inputSpec",  "$rootDir/api-spec-template/swagger.yaml")
    setProperty("outputDir", "$buildDir/generated/api-client")
    setProperty("packageName", "fi.metatavu.example.api.client")
    this.configOptions.put("dateLibrary", "string")
    this.configOptions.put("collectionType", "array")
}

tasks.named("clean") {
    this.doFirst {
        file("$rootDir/src/gen").deleteRecursively()
    }
}

tasks.named("compileKotlin") {
    dependsOn(generateApiSpec)
}

tasks.named("compileTestKotlin") {
    dependsOn(generateApiClient)
}