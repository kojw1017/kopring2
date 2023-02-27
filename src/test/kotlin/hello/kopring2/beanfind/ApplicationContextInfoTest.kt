package hello.kopring2.beanfind

import hello.kopring2.AppConfig
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class ApplicationContextInfoTest {
    val ac = AnnotationConfigApplicationContext(AppConfig::class.java)
    @Test
    @DisplayName("모든 빈 출력")
    fun findAllBean(){
        ac.beanDefinitionNames.forEach {
            if(ac.getBeanDefinition(it).role == BeanDefinition.ROLE_APPLICATION){
                println("name = $it object ${ac.getBean(it)}")
            }
        }
    }

    @Test
    @DisplayName("부모 타입 조회 Any")
    fun findAllBeanByObjectType(){
        val beansOfType = ac.getBeansOfType(Nothing::class.java)
        beansOfType.keys.forEach {
            print("key = $it value =  ${beansOfType.get(it)}")
        }
    }
}