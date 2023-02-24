package hello.kopring2.order

import hello.kopring2.discount.DisCountPolicy
import hello.kopring2.member.MemberRepo

class OrderServiceImpl(
    private val memberRepo: MemberRepo,
    private val disCountPolicy: DisCountPolicy,
): OrderService{
    override fun createOrder(memberId: Long, itemName: String, itemPrice: Int): Order {
        val member = memberRepo.findById(memberId)
        val discountPrice = disCountPolicy.discount(member, itemPrice)
        return Order(memberId, itemName, itemPrice, discountPrice)
    }
}