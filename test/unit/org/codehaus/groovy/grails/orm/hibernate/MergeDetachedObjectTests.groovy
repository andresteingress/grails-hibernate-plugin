package org.codehaus.groovy.grails.orm.hibernate

import org.springframework.orm.hibernate3.SessionFactoryUtils
import org.springframework.orm.hibernate3.SessionHolder
import org.springframework.transaction.support.TransactionSynchronizationManager

/**
* @author Graeme Rocher
* @since 1.0
*
* Created: Mar 13, 2008
*/
class MergeDetachedObjectTests extends AbstractGrailsHibernateTests {

    protected void onSetUp() {
        gcl.parseClass '''
import grails.persistence.*

@Entity
class DetachedQuestion {
    String name
    static hasMany = [answers:DetachedAnswer]
}

@Entity
class DetachedAnswer {
    String name
}
'''
    }

    void testMergeDetachedObject() {

        def questionClass = ga.getDomainClass("DetachedQuestion").clazz
        def question = questionClass.newInstance(name:"What is the capital of France?")
                                    .addToAnswers(name:"London")
                                    .addToAnswers(name:"Paris")
                                    .save(flush:true)
        assertNotNull question

        session.clear()

        question = questionClass.get(1)
        TransactionSynchronizationManager.unbindResource sessionFactory
        SessionFactoryUtils.releaseSession session, sessionFactory

        session = sessionFactory.openSession()
        TransactionSynchronizationManager.bindResource sessionFactory, new SessionHolder(session)

        question = question.merge()
        assertEquals 2, question.answers.size()
        question.name = "changed"
        question.save(flush:true)
    }

    void testStaticMergeMethod() {
        def questionClass = ga.getDomainClass("DetachedQuestion").clazz

        def question = questionClass.newInstance(name:"What is the capital of France?")
                                    .addToAnswers(name:"London")
                                    .addToAnswers(name:"Paris")
                                    .save(flush:true)
        assertNotNull question

        session.clear()

        question = questionClass.get(1)
        TransactionSynchronizationManager.unbindResource sessionFactory
        SessionFactoryUtils.releaseSession session, sessionFactory

        session = sessionFactory.openSession()
        TransactionSynchronizationManager.bindResource sessionFactory, new SessionHolder(session)

        question = questionClass.merge(question)
        assertEquals 2, question.answers.size()

        question.name = "changed"
        question.save(flush:true)
    }
}
