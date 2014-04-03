import javax.naming.InitialContext

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

grails.serverURL = "http://${InetAddress.localHost.hostAddress}:8300/$appName"

grails {
    mail {
        'default' {
            from = "gogoduck@emii.org.au"
        }

        // Configurable mail properties: see http://grails.org/plugin/mail
        host = "localhost"
        port = 25
        props = ["mail.smtp.auth": "false"]
    }
}

job {
    cleanup {
        daysToKeep = 10
        trigger = '0 0 0 * * ?'
    }
}

worker {
    fileLimit = 100
    outputFilename = "IMOS-aggregation-"
    cmd = {
        "web-app/resources/worker/gogoduck.sh ${it}"
    }
    outputPath = '/tmp/jobs'
}

environments {
    development {
        grails.logging.jul.usebridge = true
        grails.mail.disabled=true
        worker {
            cmd = {
                def filename = (it =~ /-o ([a-zA-Z0-9\/\.:-]+)/)[0][1] // Extracts filename from the options added to the gogoduck shell command
                [ 'bash', '-c', "echo bytes > ${filename}" ]
                // "test/resources/error.sh" // Uncomment this to test error handling.
            }
            outputPath = 'jobs'
        }
    }
    production {
        grails.logging.jul.usebridge = false
    }
}

/**
 * Instance specific customisation, clearly stolen from:
 * http://phatness.com/2010/03/how-to-externalize-your-grails-configuration/
 *
 * To use set for a specific instance, either set the environment variable "INSTANCE_NAME", or add this in the grails
 * commandline like so:
 *
 * grails -DINSTANCE_NAME=WA run-app
 *
 * Instance specific config files are located in $project_home/instances/
 *
 * Any configuration found in these instance specific file will OVERRIDE values set in Config.groovy and
 * application.properties.
 *
 * NOTE: app.name and version is ignored in external application.properties
 */
if(!grails.config.locations || !(grails.config.locations instanceof List)) {
    grails.config.locations = []
}

try {
    configurationPath = new InitialContext().lookup('java:comp/env/aodn.configuration')
    grails.config.locations << "file:${configurationPath}"

    println "Loading external config from '$configurationPath'..."
}
catch (e) {
    println "Not loading external config"
}

def log4jConversionPattern = '%d [%t] %-5p %c - %m%n'

log4j = {
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: log4jConversionPattern)
        'null' name: "stacktrace"
    }

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    info   'au.org.emii.gogoduck.worker.Worker'

    environments {
        development {
            debug 'grails.app.controllers',
                  'grails.app.services.au.org.emii.gogoduck.job.JobStoreService',
                  'grails.app.services.au.org.emii.gogoduck.job.NotificationService',
                  'au.org.emii.gogoduck.job.Job',
                  'au.org.emii.gogoduck.worker.Worker'
        }
    }

    root {
        info 'stdout', 'null'
    }
}
