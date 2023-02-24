package hello.kopring2.order

import hello.kopring2.AppConfig
import hello.kopring2.member.Grade
import hello.kopring2.member.Member
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class OrderServiceTest {
    private val memberService = AppConfig().memberService()
    private val orderService = AppConfig().orderService()
    @Test
    fun createOrder(){
        val memberId = 1L
        val member = Member(memberId, "memberA", Grade.VIP)
        memberService.join(member)
        val order = orderService.createOrder(memberId, "itemA", 10000)
        Assertions.assertEquals(order.discountPrice, 1000)
    }
}