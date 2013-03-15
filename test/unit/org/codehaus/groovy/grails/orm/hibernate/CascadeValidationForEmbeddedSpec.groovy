package org.codehaus.groovy.grails.orm.hibernate

import grails.persistence.Entity

/**
 */
class CascadeValidationForEmbeddedSpec extends GormSpec{

    void "Test that validation cascades to embedded entities"() {
        when:"An entity with an invalid embedded entity is created"
            def company = new Company()
            company.address = new CompanyAddress()

        then:"The entity is invalid"
            company.validate() == false

        when:"The embedded entity is made valid"
            company.address.country = "Spain"

        then:"The root entity validates"
            company.validate() == true
    }
    @Override
    List getDomainClasses() {
        [Company]
    }
}

@Entity
class Company {
    CompanyAddress address

    static embedded = ['address']

    static constraints = {
        address(nullable:false)
    }
}

@Entity
class CompanyAddress {
    String country

    static constraints = {
        country(blank:false)
    }
}