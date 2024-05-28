public class NewCustomer extends Customer {
    private double discountPercentage = 0.1; // 10% discount

    public NewCustomer(String id) {
        super(id);
    }

    @Override
    protected double calculateTotalAmount(String itemName, int quantity) {
        double totalAmount = super.calculateTotalAmount(itemName, quantity);
        return totalAmount - (totalAmount * discountPercentage);
    }
}
