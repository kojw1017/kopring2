package hello.kopring2.order

class Order(
    var memberId: Long,
    var itemName: String,
    var itemPrice: Int,
    var discountPrice: Int,
) {
    fun calculate() = itemPrice - discountPrice
    override fun toString(): String {
        return "Order(memberId=$memberId, itemName='$itemName', itemPrice=$itemPrice, discountPrice=$discountPrice)"
    }
}