rootProject.name = "tdd"
<<<<<<< HEAD

pluginManagement {
    val kotlinVersion = "1.9.20"
    val springBootVersion = "3.2.3"
    val springDependencyManagementVersion = "1.1.4"

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.jpa") version kotlinVersion
        kotlin("kapt") version kotlinVersion
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}
=======
>>>>>>> 81cbb449748bf89435a8e88a889ebd40241c94bd
