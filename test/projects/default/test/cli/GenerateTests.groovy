import grails.test.AbstractCliTestCase
import org.apache.commons.codec.digest.DigestUtils

/**
 * Test case for the "generate-dto" Grails command provided by this
 * plugin.
 */
class GenerateTests extends AbstractCliTestCase {
    private static final String POST_MD5 = "edd8fd3c40f42e1a7463cfc812584c87"
    private static final String CATEGORY_MD5 = "28a12f0908a2398efd04df09298cd1f8"
    private static final String SEC_USER_MD5 = "73fc91004df40a9be302a2ef3e90e409"
    private static final String SEC_ROLE_MD5 = "7cb87633846bec32937c6e0e6b4eb7d9"
    private static final String PAGE_MD5 = "d8811b63ca4b060a6ad1f7bec392cfaf"
    private static final String WIKI_CONTENT_MD5 = "7eb08cfb3c2e200460b91e8c97bdb133"

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
}
