
plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

// 공통 의존성 버전 관리
dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

subprojects {
	// 각 모듈에 플러그인 적용
	apply(plugin = "java")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	// 공통 자바 설정
	java {
		toolchain {
			languageVersion = JavaLanguageVersion.of(17)
		}
	}

	// 컴파일 할 때 -parameters 옵션 추가
	tasks.withType<JavaCompile> {
		options.compilerArgs.add("-parameters")
	}

	// 공통 저장소 설정
	repositories {
		mavenCentral()
	}

	dependencies {
		// Spring
		implementation("org.springframework.boot:spring-boot-starter-actuator")
		//implementation("org.springframework.boot:spring-boot-starter-data-jpa")
		//implementation("org.springframework.boot:spring-boot-starter-web")

		// swagger
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

		// Lombok
		compileOnly("org.projectlombok:lombok")
		annotationProcessor("org.projectlombok:lombok")
		testCompileOnly("org.projectlombok:lombok")
		testAnnotationProcessor("org.projectlombok:lombok")

		// DB
		//runtimeOnly("com.mysql:mysql-connector-j")

		// redis
		implementation("org.springframework.boot:spring-boot-starter-data-redis")
		implementation("org.redisson:redisson-spring-boot-starter:3.30.0")

		// webclient 추가
		implementation("org.springframework.boot:spring-boot-starter-webflux")

		// caffeine cache
//	implementation("com.github.ben-manes.caffeine:caffeine")

		// Test
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("org.springframework.boot:spring-boot-testcontainers")
		testImplementation("org.testcontainers:junit-jupiter")
		testImplementation("org.testcontainers:mysql")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
}
