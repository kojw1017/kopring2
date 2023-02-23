package hello.kopring2.order

interface OrderService {
    fun createOrder(memberId: Long, itemName: String, itemPrice: Int): Order
}