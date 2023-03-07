package hello.kopring2

import hello.kopring2.member.Grade
import hello.kopring2.member.Member
import hello.kopring2.member.MemberService
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main(){
//    val appConfig = AppConfig()
//    val memberService: MemberService = appConfig.memberService()
    val application: ApplicationContext = AnnotationConfigApplicationContext(AppConfig::class.java)
    val memberService = application.getBean("memberService", MemberService::class.java)
    val member = Member(1L, "memberA", Grade.VIP)
    memberService.join(member)
    val findMember = memberService.findMember(1L)
    println("new member ${member.name}")
    println("fine member ${findMember.name}")
}
