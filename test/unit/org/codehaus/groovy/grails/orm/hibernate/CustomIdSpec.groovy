package org.codehaus.groovy.grails.orm.hibernate

import grails.persistence.Entity

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.hibernate.id.IdentifierGenerationException
import org.springframework.orm.hibernate3.HibernateSystemException

class CustomIdSpec extends GormSpec {

    void 'Test saving an object with a custom id'() {
        when:
        def o = new ClassWithCustomId(name: 'Jeff')

        then:
        o.save()
    }

    void 'Test saving an object with a custom id that uses the assigned generator'() {
        when:
        new ClassWithAssignedCustomId(name: 'Jeff').save()

        then:
        HibernateSystemException ex = thrown()
        ex.rootCause instanceof IdentifierGenerationException

        when:
        def o = new ClassWithAssignedCustomId(name: 'Jeff', myId: 42)

        then:
        o.save()
    }

    void 'Test that a custom id properly is configured as such'() {
        given:
        GrailsDomainClass grailsDomainClass = grailsApplication.getDomainClass(ClassWithCustomId.name)

        when:
        GrailsDomainClassProperty myIdProperty = grailsDomainClass.getPersistentProperty('myId')
        GrailsDomainClassProperty myNameProperty = grailsDomainClass.getPersistentProperty('name')

        then:
        !myNameProperty.isIdentity()
        myIdProperty.isIdentity()
    }

    @Override
    List getDomainClasses() {
        [ClassWithCustomId, ClassWithAssignedCustomId]
    }
}

@Entity
class ClassWithCustomId {
    String name
    Long myId

    static mapping = {
        id name: 'myId'
    }
}

@Entity
class ClassWithAssignedCustomId {
    String name
    Long myId

    static mapping = {
        id name: 'myId', generator: 'assigned'
    }
}
