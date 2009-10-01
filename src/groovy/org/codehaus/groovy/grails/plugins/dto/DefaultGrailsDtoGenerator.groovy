package org.codehaus.groovy.grails.plugins.dto

import org.codehaus.groovy.grails.commons.GrailsDomainClass

class DefaultGrailsDtoGenerator {
    private final String eol

    private processed

    DefaultGrailsDtoGenerator() {
        this(true)
    }

    DefaultGrailsDtoGenerator(boolean useNativeEol) {
        if (useNativeEol) eol = System.getProperty("line.separator")
        else eol = "\n"
    }

    void generate(GrailsDomainClass dc, File rootDir, boolean recursive) {
        processed = [] as Set
        generateInternal(dc, rootDir, recursive)
    }

    Set generateNoRecurse(GrailsDomainClass dc, Writer writer) {
        def dcPkg = dc.clazz.package.name

        // Deal with the persistent properties.
        def imports = []
        def fields = []
        def relations = [] as Set
        def processProperty = { prop ->
            def propType = prop.type
            def field = [ name: prop.name ]

            if (prop.referencedPropertyType == propType) {
                field["typeString"] = propType.simpleName + (prop.association ? "DTO" : "")
                addImportIfNecessary(imports, dcPkg, propType, prop.association)
            }
            else {
                field["typeString"] = propType.simpleName + '<' + prop.referencedPropertyType.simpleName + (prop.association ? "DTO" : "") + '>'
                addImportIfNecessary(imports, dcPkg, propType, false)
                addImportIfNecessary(imports, dcPkg, prop.referencedPropertyType, prop.association)
            }

            // Store the reference domain class if this property is
            // an association. This is so that we can return a set
            // of related domain classes.
            if (prop.association) relations << prop.referencedDomainClass

            fields << field
        }

        processProperty.call(dc.identifier)
        dc.persistentProperties.each(processProperty)

        // Start with the package line.
        writer.write "package ${dcPkg};${eol}${eol}"

        // Now add any required imports.
        if (imports) {
            imports.unique().sort().each { str ->
                writer.write "import ${str};${eol}"
            }
            writer.write eol
        }

        // Next, the class declaration.
        writer.write "public class ${dc.shortName}DTO {${eol}"

        // The private fields.
        fields.each { field ->
            writer.write "    private ${field.typeString} ${field.name};${eol}"
        }

        // The getters and setters.
        writer.write eol
        fields.each { field ->
            def propSuffix = new StringBuilder(field.name)
            propSuffix.setCharAt(0, Character.toUpperCase(propSuffix.charAt(0)))
            propSuffix = propSuffix.toString()

            writer.write "    public ${field.typeString} get${propSuffix}() { return ${field.name}; }${eol}"
            writer.write "    public void set${propSuffix}(${field.typeString} ${field.name}) { this.${field.name} = ${field.name}; }${eol}"
        }

        // Class terminator.
        writer.write "}${eol}"

        // All done. Make sure all data has been pushed to the destination
        // before we leave.
        writer.flush()

        return relations
    }

    private void generateInternal(GrailsDomainClass dc, File rootDir, boolean recursive) {
        def dtoFile = getDtoFile(rootDir, dc)
        dtoFile.parentFile.mkdirs()

        def writer = new BufferedWriter(new FileWriter(dtoFile))
        try {
            def relations = generateNoRecurse(dc, writer)
            processed << dc.clazz

            if (recursive && relations) {
                relations.each { rel ->
                    if (!processed.contains(rel.clazz)) {
                        generateInternal(rel, rootDir, true)
                    }
                }
            }
        }
        finally {
            if (writer) writer.close()
        }
    }

    protected void addImportIfNecessary(List imports, String hostPackage, Class clazz, boolean isAssociation) {
        def pkg = clazz.package?.name
        if (pkg && pkg != hostPackage && pkg != "java.lang") {
            imports << (isAssociation ? clazz.name + "DTO" : clazz.name)
        }
    }

    protected File getDtoFile(File rootDir, GrailsDomainClass dc) {
        def pkgPath = dc.clazz.package.name.replace(".", "/")
        return new File(rootDir, "${pkgPath}/${dc.shortName}DTO.java")
    }
}
