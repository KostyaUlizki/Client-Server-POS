import java.util.List;
public interface ProductDao {
    void createProduct(Product product);
    Product getProductByName(String productName);
    List<Product> getAllProducts();
    void deleteProduct(String productName);
}
