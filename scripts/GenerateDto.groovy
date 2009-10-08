includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsCreateArtifacts")

USAGE = """
    generate-dto [--all] [--non-recursive] [--pkg=PKG]
                 [--oldpkg=OLDPKG --newpkg=NEWPKG] [DOMAINCLASS]
                
where
    PKG         = The target package to put the DTO classes in. By default,
                  each DTO class goes in the same package as its domain class.
    OLDPKG      = The part of the domain class package to substitute.
    NEWPKG      = The package name to substitute OLDPKG with.
    DOMAINCLASS = The fully qualified name of the domain class to generate
                  a DTO for. Ignored if --all is used.
"""

target(default: "Generates DTO classes for one or more domain classes.") {
    depends(parseArguments, bootstrap)
    
    // Check that the "dto.package.transforms" setting is the correct type.
    if (!buildConfig.dto.package.transform instanceof Map) {
        println "The 'dto.package.transform' build setting must be a map."
        exit(1)
    }

    // Check the arguments.
    def replacePackage = "*"
    def targetPackage = null
    if (argsMap["pkg"]) {
        // --pkg cannot be used with either --oldpkg or --newpkg.
        if (argsMap["oldpkg"] || argsMap["newpkg"]) {
            println "You cannot use --pkg with either --oldpkg or --newpkg"
            exit(1)
        }

        // Use the specified package.
        targetPackage = argsMap["pkg"]
    }
    else if (argsMap["oldpkg"] || argsMap["newPkg"]) {
        if (!(argsMap["oldpkg"] && argsMap["newpkg"])) {
            println "You must specify both --oldpkg and --newpkg, not just one of them"
            exit(1)
        }

        // Use the specified packages.
        replacePackage = argsMap["oldpkg"]
        targetPackage = argsMap["newpkg"]
    }

    def domainClasses
    if (argsMap["all"]) {
        def response = confirmInput("Are you sure you want to generate DTOs for all domain classes?")
        if (!response) {
            return 0
        }
        else {
            domainClasses = grailsApp.domainClasses
        }
    }
    else {
        promptForName(type: "Domain class")

        def failed = false
        def params = argsMap["params"]
        domainClasses = params.collect {
            def dc = grailsApp.getDomainClass(it)
            if (!dc) {
                failed = true;
                event("StatusError", [ "Cannot find domain class $it" ])
            }
            return dc
        }

        if (failed) return 1
    }

    def srcDir = new File("src/java")
    if (!srcDir.exists()) srcDir.mkdirs()

    // The generator is a Spring bean, so we can get it from the
    // application context.
    def generator = grailsApp.mainContext.getBean("dtoGenerator")

    // If we have a target package, initialise the generator's package
    // transformations.
    if (targetPackage) {
        generator.packageTransforms = [ (replacePackage): targetPackage ]
    }
    else if (buildConfig.dto.package.transforms) {
        generator.packageTransforms = buildConfig.dto.package.transforms
    }

    // Use the generator on each of the specified domain classes.
    domainClasses.each { dc ->
        generator.generate(dc, srcDir, !argsMap["non-recursive"])
    }

    event("StatusFinal", [ "Successfully created the DTOs under src/java" ])
}
