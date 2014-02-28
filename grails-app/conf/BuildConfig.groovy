grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()

        mavenRepo "http://download.java.net/maven/2/"
    }

    dependencies {
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
    }

    plugins {
        compile ':cache:1.0.1'
        compile ":joda-time:1.4"
        compile ":mail:1.0.1"
        compile ':quartz:1.0.1'

        runtime ":database-migration:1.2.1"
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.8.3"
        runtime ":resources:1.1.6"

        test(":spock:0.7") {
            exclude "spock-grails-support"
        }

        build ":tomcat:$grailsVersion"
    }
}
