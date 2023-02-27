package hello.kopring2

import hello.kopring2.member.Grade
import hello.kopring2.member.Member
import hello.kopring2.member.MemberService
import hello.kopring2.order.OrderService
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main(){
    val application: ApplicationContext = AnnotationConfigApplicationContext(AppConfig::class.java)
    val memberService = application.getBean("memberService", MemberService::class.java)
    val orderService = application.getBean("orderService", OrderService::class.java)

    val memberId = 1L
    val member = Member(memberId, "memberA", Grade.VIP)
    memberService.join(member)
    val order = orderService.createOrder(memberId, "itemA", 20000)
    println(order)
}
