package com.github.delphyne.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Groovydoc
import org.gradle.api.tasks.javadoc.Javadoc

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
			if (javadoc) {
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
			if (groovydoc) {
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
