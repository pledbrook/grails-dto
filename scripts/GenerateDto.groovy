includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target(default: "Generates DTO classes for one or more domain classes.") {
    depends(parseArguments, bootstrap)

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

    def generator = grailsApp.mainContext.getBean("dtoGenerator")
    domainClasses.each { dc ->
        generator.generate(dc, srcDir, !argsMap["non-recursive"])
    }

    event("StatusFinal", [ "Successfully created the DTOs under src/java" ])
}
