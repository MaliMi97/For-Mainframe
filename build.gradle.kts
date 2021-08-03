import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.intellij") version "0.6.5"
  kotlin("jvm") version "1.4.32"
  java
}

apply(plugin = "kotlin")
apply(plugin = "org.jetbrains.intellij")

group = "eu.ibagroup"
version = "0.4.1"

repositories {
  mavenCentral()
  //temporary solution to enable ZOWE community work
  flatDir {
    dir("libs")
  }
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
  implementation("eu.ibagroup:r2z:1.0.13")
  implementation("com.segment.analytics.java:analytics:+")
  testImplementation("io.mockk:mockk:1.10.2")
  testImplementation("org.mock-server:mockserver-netty:5.11.1")
//  testImplementation("junit", "junit", "4.12")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.7.1")
}

intellij {
  version = "2021.1.3"
}

tasks.getByName<PatchPluginXmlTask>("patchPluginXml") {
  sinceBuild("203.5981")
  untilBuild("211.*")
  changeNotes(
    """
      In version 0.4.1 we added:<br/>
      <ul>
        <li>Pride logo to support LGBTQIA+ community. Peace, love, pride</li>
        <li>Job submission by the right click on files in the File Explorer</li>
        <li>Move and Copy operations are available for USS files and directories</li>
        <li>Editing Working Sets is now accessible by the right click on the Working Set in the File Explorer</li>
        <li>Tracking analytics events is now enabled with corresponding Privacy Policy</li>
        <li>Small UI fixes.</li>
      </ul>"""
  )
}


tasks.test {
  useJUnitPlatform()
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