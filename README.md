[![Build Status](https://travis-ci.org/delphyne/gradle-default-jars.svg?branch=master)](https://travis-ci.org/delphyne/gradle-default-jars)

# gradle-default-jars
_Automatically add and publish source and doc jar_

This plugin automatically adds a source jar containing the contents of project.sourceSets.main.allSource, a
javadoc jar, and a groovydoc jar (if appropriate) to your projects artifacts.

## Installation

### Within a standalone build.gradle
```groovy
apply plugin: 'com.github.delphyne.default-jars'

buildscript {
	repositories {
		maven {
			url 'https://delphyne.github.io/.m2/'
		}
	}
	dependencies {
		classpath 'com.github.delphyne:default-jars-gradle-plugin:0.0.1'
	}
}
```

### With a buildSrc directory
#### buildSrc/build.gradle
```groovy
repositories {
	maven {
		url 'https://delphyne.github.io/.m2/'
	}
}

dependencies {
	compile 'com.github.delphyne:default-jars-gradle-plugin:0.0.1'
}
```

#### build.gradle
```groovy
apply plugin: 'com.github.delphyne.default-jars'
```

## Usage

Add the plugin.
