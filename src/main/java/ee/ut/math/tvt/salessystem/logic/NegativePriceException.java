package ee.ut.math.tvt.salessystem.logic;

public class NegativePriceException extends Exception {

    public NegativePriceException(){
        super("Price cannot be negative. Please enter correct price");
    }

}
