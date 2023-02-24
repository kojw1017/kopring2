package hello.kopring2.order

class Order(
    private var memberId: Long,
    private var itemName: String,
    private var itemPrice: Int,
    var discountPrice: Int,
) {
    fun calculate() = itemPrice - discountPrice
    override fun toString(): String {
        return "Order(memberId=$memberId, itemName='$itemName', itemPrice=$itemPrice, discountPrice=$discountPrice)"
    }
}