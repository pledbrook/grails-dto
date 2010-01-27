package org.codehaus.groovy.grails.plugins.dto

import org.codehaus.groovy.grails.commons.GrailsDomainClass

/**
 * Default implementation of the DTO generator. It inspects the domain
 * class descriptors and uses that information to write out the DTO
 * files directly. You can specify whether the generated files have
 * native line endings or simply '\n'.
 *
 * @author Peter Ledbrook
 */
class DefaultGrailsDtoGenerator {
    private final String eol
    private final String indent

    private processed

    Map packageTransforms = [:]

    /**
     * Creates a generator.
     * @param useNativeEol The generator creates files with native line
     * endings if this is <code>true</code>, otherwise it uses '\n'.
     * Default value is <code>true</code>.
     * @param indent The string to use for indenting. Defaults to 4
     * spaces.
     */
    DefaultGrailsDtoGenerator(boolean useNativeEol = true, String indent = "    ") {
        if (useNativeEol) eol = System.getProperty("line.separator")
        else eol = "\n"

        this.indent = indent
    }

    /**
     * Generates the DTO files for the given domain class.
     * @param dc The domain class to generate a DTO for.
     * @param rootDir The root directory where the DTO files will be
     * generated, e.g. "src/java". Package directories will be created
     * within this directory as needed.
     * @param recursive If <code>true</code>, the method will generate
     * DTOs for all related domain classes too. Otherwise only the one
     * DTO for 'dc' will be created.
     */
    void generate(GrailsDomainClass dc, File rootDir, boolean recursive) {
        processed = [] as Set
        generateInternal(dc, rootDir, recursive)
    }

    /**
     * Generates the DTO file for the given domain class, without also
     * generating DTOs for related domain classes. Rather than write
     * the DTO to a file, this method will write it out to the given
     * Writer.
     * @param dc The domain class to generate a DTO for.
     * @param writer The writer to use when generating the DTO class.
     */
    Set generateNoRecurse(GrailsDomainClass dc, Writer writer) {
        final dcPkg = dc.clazz.package?.name
        return generateNoRecurseInternal(dc, writer, getTargetPackage(dcPkg))
    }

    private void generateInternal(GrailsDomainClass dc, File rootDir, boolean recursive) {
        final targetPkg = getTargetPackage(dc.clazz.package?.name)
        def dtoFile = getDtoFile(rootDir, dc, targetPkg)
        dtoFile.parentFile.mkdirs()

        def writer = new BufferedWriter(new FileWriter(dtoFile))
        try {
            def relations = generateNoRecurseInternal(dc, writer, targetPkg)
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

    private Set generateNoRecurseInternal(GrailsDomainClass dc, Writer writer, String targetPkg) {
        // Deal with the persistent properties.
        def imports = []
        def fields = []
        def relations = [] as Set
        def processProperty = { prop ->
            def propType = prop.type
            def field = [ name: prop.name ]

            if (prop.referencedPropertyType == propType) {
                field["typeString"] = propType.simpleName + (prop.association ? "DTO" : "")
                addImportIfNecessary(imports, targetPkg, propType, prop.association)
            }
            else {
                field["typeString"] = propType.simpleName + '<' + prop.referencedPropertyType.simpleName + (prop.association ? "DTO" : "") + '>'
                addImportIfNecessary(imports, targetPkg, propType, false)
                addImportIfNecessary(imports, targetPkg, prop.referencedPropertyType, prop.association)
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
        if (targetPkg) {
            writer.write "package ${targetPkg};${eol}${eol}"
        }

        // Now add any required imports.
        if (imports) {
            imports.unique().sort().each { str ->
                writer.write "import ${str};${eol}"
            }
            writer.write eol
        }

        // Next, the class declaration.
        writer.write "public class ${dc.shortName}DTO implements grails.plugins.dto.DTO {${eol}"
        
        // A serialUID, since DTOs are serialisable.
        writer.write "${indent}private static final long serialVersionUID = 1L;${eol}${eol}"

        // The private fields.
        fields.each { field ->
            writer.write "${indent}private ${field.typeString} ${field.name};${eol}"
        }

        // The getters and setters.
        writer.write eol
        fields.each { field ->
            def propSuffix = new StringBuilder(field.name)
            propSuffix.setCharAt(0, Character.toUpperCase(propSuffix.charAt(0)))
            propSuffix = propSuffix.toString()

            writer.write "${indent}public ${field.typeString} get${propSuffix}() { return ${field.name}; }${eol}"
            writer.write "${indent}public void set${propSuffix}(${field.typeString} ${field.name}) { this.${field.name} = ${field.name}; }${eol}"
        }

        // toString()
        writer.write eol
        writer.write "${indent}public String toString() {${eol}"
        writer.write "${indent * 2}StringBuilder sb = new StringBuilder();${eol}"
        writer.write "${indent * 2}sb.append(\"${dc.shortName}DTO[\");${eol}"
        fields.each { field ->
            writer.write "${indent * 2}sb.append(\"\\n\\t${field.name}: \" + this.${field.name});${eol}"
        }
        writer.write "${indent * 2}sb.append(\"]\");${eol}"
        writer.write "${indent * 2}return sb.toString();${eol}"
        writer.write "${indent}}${eol}"

        // Class terminator.
        writer.write "}${eol}"

        // All done. Make sure all data has been pushed to the destination
        // before we leave.
        writer.flush()

        return relations
    }

    protected void addImportIfNecessary(List imports, String hostPackage, Class clazz, boolean isAssociation) {
        def pkg = isAssociation ? getTargetPackage(clazz.package?.name) : clazz.package?.name
        if (pkg && pkg != hostPackage && pkg != "java.lang") {
            imports << "${pkg}.${clazz.simpleName}${isAssociation ? 'DTO' : ''}"
        }
    }

    protected File getDtoFile(File rootDir, GrailsDomainClass dc, String targetPkg) {
        def pkgPath = ""
        if (targetPkg) pkgPath = targetPkg.replace(".", "/") + '/'
        return new File(rootDir, "${pkgPath}${dc.shortName}DTO.java")
    }

    protected String getTargetPackage(final String dcPkg) {
        def targetPkg = dcPkg ?: ""
        if (packageTransforms) {
            // Find a transform that matches the domain class package.
            // If the default package is in the transforms map (i.e.
            // the empty string is a key), the domain class package
            // must be an exact match. Otherwise, sub-packages match.
            def entry = packageTransforms.find { key, val -> key ? targetPkg?.startsWith(key) : targetPkg == key }
            if (entry) {
                // Found one, so use the associated package name as the
                // target package.
                targetPkg = targetPkg.replace(entry.key, entry.value)
            }
            else if (packageTransforms["*"]) {
                // Didn't find a matching transform, but did find the
                // wildcard one.
                targetPkg = packageTransforms["*"]
            }
        }

        return targetPkg
    }
}
