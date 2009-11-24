import grails.test.AbstractCliTestCase
import org.apache.commons.codec.digest.DigestUtils

/**
 * Test case for the "generate-dto" Grails command provided by this
 * plugin.
 */
class GenerateTests extends AbstractCliTestCase {
    private static final String POST_MD5 = "0739eca700015a3d0e6ebb9e3a95e89e"
    private static final String CATEGORY_MD5 = "15155579a345fa87af83ee90e6ded95c"
    private static final String SEC_USER_MD5 = "9a90e08907b63059362cf8a54cd9bad3"
    private static final String SEC_ROLE_MD5 = "99ebf95dbe51b7695ea08ad9ae2fca9f"
    private static final String PAGE_MD5 = "6ba46a5f7a1e2ed3a3c41e9ac700fc1a"
    private static final String WIKI_CONTENT_MD5 = "0d6efe218d4a663911fa7425424760f5"
    private static final String ATTR_CONTENT_MD5 = "583e7a10a917df5c520cb33c3b0bb658"

    private final File srcDir = new File("src/java")
    private final File postDto = new File(srcDir, "org/ex/client/dto/PostDTO.java")
    private final File catDto = new File(srcDir, "org/ex/client/dto/CategoryDTO.java")
    private final File userDto = new File(srcDir, "org/ex/client/dto/security/SecUserDTO.java")
    private final File roleDto = new File(srcDir, "org/ex/client/dto/security/SecRoleDTO.java")
    private final File pageDto = new File(srcDir, "org/ex/client/dto/PageDTO.java")
    private final File wikiDto = new File(srcDir, "org/ex/client/dto/WikiContentDTO.java")
    private final File attrDto = new File(srcDir, "org/another/client/AttributeDTO.java")

    protected void setUp() {
        srcDir.deleteDir()
        srcDir.mkdirs()
    }

    /**
     * Tests that DTOs are created in the correct packages based on the
     * "dto.package.transforms" build setting.
     */
    void testGenerate() {
        execute([ "generate-dto", "--all" ])
        enterInput("y")

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")

        // Check that the expected DTO files exist.
        assertTrue "Post DTO does not exist.", postDto.exists()
        assertTrue "Category DTO does not exist.", catDto.exists()
        assertTrue "SecUser DTO does not exist.", userDto.exists()
        assertTrue "SecRole DTO does not exist.", roleDto.exists()
        assertTrue "Page DTO does not exist.", pageDto.exists()
        assertTrue "WikiContent DTO does not exist.", wikiDto.exists()
        assertTrue "Attribute DTO does not exist.", attrDto.exists()

        assertEquals "Unexpected MD5 digest for Post DTO.", POST_MD5, DigestUtils.md5Hex(postDto.text)
        assertEquals "Unexpected MD5 digest for Category DTO.", CATEGORY_MD5, DigestUtils.md5Hex(catDto.text)
        assertEquals "Unexpected MD5 digest for SecUser DTO.", SEC_USER_MD5, DigestUtils.md5Hex(userDto.text)
        assertEquals "Unexpected MD5 digest for SecRole DTO.", SEC_ROLE_MD5, DigestUtils.md5Hex(roleDto.text)
        assertEquals "Unexpected MD5 digest for Page DTO.", PAGE_MD5, DigestUtils.md5Hex(pageDto.text)
        assertEquals "Unexpected MD5 digest for WikiContent DTO.", WIKI_CONTENT_MD5, DigestUtils.md5Hex(wikiDto.text)
        assertEquals "Unexpected MD5 digest for Attribute DTO.", ATTR_CONTENT_MD5, DigestUtils.md5Hex(attrDto.text)
    }

    /**
     * Tests that the "dto.package.transforms" setting can be overridden
     * by command line arguments.
     */
    void testGenerateWithOverride() {
        execute([ "generate-dto", "--all", "--oldpkg=org.example", "--newpkg=net.nowhere.sub" ])
        enterInput("y")

        assertEquals 0, waitForProcess()
        verifyHeader()

        // Make sure that the script was found.
        assertFalse "GenerateDto script not found.", output.contains("Script not found:")

        // Check that the expected DTO files exist.
        def postDto = new File(srcDir, "net/nowhere/sub/PostDTO.java")
        def catDto = new File(srcDir, "net/nowhere/sub/CategoryDTO.java")
        def userDto = new File(srcDir, "net/nowhere/sub/security/SecUserDTO.java")
        def roleDto = new File(srcDir, "net/nowhere/sub/security/SecRoleDTO.java")
        def pageDto = new File(srcDir, "net/nowhere/sub/PageDTO.java")
        def wikiDto = new File(srcDir, "net/nowhere/sub/WikiContentDTO.java")

        assertTrue "Post DTO does not exist.", postDto.exists()
        assertTrue "Category DTO does not exist.", catDto.exists()
        assertTrue "SecUser DTO does not exist.", userDto.exists()
        assertTrue "SecRole DTO does not exist.", roleDto.exists()
        assertTrue "Page DTO does not exist.", pageDto.exists()
        assertTrue "WikiContent DTO does not exist.", wikiDto.exists()
        assertTrue "Attribute DTO does not exist.", attrDto.exists()

        assertTrue "Post DTO declares the wrong package.", postDto.text.startsWith("package net.nowhere.sub;")
        assertTrue "Category DTO declares the wrong package.", catDto.text.startsWith("package net.nowhere.sub;")
        assertTrue "SecUser DTO declares the wrong package.", userDto.text.startsWith("package net.nowhere.sub.security;")
        assertTrue "SecRole DTO declares the wrong package.", roleDto.text.startsWith("package net.nowhere.sub.security;")
        assertTrue "Page DTO declares the wrong package.", pageDto.text.startsWith("package net.nowhere.sub;")
        assertTrue "WikiContent DTO declares the wrong package.", wikiDto.text.startsWith("package net.nowhere.sub;")
        assertEquals "Unexpected MD5 digest for Attribute DTO.", ATTR_CONTENT_MD5, DigestUtils.md5Hex(attrDto.text)
    }
}
