import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.intellij") version "0.6.5"
  kotlin("jvm") version "1.4.30"
  java
}

apply(plugin = "kotlin")
apply(plugin = "org.jetbrains.intellij")

group = "eu.ibagroup"
version = "0.2"

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType(KotlinCompile::class).all {
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.toString()
    languageVersion = org.jetbrains.kotlin.config.LanguageVersion.LATEST_STABLE.versionString
  }
}

tasks.buildSearchableOptions {
  enabled = false
}

dependencies {
  implementation(group = "com.squareup.retrofit2", name = "retrofit", version = "2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.5.0")
  implementation("com.squareup.retrofit2:converter-scalars:2.1.0")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.30")
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
  implementation("org.jgrapht:jgrapht-core:1.5.0")
  implementation("eu.ibagroup:r2z:1.0.2")
  testImplementation("io.mockk:mockk:1.10.2")
  testImplementation("org.mock-server:mockserver-netty:5.11.1")
  testImplementation("junit", "junit", "4.12")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-api:5.7.1")
}

intellij {
  version = "2021.1.3"
}



tasks.getByName<PatchPluginXmlTask>("patchPluginXml") {
  sinceBuild("203.5981")
  untilBuild("211.*")
  changeNotes(
    """
      In version 0.2 we added:<br/>
      <ul>
        <li>Binary and text modes added for USS files and data sets</li>
        <li>Error messages are improved a bit</li>
        <li>Possibility to add a DS Mask right from File Explorer's context menu</li>
        <li>Small UI fixes</li>
      </ul>"""
  )
}

tasks.test {
  useJUnit()
  testLogging {
    events("passed", "skipped", "failed")
  }
}

sourceSets {
  create("apiTest") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
    java.srcDirs("src/apiTest/java", "src/apiTest/kotlin")
    resources.srcDirs("src/apiTest/resources")
  }
}

val apiTestImplementation by configurations.getting {
  extendsFrom(configurations.testImplementation.get())
}

configurations["apiTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

val apiTest = task<Test>("apiTest") {
  description = "Runs the integration tests for API."
  group = "verification"
  testClassesDirs = sourceSets["apiTest"].output.classesDirs
  classpath = sourceSets["apiTest"].runtimeClasspath
  testLogging {
    events("passed", "skipped", "failed")
  }
}
