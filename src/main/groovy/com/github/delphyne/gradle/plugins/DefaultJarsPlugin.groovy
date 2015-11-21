package com.github.delphyne.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Groovydoc
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * A plugin which generates several jars by default, and adds them as project artifacts.
 *
 * If a plugin has been applied that creates the sourceSets project property, a sourceJar task is added
 * and a jar containing project.sourceSets.main.allSource is added.
 *
 * If the javadoc task exists, a javadocJar task is added and its output is added.
 *
 * If the groovydoc task exists, a groovydocJar task is added and its output is added.
 */
class DefaultJarsPlugin implements Plugin<Project> {
	@Override
	void apply(Project project) {
		project.afterEvaluate {
			if (project.hasProperty('sourceSets')) {
				Jar sources = project.tasks.create('sourceJar', Jar)
				sources.with {
					classifier = 'sources'
					description = 'Assembles a jar archive containing the project sources.'
					from project.sourceSets.main.allSource
				}
				project.artifacts.archives sources
			}

			Javadoc javadoc = (Javadoc) project.tasks.findByName('javadoc')
			if (! javadoc?.inputs?.sourceFiles?.isEmpty()) {
				Jar javadocs = project.tasks.create('javadocJar', Jar)
				javadocs.with{
					classifier = 'javadoc'
					description = 'Assembles a jar archive containing the Javadocs.'
					dependsOn javadoc
					from javadoc.destinationDir
				}
				project.artifacts.archives javadocs
			}

			Groovydoc groovydoc = (Groovydoc) project.tasks.findByName('groovydoc')
			if (! groovydoc?.inputs?.sourceFiles?.isEmpty()) {
				Jar groovydocs = project.tasks.create('groovydocJar', Jar)
				groovydocs.with {
					classifier = 'groovydoc'
					description = 'Assembles a jar archive containing the Groovydocs.'
					dependsOn groovydoc
					from(groovydoc.destinationDir)
				}
				project.artifacts.archives groovydocs
			}
		}
	}
}
