import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.DontIncludeResourceTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.IncludeResourceTransformer

plugins {
  id("java-library")
  kotlin("jvm")
  id("com.github.johnrengelman.shadow")
  id("me.champeau.gradle.jmh")
}

jmh {
  jvmArgs = listOf("-Djmh.separateClasspathJAR=true")
  include = listOf("""com\.squareup\.okio\.benchmarks\.MessageDigestBenchmark.*""")
  duplicateClassesStrategy = DuplicatesStrategy.WARN
}

dependencies {
  api(project(":okio"))
  api(deps.jmh.core)
  jmh(project(path = ":okio", configuration = "jvmRuntimeElements"))
  jmh(deps.jmh.core)
  jmh(deps.jmh.generator)
}

tasks {
  val jmhJar by getting(ShadowJar::class) {
    transform(DontIncludeResourceTransformer().apply {
      resource = "META-INF/BenchmarkList"
    })

    transform(IncludeResourceTransformer().apply {
      resource = "META-INF/BenchmarkList"
      file = file("${project.buildDir}/jmh-generated-resources/META-INF/BenchmarkList")
    })
  }

  val assemble by getting {
    dependsOn(jmhJar)
  }
}
