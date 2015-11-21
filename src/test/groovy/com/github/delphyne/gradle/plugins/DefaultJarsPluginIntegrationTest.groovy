package com.github.delphyne.gradle.plugins

import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.util.jar.JarFile

@Test
class DefaultJarsPluginIntegrationTest {

	File projectDir
	GradleRunner runner

	@BeforeMethod
	void setup() {
		List<File> pluginClasspath = getClass()
				.classLoader
				.findResource('plugin-classpath.txt')
				.readLines()
				.collect {
			new File(it)
		}
		projectDir = File.createTempDir()
		projectDir.deleteOnExit()
		runner = GradleRunner
				.create()
				.withProjectDir(projectDir)
				.withPluginClasspath(pluginClasspath)
	}

	void testApply() {
		def name = this.class.simpleName
		def group = 'com.github.delphyne.gradle.tests'
		def version = '0.0.1'

		new File(projectDir, 'build.gradle').text = """
			plugins {
				id 'com.github.delphyne.default-jars'
			}
			apply plugin: GroovyPlugin
			apply plugin: MavenPlugin

			group='${group}'
			version='${version}'

			repositories {
				jcenter()
			}

			dependencies {
				compile "org.codehaus.groovy:groovy-all:${GroovySystem.version}"
			}

			uploadArchives {
				repositories {
					mavenDeployer {
						repository(url: new File(buildDir, '.m2').toURL())
					}
				}
			}
		"""

		new File(projectDir, 'settings.gradle').text = """
			rootProject.name='${name}'
		"""

		File groovySources = new File(projectDir, 'src/main/groovy')
		File javaSources = new File(projectDir, 'src/main/java')

		[groovySources, javaSources]*.mkdirs()

		new File(groovySources, 'Foo.groovy').text = """
			/**
			 * Random Comments
			 */
			interface Foo {
			}
		"""

		new File(javaSources, 'Bar.java').text = """
			/**
			 * More random stuff
			 */
			public interface Bar {
			}
		"""

		def result = runner
				.withArguments('uploadArchives')
				.build()

		['sourceJar', 'javadoc', 'javadocJar', 'groovydoc', 'groovydocJar'].each { taskName ->
			assert result.tasks.find { BuildTask t -> t.path == ":${taskName}".toString() }
		}

		File uploadDir = new File(projectDir, "build/.m2/${group.split(/\./).join('/')}/${name}/${version}")
		['javadoc', 'groovydoc'].each { classifier ->
			File jar = new File(uploadDir, "${[name, version, classifier].join('-')}.jar")
			assert jar.exists()
			assert new JarFile(jar).entries().find { it.toString() == 'index.html' }
		}

		File jar = new File(uploadDir, "${[name, version, 'sources'].join('-')}.jar")
		assert jar.exists()
		assert new JarFile(jar).entries().find { it.toString() == 'Foo.groovy' }
		assert new JarFile(jar).entries().find { it.toString() == 'Bar.java' }
	}

	void testDoesNotCreateEmptyJar() {
		new File(projectDir, 'build.gradle').text = """
			plugins {
				id 'com.github.delphyne.default-jars'
			}
			apply plugin: GroovyPlugin
			apply plugin: MavenPlugin

			uploadArchives {
				repositories {
					mavenDeployer {
						repository(url: new File(buildDir, '.m2').toURL())
					}
				}
			}
		"""

		runner
				.withArguments('uploadArchives')
				.build()

		assert ! new File(projectDir, 'build/libs/').listFiles().find { it.name.contains('groovydoc' )}
		assert ! new File(projectDir, 'build/libs/').listFiles().find { it.name.contains('javadoc' )}
	}
}
