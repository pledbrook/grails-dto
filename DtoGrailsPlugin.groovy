import grails.plugins.dto.DTO

import org.codehaus.groovy.grails.plugins.dto.DefaultGrailsDtoGenerator
import org.dozer.spring.DozerBeanMapperFactoryBean
import org.springframework.context.ApplicationContext

class DtoGrailsPlugin {
    // the plugin version
    def version = "0.2.4"
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
        // Create the DTO generator bean.
        if (application.config.grails.generate.indent) {
            dtoGenerator(DefaultGrailsDtoGenerator, true, application.config.grails.generate.indent)
        }
        else {
            dtoGenerator(DefaultGrailsDtoGenerator)
        }

        dozerMapper(DozerBeanMapperFactoryBean) {
            if (application.config.dto.mapping.files) {
                mappingFiles = application.config.dto.mapping.files
            }
        }
    }

    def doWithDynamicMethods = { final ctx ->
        // Add "as DTO" and toDTO() to domain classes.
        for (dc in application.domainClasses) {
            addDtoMethods(dc.metaClass, ctx)
        }

        // Add the toDTO(Class) method to collections.
        Collection.metaClass.toDTO = { obj ->
            // Find out what class the target collection should contain.
            def containedClass = obj instanceof Class ? obj : obj.getClass()

            // Next create a collection of the appropriate type.
            def clazz = delegate.getClass()
            if (SortedSet.isAssignableFrom(clazz)) {
                obj = new TreeSet()
            }
            else if (Set.isAssignableFrom(clazz)) {
                obj = new HashSet()
            }
            else {
                obj = new ArrayList(delegate.size())
            }

            // Finally, add the individual DTOs to the new collection.
            final mapper = ctx.getBean("dozerMapper")
            delegate.each { obj << mapper.map(it, containedClass) }
            return obj
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

    private addDtoMethods(final MetaClass mc, final ApplicationContext ctx) {
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

        mc.toDTO = { Class clazz ->
            // Convert the domain instance to a DTO.
            def mapper = ctx.getBean("dozerMapper")
            return mapper.map(delegate, clazz)
        }
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
