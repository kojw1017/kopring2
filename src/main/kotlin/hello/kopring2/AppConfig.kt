package hello.kopring2

import hello.kopring2.discount.FixDiscountPolicy
import hello.kopring2.discount.RateDiscountPolicy
import hello.kopring2.member.MemberServiceImpl
import hello.kopring2.member.MemoryMemberRepo
import hello.kopring2.order.OrderServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {
    @Bean
    fun memberService() = MemberServiceImpl(memberRepository())
    @Bean
    fun memberRepository() = MemoryMemberRepo()
    @Bean
    fun orderService() = OrderServiceImpl(MemoryMemberRepo(), discountPolicy())
    @Bean
    fun discountPolicy() = RateDiscountPolicy()
//    @Bean
//    fun discountPolicy() = FixDiscountPolicy()
}