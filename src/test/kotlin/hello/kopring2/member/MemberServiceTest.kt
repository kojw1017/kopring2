package hello.kopring2.member

import hello.kopring2.AppConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MemberServiceTest {
    private val memberService = AppConfig().memberService()
    @Test
    fun join(){
        //given
        val member = Member(1L, "MemberA", Grade.VIP)
        //when
        memberService.join(member)
        val findMember = memberService.findMember(1L)
        //then
        Assertions.assertEquals(member.name, findMember.name)
    }
}