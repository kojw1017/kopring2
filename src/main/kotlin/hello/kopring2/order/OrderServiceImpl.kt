package hello.kopring2.order

import hello.kopring2.discount.DisCountPolicy
import hello.kopring2.discount.FixDiscountPolicy
import hello.kopring2.member.MemoryMemberRepo

class OrderServiceImpl(
    private val memberRepo: MemoryMemberRepo = MemoryMemberRepo(),
    private val disCountPolicy: DisCountPolicy = FixDiscountPolicy(),
):OrderService{
    override fun createOrder(memberId: Long, itemName: String, itemPrice: Int): Order {
        val member = memberRepo.findById(memberId)
        val discountPrice = disCountPolicy.discount(member, itemPrice)
        return Order(memberId, itemName, itemPrice, discountPrice)
    }
}