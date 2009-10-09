import grails.converters.XML
import grails.plugins.dto.DTO
import grails.test.*

import org.example.*
import org.example.security.*
import org.ex.client.dto.security.SecUserDTO

/**
 * Test case for the dynamic "toDTO()" method that takes an argument.
 * This is for cases where the DTO package does not match that of the
 * corresponding domain class.
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
     * Tests that toDTO() works when given a DTO class as an argument.
     */
    void testUserDomain() {
        // First get hold of a user domain instance.
        def dilbert = SecUser.findById("dilbert")
        assertNotNull dilbert

        // Next, convert it to a DTO and check that the values in the
        // tree of DTOs match those in the corresponding domain instances.
        compareUserDomainAndDTO dilbert, dilbert.toDTO(SecUserDTO)
    }

    /**
     * Tests that toDTO(Class) works on collections.
     */
    void testCollection() {
        def users = SecUser.list()
        assertEquals 2, users.size()

        // Convert the list to a list of DTOs.
        def dtos = users.toDTO(SecUserDTO)
        assertEquals 2, dtos.size()
        assertNotNull "First DTO element is null", dtos[0]
        assertNotNull "Second DTO element is null", dtos[1]

        compareUserDomainAndDTO users[0], dtos[0]
        compareUserDomainAndDTO users[1], dtos[1]
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
        def numPosts = 0
        if (domainObj.posts == null) {
            assertNull "DTO user posts should be null.", dtoObj.posts
        }
        else {
            numPosts = domainObj.posts.size()
            assertEquals numPosts, dtoObj.posts.size()
        }

        for (int i in 0..<numPosts) {
            def dcPost = domainObj.posts[i]
            def dtoPost = dtoObj.posts[i]
            assertEquals dcPost.id, dtoPost.id
            assertEquals dcPost.content, dtoPost.content
            assertEquals dcPost.priority, dtoPost.priority
            assertEquals dcPost.type, dtoPost.type
            
            if (dcPost.category) {
                assertEquals dcPost.category.id, dtoPost.category.id
                assertEquals dcPost.category.name, dtoPost.category.name
                assertEquals dcPost.category.posts?.size(), dtoPost.category.posts?.size()
            }
            assertEquals dcPost.user.id, dtoPost.user.id
        }

        assertEquals domainObj.roles?.size(), dtoObj.roles?.size()
        domainObj.roles.each { final dcRole ->
            // Check that the user DTO has a role DTO matching this role.
            assertTrue "No role DTO found matching the domain instance.", dtoObj.roles.any {
                it.id == dcRole.id && it.name == dcRole.name && it.users.size() == dcRole.users.size()
            }
        }
    }
}
