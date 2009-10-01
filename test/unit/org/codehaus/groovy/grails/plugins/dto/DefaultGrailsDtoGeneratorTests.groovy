package org.codehaus.groovy.grails.plugins.dto

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.gmock.WithGMock

@WithGMock
class DefaultGrailsDtoGeneratorTests extends GroovyTestCase {
    void testGenerate() {
        def outDir = new File("tmp/src/java")
        outDir.deleteDir()

        // Mock the domain class descriptors.
        def mockRootClass = mock(GrailsDomainClass)
        def mockOtherDomain = mock(GrailsDomainClass)
        def mockHasMany = mock(GrailsDomainClass)

        mockRootClass.clazz.returns(org.example.MyDomain).stub()
        mockRootClass.shortName.returns("MyDomain").stub()
        mockRootClass.identifier.returns([ name: "id", association: false, type: long, referencedPropertyType: long ]).stub()
        mockRootClass.persistentProperties.returns([
            [ name: "name", association: false, type: String, referencedPropertyType: String ],
            [ name: "age", association: false, type: int, referencedPropertyType: int ],
            [ name: "theOther",
              association: true,
              type: org.example.OtherDomain,
              referencedPropertyType: org.example.OtherDomain,
              referencedDomainClass: mockOtherDomain ],
            [ name: "items",
              association: true,
              type: List,
              referencedPropertyType: org.example.sub.HasMany,
              referencedDomainClass: mockHasMany ]
        ]).stub()
        
        mockOtherDomain.clazz.returns(org.example.OtherDomain).stub()
        mockOtherDomain.shortName.returns("OtherDomain").stub()
        mockOtherDomain.identifier.returns([ name: "id", association: false, type: Long, referencedPropertyType: Long ]).stub()
        mockOtherDomain.persistentProperties.returns([
            [ name: "type", association: false, type: org.example.SomeType, referencedPropertyType: org.example.SomeType ],
            [ name: "owner",
              association: true,
              type: org.example.MyDomain,
              referencedPropertyType: org.example.MyDomain,
              referencedDomainClass: mockOtherDomain ]
        ]).stub()
        
        mockHasMany.clazz.returns(org.example.sub.HasMany).stub()
        mockHasMany.shortName.returns("HasMany").stub()
        mockHasMany.identifier.returns([ name: "uniqueName", association: false, type: String, referencedPropertyType: String ]).stub()
        mockHasMany.persistentProperties.returns([
            [ name: "number", association: false, type: Integer, referencedPropertyType: Integer ],
            [ name: "amount", association: false, type: BigDecimal, referencedPropertyType: BigDecimal ],
            [ name: "stuff", association: false, type: String, referencedPropertyType: String ]
        ]).stub()

        play {
            def generator = new DefaultGrailsDtoGenerator(false)
            generator.generate(mockRootClass, outDir, true)

            def myDomainDtoFile = new File(outDir, "org/example/MyDomainDTO.java")
            def otherDomainDtoFile = new File(outDir, "org/example/OtherDomainDTO.java")
            def hasManyDtoFile = new File(outDir, "org/example/sub/HasManyDTO.java")

            assertTrue "MyDomainDTO is missing.", myDomainDtoFile.exists()
            assertTrue "OtherDomainDTO is missing.", otherDomainDtoFile.exists()
            assertTrue "HasManyDTO is missing.", hasManyDtoFile.exists()

            assertEquals """\
package org.example;

import java.util.List;
import org.example.sub.HasManyDTO;

public class MyDomainDTO implements grails.plugins.dto.DTO {
    private long id;
    private String name;
    private int age;
    private OtherDomainDTO theOther;
    private List<HasManyDTO> items;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public OtherDomainDTO getTheOther() { return theOther; }
    public void setTheOther(OtherDomainDTO theOther) { this.theOther = theOther; }
    public List<HasManyDTO> getItems() { return items; }
    public void setItems(List<HasManyDTO> items) { this.items = items; }
}
""", myDomainDtoFile.text

            assertEquals """\
package org.example;

public class OtherDomainDTO implements grails.plugins.dto.DTO {
    private Long id;
    private SomeType type;
    private MyDomainDTO owner;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SomeType getType() { return type; }
    public void setType(SomeType type) { this.type = type; }
    public MyDomainDTO getOwner() { return owner; }
    public void setOwner(MyDomainDTO owner) { this.owner = owner; }
}
""", otherDomainDtoFile.text

            assertEquals """\
package org.example.sub;

import java.math.BigDecimal;

public class HasManyDTO implements grails.plugins.dto.DTO {
    private String uniqueName;
    private Integer number;
    private BigDecimal amount;
    private String stuff;

    public String getUniqueName() { return uniqueName; }
    public void setUniqueName(String uniqueName) { this.uniqueName = uniqueName; }
    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStuff() { return stuff; }
    public void setStuff(String stuff) { this.stuff = stuff; }
}
""", hasManyDtoFile.text
        }
    }

    void testGenerateNoRecurse() {
        def testProperties = [
            [ name: "name", association: false, type: String, referencedPropertyType: String ],
            [ name: "age", association: false, type: int, referencedPropertyType: int ],
            [ name: "theOther", association: true, type: org.example.OtherDomain, referencedPropertyType: org.example.OtherDomain ],
            [ name: "items", association: true, type: List, referencedPropertyType: org.example.sub.HasMany ]
        ]
        def mockDomainClass = mock(GrailsDomainClass)
        mockDomainClass.clazz.returns([ package: [ name: "org.example" ] ])
        mockDomainClass.shortName.returns("MyDomain")
        mockDomainClass.identifier.returns([ name: "id", association: false, type: long, referencedPropertyType: long ])
        mockDomainClass.persistentProperties.returns(testProperties)
        
        play {
            def writer = new StringWriter()
            def generator = new DefaultGrailsDtoGenerator(false)
            generator.generateNoRecurse(mockDomainClass, writer)
            
            assertEquals "Generated source does not match what was expected.", """\
package org.example;

import java.util.List;
import org.example.sub.HasManyDTO;

public class MyDomainDTO implements grails.plugins.dto.DTO {
    private long id;
    private String name;
    private int age;
    private OtherDomainDTO theOther;
    private List<HasManyDTO> items;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public OtherDomainDTO getTheOther() { return theOther; }
    public void setTheOther(OtherDomainDTO theOther) { this.theOther = theOther; }
    public List<HasManyDTO> getItems() { return items; }
    public void setItems(List<HasManyDTO> items) { this.items = items; }
}
""", writer.toString()
        }
    }
}
