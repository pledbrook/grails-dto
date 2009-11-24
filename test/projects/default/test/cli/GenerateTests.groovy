import grails.test.AbstractCliTestCase
import org.apache.commons.codec.digest.DigestUtils

/**
 * Test case for the "generate-dto" Grails command provided by this
 * plugin.
 */
class GenerateTests extends AbstractCliTestCase {
    private static final String POST_MD5 = "931d875066934cad720fca33f631dd42"
    private static final String CATEGORY_MD5 = "16aadf011280684ded3fe149c6d27198"
    private static final String SEC_USER_MD5 = "2935cb56ed65369b3b6a7492ab283827"
    private static final String SEC_ROLE_MD5 = "995906e9fd730184fa9b56a78c82581a"
    private static final String PAGE_MD5 = "7e769d51e9e30b132b67f9b656c06a21"
    private static final String WIKI_CONTENT_MD5 = "589355d65e02f1382d27a72b08cb72d1"

    private final File srcDir = new File("src/java")
    private final File postDto = new File(srcDir, "org/example/PostDTO.java")
    private final File catDto = new File(srcDir, "org/example/CategoryDTO.java")
    private final File userDto = new File(srcDir, "org/example/security/SecUserDTO.java")
    private final File roleDto = new File(srcDir, "org/example/security/SecRoleDTO.java")
    private final File pageDto = new File(srcDir, "org/example/PageDTO.java")
    private final File wikiDto = new File(srcDir, "org/example/WikiContentDTO.java")

    protected void setUp() {
        srcDir.deleteDir()
        srcDir.mkdirs()
    }

    /**
     * Tests that, given a domain class name (with package) as an argument,
     * the command generates the DTO for that domain class plus the DTOs
     * for all related domain classes. In other words, it tests the standard
     * recursive mode.
     */
    void testGenerate() {
        workDir = new File(".")
        execute([ "generate-dto", "org.example.Post" ])

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")

        // Check that the expected DTO files exist.
        assertTrue "Post DTO does not exist.", postDto.exists()
        assertTrue "Category DTO does not exist.", catDto.exists()
        assertTrue "SecUser DTO does not exist.", userDto.exists()
        assertTrue "SecRole DTO does not exist.", roleDto.exists()
        assertFalse "Page DTO exists, but it shouldn't.", pageDto.exists()
        assertFalse "WikiContent DTO exists, but it shouldn't.", wikiDto.exists()

        assertEquals "Unexpected MD5 digest for Post DTO.", POST_MD5, DigestUtils.md5Hex(postDto.text)
        assertEquals "Unexpected MD5 digest for Category DTO.", CATEGORY_MD5, DigestUtils.md5Hex(catDto.text)
        assertEquals "Unexpected MD5 digest for SecUser DTO.", SEC_USER_MD5, DigestUtils.md5Hex(userDto.text)
        assertEquals "Unexpected MD5 digest for SecRole DTO.", SEC_ROLE_MD5, DigestUtils.md5Hex(roleDto.text)
    }

    /**
     * Tests that the appropriate DTOs are generated for the Page domain
     * class and its relations. DTOs should not be generated for Post
     * and all its related classes.
     */
    void testGenerateWithPage() {
        workDir = new File(".")
        execute([ "generate-dto", "org.example.Page" ])

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")

        // Check that the expected DTO files exist.
        assertFalse "Post DTO exists, but it shouldn't.", postDto.exists()
        assertFalse "Category DTO exists, but it shouldn't.", catDto.exists()
        assertFalse "SecUser DTO exists, but it shouldn't.", userDto.exists()
        assertFalse "SecRole DTO exists, but it shouldn't.", roleDto.exists()
        assertTrue "Page DTO does not exist.", pageDto.exists()
        assertTrue "WikiContent DTO does not exist.", wikiDto.exists()

        assertEquals "Unexpected MD5 digest for Page DTO.", PAGE_MD5, DigestUtils.md5Hex(pageDto.text)
        assertEquals "Unexpected MD5 digest for WikiContent DTO.", WIKI_CONTENT_MD5, DigestUtils.md5Hex(wikiDto.text)
    }

    /**
     * Tests that a domain class with no explicitly declared relations
     * results in just a single DTO being generated.
     */
    void testGenerateWithNoReferencedDomainClasses() {
        workDir = new File(".")
        execute([ "generate-dto", "org.example.WikiContent" ])

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")

        // Check that the expected DTO files exist.
        assertFalse "Post DTO exists, but it shouldn't.", postDto.exists()
        assertFalse "Category DTO exists, but it shouldn't.", catDto.exists()
        assertFalse "SecUser DTO exists, but it shouldn't.", userDto.exists()
        assertFalse "SecRole DTO exists, but it shouldn't.", roleDto.exists()
        assertFalse "Page DTO exists, but it shouldn't.", pageDto.exists()
        assertTrue "WikiContent DTO does not exist.", wikiDto.exists()

        assertEquals "Unexpected MD5 digest for WikiContent DTO.", WIKI_CONTENT_MD5, DigestUtils.md5Hex(wikiDto.text)
    }

    /**
     * Tests that the "--all" argument results in DTOs being generated
     * for all domain classes.
     */
    void testGenerateAll() {
        workDir = new File(".")
        execute([ "generate-dto", "--all" ])
        enterInput("y")

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")

        // Other command output checks.
        assertTrue "Confirm all message not shown.", output.contains("Are you sure you want to generate DTOs for all domain classes?")
        assertTrue "Success message not shown.", output.contains("Successfully created the DTOs under src/java")

        // Check that the expected DTO files exist.
        assertTrue "Post DTO does not exist.", postDto.exists()
        assertTrue "Category DTO does not exist.", catDto.exists()
        assertTrue "SecUser DTO does not exist.", userDto.exists()
        assertTrue "SecRole DTO does not exist.", roleDto.exists()
        assertTrue "Page DTO does not exist.", pageDto.exists()
        assertTrue "WikiContent DTO does not exist.", wikiDto.exists()

        assertEquals "Unexpected MD5 digest for Post DTO.", POST_MD5, DigestUtils.md5Hex(postDto.text)
        assertEquals "Unexpected MD5 digest for Category DTO.", CATEGORY_MD5, DigestUtils.md5Hex(catDto.text)
        assertEquals "Unexpected MD5 digest for SecUser DTO.", SEC_USER_MD5, DigestUtils.md5Hex(userDto.text)
        assertEquals "Unexpected MD5 digest for SecRole DTO.", SEC_ROLE_MD5, DigestUtils.md5Hex(roleDto.text)
        assertEquals "Unexpected MD5 digest for Page DTO.", PAGE_MD5, DigestUtils.md5Hex(pageDto.text)
        assertEquals "Unexpected MD5 digest for WikiContent DTO.", WIKI_CONTENT_MD5, DigestUtils.md5Hex(wikiDto.text)
    }

    /**
     * Tests that no DTOs are generated if the "--all" argument is used,
     * but the user enters a response on "n" when asked to confirm that
     * he or she wants DTOs for all domain classes.
     */
    void testGenerateAllCancelled() {
        workDir = new File(".")
        execute([ "generate-dto", "--all" ])
        enterInput("n")

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")

        // Other command output checks.
        assertTrue "Confirm all message not shown.", output.contains("Are you sure you want to generate DTOs for all domain classes?")
        assertFalse "Success message shown when it shouldn't have been.", output.contains("Successfully created the DTOs under src/java")

        // Check that no DTOs are created.
        assertFalse "Post DTO exists, but it shouldn't.", postDto.exists()
        assertFalse "Category DTO exists, but it shouldn't.", catDto.exists()
        assertFalse "SecUser DTO exists, but it shouldn't.", userDto.exists()
        assertFalse "SecRole DTO exists, but it shouldn't.", roleDto.exists()
        assertFalse "Page DTO exists, but it shouldn't.", pageDto.exists()
        assertFalse "WikiContent DTO exists, but it shouldn't.", wikiDto.exists()
    }

    /**
     * Tests that the "--non-recursive" argument causes the command to
     * only generate DTOs for the given domain class(es). In other words,
     * DTOs should not be generated for the relations.
     */
    void testGenerateNonRecursive() {
        workDir = new File(".")
        execute([ "generate-dto", "--non-recursive", "org.example.Post" ])

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")

        // Check that the expected DTO files exist.
        assertTrue "Post DTO does not exist.", postDto.exists()
        assertFalse "Category DTO exists, but it shouldn't.", catDto.exists()
        assertFalse "SecUser DTO exists, but it shouldn't.", userDto.exists()
        assertFalse "SecRole DTO exists, but it shouldn't.", roleDto.exists()
        assertFalse "Page DTO exists, but it shouldn't.", pageDto.exists()
        assertFalse "WikiContent DTO exists, but it shouldn't.", wikiDto.exists()

        assertEquals "Unexpected MD5 digest for Post DTO.", POST_MD5, DigestUtils.md5Hex(postDto.text)
    }

    /**
     * Tests that the user can pass multiple domain classes as arguments
     * to the command.
     */
    void testGenerateWithMultipleArgs() {
        workDir = new File(".")
        execute([ "generate-dto", "--non-recursive", "org.example.Post", "org.example.Page" ])

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")

        // Check that the expected DTO files exist.
        assertTrue "Post DTO does not exist.", postDto.exists()
        assertFalse "Category DTO exists, but it shouldn't.", catDto.exists()
        assertFalse "SecUser DTO exists, but it shouldn't.", userDto.exists()
        assertFalse "SecRole DTO exists, but it shouldn't.", roleDto.exists()
        assertTrue "Page DTO does not exist.", pageDto.exists()
        assertFalse "WikiContent DTO exists, but it shouldn't.", wikiDto.exists()

        assertEquals "Unexpected MD5 digest for Post DTO.", POST_MD5, DigestUtils.md5Hex(postDto.text)
        assertEquals "Unexpected MD5 digest for Page DTO.", PAGE_MD5, DigestUtils.md5Hex(pageDto.text)
    }

    /**
     * Tests that the command exits with the appropriate error message
     * and exit code when the given domain class can not be found.
     */
    void testGenerateDomainClassNotFound() {
        workDir = new File(".")
        execute([ "generate-dto", "Post" ])

        assertEquals 1, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")
        assertTrue "Error message missing for domain class not found.", output.contains("Cannot find domain class Post")

        // Check that the expected DTO files exist.
        assertFalse "Post DTO exists, but it shouldn't.", postDto.exists()
        assertFalse "Category DTO exists, but it shouldn't.", catDto.exists()
        assertFalse "SecUser DTO exists, but it shouldn't.", userDto.exists()
        assertFalse "SecRole DTO exists, but it shouldn't.", roleDto.exists()
        assertFalse "Page DTO exists, but it shouldn't.", pageDto.exists()
        assertFalse "WikiContent DTO exists, but it shouldn't.", wikiDto.exists()
    }

    /**
     * Tests that the DTOs are all created in the package provided as
     * a command argument.
     */
    void testGenerateWithExplicitPackage() {
        execute([ "generate-dto", "--all", "--pkg=org.another.gwt.client" ])
        enterInput("y")

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")

        // Check that the expected DTO files exist.
        def postDto = new File(srcDir, "org/another/gwt/client/PostDTO.java")
        def catDto = new File(srcDir, "org/another/gwt/client/CategoryDTO.java")
        def userDto = new File(srcDir, "org/another/gwt/client/SecUserDTO.java")
        def roleDto = new File(srcDir, "org/another/gwt/client/SecRoleDTO.java")
        def pageDto = new File(srcDir, "org/another/gwt/client/PageDTO.java")
        def wikiDto = new File(srcDir, "org/another/gwt/client/WikiContentDTO.java")

        assertTrue "Post DTO does not exist.", postDto.exists()
        assertTrue "Category DTO does not exist.", catDto.exists()
        assertTrue "SecUser DTO does not exist.", userDto.exists()
        assertTrue "SecRole DTO does not exist.", roleDto.exists()
        assertTrue "Page DTO does not exist.", pageDto.exists()
        assertTrue "WikiContent DTO does not exist.", wikiDto.exists()

        assertTrue "Post DTO declares the wrong package.", postDto.text.startsWith("package org.another.gwt.client;")
        assertTrue "Category DTO declares the wrong package.", catDto.text.startsWith("package org.another.gwt.client;")
        assertTrue "SecUser DTO declares the wrong package.", userDto.text.startsWith("package org.another.gwt.client;")
        assertTrue "SecRole DTO declares the wrong package.", roleDto.text.startsWith("package org.another.gwt.client;")
        assertTrue "Page DTO declares the wrong package.", pageDto.text.startsWith("package org.another.gwt.client;")
        assertTrue "WikiContent DTO declares the wrong package.", wikiDto.text.startsWith("package org.another.gwt.client;")
    }

    /**
     * Tests that package replacement works correctly. Any part of a
     * domain class's package that matches the specified old package
     * should be replaced by the new package. Any parts of the package
     * that don't match are retained appended to the new package.
     */
    void testGenerateWithExplicitPackageMapping() {
        execute([ "generate-dto", "--all", "--oldpkg=org.example", "--newpkg=org.another.gwt.client" ])
        enterInput("y")

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")

        // Check that the expected DTO files exist.
        def postDto = new File(srcDir, "org/another/gwt/client/PostDTO.java")
        def catDto = new File(srcDir, "org/another/gwt/client/CategoryDTO.java")
        def userDto = new File(srcDir, "org/another/gwt/client/security/SecUserDTO.java")
        def roleDto = new File(srcDir, "org/another/gwt/client/security/SecRoleDTO.java")
        def pageDto = new File(srcDir, "org/another/gwt/client/PageDTO.java")
        def wikiDto = new File(srcDir, "org/another/gwt/client/WikiContentDTO.java")

        assertTrue "Post DTO does not exist.", postDto.exists()
        assertTrue "Category DTO does not exist.", catDto.exists()
        assertTrue "SecUser DTO does not exist.", userDto.exists()
        assertTrue "SecRole DTO does not exist.", roleDto.exists()
        assertTrue "Page DTO does not exist.", pageDto.exists()
        assertTrue "WikiContent DTO does not exist.", wikiDto.exists()

        assertTrue "Post DTO declares the wrong package.", postDto.text.startsWith("package org.another.gwt.client;")
        assertTrue "Category DTO declares the wrong package.", catDto.text.startsWith("package org.another.gwt.client;")
        assertTrue "SecUser DTO declares the wrong package.", userDto.text.startsWith("package org.another.gwt.client.security;")
        assertTrue "SecRole DTO declares the wrong package.", roleDto.text.startsWith("package org.another.gwt.client.security;")
        assertTrue "Page DTO declares the wrong package.", pageDto.text.startsWith("package org.another.gwt.client;")
        assertTrue "WikiContent DTO declares the wrong package.", wikiDto.text.startsWith("package org.another.gwt.client;")
    }

    /**
     * Tests that the generate-dto command prints an error message if
     * <tt>--pkg</tt> is used with <tt>--oldpkg</tt>. 
     */
    void testGenerateWithConflictingPkgArgsOld() {
        execute([ "generate-dto", "--all", "--oldpkg=org.example", "--pkg=org.another.gwt.client" ])

        assertEquals 1, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")
        assertTrue "Conflicting args error message missing.", output.contains("You cannot use --pkg with either --oldpkg or --newpkg")
    }

    /**
     * Tests that the generate-dto command prints an error message if
     * <tt>--pkg</tt> is used with either <tt>--newpkg</tt>.
     */
    void testGenerateWithConflictingPkgArgsNew() {
        execute([ "generate-dto", "--all", "--pkg=org.example", "--newpkg=org.another.gwt.client" ])

        assertEquals 1, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")
        assertTrue "Conflicting args error message missing.", output.contains("You cannot use --pkg with either --oldpkg or --newpkg")
    }

    /**
     * Tests that the generate-dto command prints an error message if
     * either <tt>--oldpkg</tt> or <tt>--newpkg</tt> are specified
     * without the other.
     */
    void testGenerateWithMissingPkgArg() {
        execute([ "generate-dto", "--all", "--oldpkg=org.example" ])

        assertEquals 1, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")
        assertTrue "Missing --newpkg arg error message.", output.contains("You must specify both --oldpkg and --newpkg, not just one of them")
    }
}
