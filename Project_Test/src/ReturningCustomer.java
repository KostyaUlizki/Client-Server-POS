public class ReturningCustomer extends Customer {
    private double discountPercentage = 0.05; // 5% discount

    public ReturningCustomer(String id) {
        super(id);
    }

    @Override
    protected double calculateTotalAmount(String itemName, int quantity) {
        double totalAmount = super.calculateTotalAmount(itemName, quantity);
        return totalAmount - (totalAmount * discountPercentage);
    }
}
