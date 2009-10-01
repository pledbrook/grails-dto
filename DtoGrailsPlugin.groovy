import org.codehaus.groovy.grails.plugins.dto.DefaultGrailsDtoGenerator
import org.dozer.spring.DozerBeanMapperFactoryBean

class DtoGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Peter Ledbrook"
    def authorEmail = ""
    def title = "DTO plugin for Grails"
    def description = """\\
Manually creating and managing DTO objects for your domain classes is
labour intensive and error-prone. This plugin aims to simplify DTOs
by automatically generating them and providing a mechanism to easily
map domain class instances to DTO instances.
"""

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/dto"

    def doWithSpring = {
        dtoGenerator(DefaultGrailsDtoGenerator)
        dozerMapper(DozerBeanMapperFactoryBean)
    }

    def doWithDynamicMethods = { ctx ->
    }

    def doWithApplicationContext = { applicationContext ->
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
