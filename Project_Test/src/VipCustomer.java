public class VipCustomer extends Customer {
    private double discountPercentage = 0.15; // 15% discount

    public VipCustomer(String id) {
        super(id);
    }

    @Override
    protected double calculateTotalAmount(String itemName, int quantity) {
        double totalAmount = super.calculateTotalAmount(itemName, quantity);
        return totalAmount - (totalAmount * discountPercentage);
    }
}
