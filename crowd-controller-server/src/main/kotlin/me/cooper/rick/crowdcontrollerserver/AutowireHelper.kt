package me.cooper.rick.crowdcontrollerserver

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

//https://stackoverflow.com/questions/12155632/injecting-a-spring-dependency-into-a-jpa-entitylistener
@Component
class AutowireHelper private constructor() : ApplicationContextAware {

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        AutowireHelper.applicationContext = applicationContext
    }

    companion object {

        private var applicationContext: ApplicationContext? = null

        fun autowire(classToAutowire: Any) {
            AutowireHelper.applicationContext!!.autowireCapableBeanFactory.autowireBean(classToAutowire)
        }
    }

}
