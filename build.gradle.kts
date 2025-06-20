/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    java
    `maven-publish`
    application
}

repositories {
    maven {
        url = uri("https://m2.chew.pro/releases/")
        content {
            includeGroup("pw.chew")
        }
    }

    maven {
        url = uri("https://m2.chew.pro/snapshots/")
        content {
            includeModule("pw.chew.chewbotcca", "Chewbotcca")
        }
    }

    mavenCentral {
        content {
            excludeGroup("pw.chew")
        }
    }
}

dependencies {
    implementation("net.dv8tion", "JDA", "5.6.1")
    implementation("pw.chew", "jda-chewtils", "2.1")
    implementation("ch.qos.logback", "logback-classic", "1.5.7")
    implementation("org.json", "json", "20240303")
    implementation("org.reflections", "reflections", "0.10.2")
    implementation("org.mapdb", "mapdb", "3.1.0")
    implementation("org.knowm.xchart", "xchart", "3.8.8")
    implementation("pw.chew.chewbotcca", "Chewbotcca", "2.0-SNAPSHOT") {
        isTransitive = false
    }
}

group = "pw.chew"
version = "2.0"
description = "ChanServ"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("pw.chew.chanserv.ChanServ")
}
