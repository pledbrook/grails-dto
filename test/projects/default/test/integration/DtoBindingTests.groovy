import grails.converters.XML
import grails.plugins.dto.DTO
import grails.test.*

import org.example.*
import org.example.security.*

/**
 * Test case for the dynamic "as DTO" and "toDTO()" methods provided
 * by the DTO plugin.
 */
class DtoBindingTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()

        // The following tests need some domain instances to work with.
        def userRole = new SecRole(name: "User")

        def adminUser = new SecUser(
                fullName: "System Administrator",
                email: "admin@nowhere.org",
                dateOfBirth: new Date())
        adminUser.id = "admin"
        adminUser.addToRoles(name: "Administrator")
        adminUser.addToRoles(userRole)
        adminUser.save()

        def dilbert = new SecUser(
                fullName: "Dilbert from the cartoon",
                email: "dilbert@dilbert.com",
                dateOfBirth: new Date())
        dilbert.id = "dilbert"
        dilbert.addToRoles(userRole)

        def firstPost = new Post(content: "First post!", priority: 1, type: PostType.PG)
        def secondPost = new Post(content: "Second post!", priority: 10, type: PostType.X)
        def thirdPost = new Post(content: "Third post!", priority: 5, type: PostType.R)
        def fourthPost = new Post(content: "Fourth post!", priority: 5, type: PostType.PG)
        def fifthPost = new Post(content: "Fifth post!", priority: 8, type: PostType.R)
        dilbert.addToPosts(firstPost)
        dilbert.addToPosts(secondPost)
        dilbert.addToPosts(thirdPost)
        dilbert.addToPosts(fourthPost)
        dilbert.addToPosts(fifthPost)
        dilbert.save()

        def stuffCat = new Category(name: "Stuff")
        stuffCat.addToPosts(firstPost)
        stuffCat.addToPosts(secondPost)
        stuffCat.addToPosts(fourthPost)
        stuffCat.save()

        def otherCat = new Category(name: "Other")
        otherCat.addToPosts(secondPost)
        otherCat.addToPosts(fifthPost)
        otherCat.save(flush: true)
    }

    protected void tearDown() {
        super.tearDown()
    }

    /**
     * Tests that "as DTO" works on domain instances.
     */
    void testAsDTO() {
        // First get hold of a user domain instance.
        def dilbert = SecUser.findById("dilbert")
        assertNotNull dilbert

        // Next, convert it to a DTO and check that the values in the
        // tree of DTOs match those in the corresponding domain instances.
        println ">> Dilbert XML: ${dilbert as XML}"
        compareUserDomainAndDTO dilbert, dilbert as DTO
    }

    /**
     * Tests that the toDTO() method works on domain instances.
     */
    void testToDTO() {
        // First get hold of a user domain instance.
        def dilbert = SecUser.findById("dilbert")
        assertNotNull dilbert

        // Next, convert it to a DTO and check that the values in the
        // tree of DTOs match those in the corresponding domain instances.
        compareUserDomainAndDTO dilbert, dilbert.toDTO()
    }

    /**
     * Compares a given SecUser DTO with the given SecUser domain
     * instance.
     */
    private void compareUserDomainAndDTO(domainObj, dtoObj) {
        assertEquals domainObj.id, dtoObj.id
        assertEquals domainObj.fullName, dtoObj.fullName
        assertEquals domainObj.email, dtoObj.email
        assertEquals domainObj.dateOfBirth, dtoObj.dateOfBirth

        // The user fields are all OK, so now recurse into the relations.
        assertEquals domainObj.posts.size(), dtoObj.posts.size()
        for (int i in 0..<domainObj.posts.size()) {
            def dcPost = domainObj.posts[i]
            def dtoPost = dtoObj.posts[i]
            assertEquals dcPost.id, dtoPost.id
            assertEquals dcPost.content, dtoPost.content
            assertEquals dcPost.priority, dtoPost.priority
            assertEquals dcPost.type, dtoPost.type
            
            if (dcPost.category) {
                assertEquals dcPost.category.id, dtoPost.category.id
                assertEquals dcPost.category.name, dtoPost.category.name
                assertEquals dcPost.category.posts.size(), dtoPost.category.posts.size()
            }
            assertEquals dcPost.user.id, dtoPost.user.id
        }

        assertEquals domainObj.roles.size(), dtoObj.roles.size()
        domainObj.roles.each { final dcRole ->
            // Check that the user DTO has a role DTO matching this role.
            assertTrue "No role DTO found matching the domain instance.", dtoObj.roles.any {
                it.id == dcRole.id && it.name == dcRole.name && it.users.size() == dcRole.users.size()
            }
        }
    }
}
