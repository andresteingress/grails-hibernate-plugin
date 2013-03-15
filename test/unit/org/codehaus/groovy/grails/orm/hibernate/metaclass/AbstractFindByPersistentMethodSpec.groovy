package org.codehaus.groovy.grails.orm.hibernate.metaclass

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.orm.hibernate.Customer
import org.codehaus.groovy.grails.orm.hibernate.GormSpec
import org.codehaus.groovy.grails.orm.hibernate.HibernateDatastore
import org.grails.datastore.mapping.model.MappingContext
import org.hibernate.Criteria
import org.hibernate.SessionFactory
import org.hibernate.impl.CriteriaImpl

import spock.lang.Issue

class AbstractFindByPersistentMethodSpec extends GormSpec {

    @Issue('GRAILS-8762')
    void "Test that findBy configures a limit of 1"() {
        given:"A finder method"
            final datastore = new HibernateDatastore(applicationContext.getBean(MappingContext), sessionFactory, grailsApplication.config)
            datastore.applicationContext = applicationContext
            def finder = new TestFindBy(datastore, grailsApplication)
        when:"The finder is invoked"
            CriteriaImpl c  = finder.invoke(Customer, "findByName", "Bob")

        then: "ensure that the limit is one"
            c.maxResults == 1
    }

    @Override
    List getDomainClasses() {
        [Customer]
    }
}

class TestFindBy extends AbstractFindByPersistentMethod {

    TestFindBy(HibernateDatastore datastore, GrailsApplication application) {
        super(datastore, application, datastore.getSessionFactory(), Thread.currentThread().contextClassLoader, ~/(findBy)([A-Z]\w*)/, ["Or", "And"] as String[])
    }

    @Override
    protected Object getResult(Criteria crit, boolean useLimit) {
        return crit
    }
}
