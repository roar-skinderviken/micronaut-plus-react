plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.micronaut.application)
}

dependencies {
    runtimeOnly("org.yaml:snakeyaml")
    implementation("io.micronaut.views:micronaut-views-thymeleaf")

    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib.jdk8)

    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("io.micronaut:micronaut-http-client")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs
            .addAll(
                "-Xjsr305=strict",
                "-Xannotation-default-target=param-property"
            )
    }
}

application {
    mainClass = "no.javatec.ApplicationKt"
}

micronaut {
    version = libs.versions.micronaut.platform.version.get()
    runtime("netty")
    testRuntime("kotest5")
    processing {
        incremental(true)
        annotations("no.javatec.*")
    }
}

tasks.test {
    jvmArgs(
        "-Xshare:off",
        "-XX:+EnableDynamicAgentLoading",
        "-Dkotest.framework.classpath.scanning.autoscan.disable=true",
        "-Dkotest.framework.config.fqn=no.javatec.ProjectConfig",
    )
    useJUnitPlatform()
}

tasks.register<Copy>("processFrontendResources") {
    val backendTargetDir = project.layout.buildDirectory.dir("resources/main/static")
    val frontendBuildDir =
        project(":frontend").layout.projectDirectory.dir("dist")

    group = "Frontend"
    description = "Process frontend resources"
    dependsOn(":frontend:assembleFrontend")

    from(frontendBuildDir)
    into(backendTargetDir)
}

tasks.named("processResources") {
    dependsOn("processFrontendResources")
}