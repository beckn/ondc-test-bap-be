import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.*

buildscript {
  repositories {
    maven {
      mavenLocal()
      jcenter()// for local testing
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
  dependencies {
    classpath("org.shipkit:shipkit-auto-version:1.1.20")
    classpath("com.amazonaws:aws-java-sdk-iam:1.12.26")
    classpath("com.amazonaws:aws-java-sdk-ec2:1.12.26")
  }
}

plugins {
  id("jacoco")
  kotlin("jvm") version "1.4.31"
  kotlin("kapt") version "1.4.31"
  id("maven-publish")
  id("org.shipkit.shipkit-auto-version") version "1.1.20"
}

val versionProperties = loadVersionProps()
group = "org.beckn.jvm"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.+")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.+")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

val autoVersion by tasks.registering {
  val shipKitVersion = project.version.toString()
  project.ext.set("auto-versioning.build-number", shipKitVersion)
  val buildNumber = shipKitVersion.split(".").last()
  project.version = "${versionProperties.getProperty("becknVersion")}.${buildNumber}"
  project.ext.set("auto-versioning.final-version", project.version)
}

val gitTagBuildNumber by tasks.registering {
  dependsOn(autoVersion)
  doLast {
    val buildTag = "build-${project.ext.get("auto-versioning.build-number")}"
    gitTagAndPush(buildTag)
  }
}

val gitTagVersion by tasks.registering {
  dependsOn(autoVersion)
  doLast {
    val versionTag = "v-${project.ext.get("auto-versioning.final-version")}"
    gitTagAndPush(versionTag)
  }
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
    }
  }
  repositories {
    maven {
      url = uri("s3://beckn-maven-artifacts/releases")
      authentication {
        val awsIm by registering(AwsImAuthentication::class)
      }
    }
  }
}

fun loadVersionProps(): Properties {
  val versionProperties = Properties()
  versionProperties.load(FileInputStream(file("version.properties")))
  return versionProperties
}

fun String.execute(currentWorkingDir: File = file("./")): String {
  val byteOut = ByteArrayOutputStream()
  project.exec {
    workingDir = currentWorkingDir
    commandLine = this@execute.split("\\s".toRegex())
    standardOutput = byteOut
  }
  return String(byteOut.toByteArray()).trim()
}

fun gitTagAndPush(tagName: String) {
  "git tag $tagName".execute()
  "git push origin $tagName".execute()
}