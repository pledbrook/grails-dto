import grails.plugins.dto.DTO

import org.codehaus.groovy.grails.plugins.dto.DefaultGrailsDtoGenerator
import org.dozer.spring.DozerBeanMapperFactoryBean

class DtoGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    def loadAfter = [ "converters" ]

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

    def doWithDynamicMethods = { final ctx ->
        // Add "as DTO" and toDTO() to domain classes.
        for (dc in application.domainClasses) {
            def mc = dc.metaClass

            // First add the "as DTO".
            final originalAsType = mc.getMetaMethod("asType", [ Class ] as Object[])
            mc.asType = { Class clazz ->
                if (DTO == clazz) {
                    // Do the DTO conversion.
                    return mapDomainInstance(ctx, delegate)
                }
                else {
                    // Use the original asType implementation.
                    return originalAsType.invoke(delegate, [ clazz ] as Object[])
                }
            }

            // Then the toDTO() method.
            mc.toDTO = {->
                return mapDomainInstance(ctx, delegate)
            }
        }
    }

    def doWithApplicationContext = { final ctx ->
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

    /**
     * Uses the Dozer mapper to map a domain instance to its corresponding
     * DTO.
     * @param ctx The Spring application context containing the Dozer
     * mapper.
     * @param obj The domain instance to map.
     * @return The DTO corresponding to the given domain instance.
     */
    private mapDomainInstance(ctx, obj) {
        // Get the appropriate DTO class for this domain instance.
        def dtoClassName = obj.getClass().name + "DTO"
        def dtoClass = obj.getClass().classLoader.loadClass(dtoClassName)

        // Now convert the domain instance to a DTO.
        def mapper = ctx.getBean("dozerMapper")
        return mapper.map(obj, dtoClass)
    }
}
