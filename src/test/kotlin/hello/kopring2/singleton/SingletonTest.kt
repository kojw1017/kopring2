package hello.kopring2.singleton

import hello.kopring2.AppConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SingletonTest {
    @Test
    @DisplayName("스프링 없는 순수한 DI 컨테이너")
    fun pureContainer(){
        val appConfig = AppConfig()
        val memberService1 = appConfig.memberService()
        val memberService2 = appConfig.memberService()
        assertNotEquals(memberService1, memberService2)
        assertEquals(memberService1, memberService2)
    }
}