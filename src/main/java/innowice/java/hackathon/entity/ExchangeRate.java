package innowice.java.hackathon.entity;

import java.time.LocalDateTime;

public class ExchangeRate {
    private Long id;
    private String symbol;
    private String price;

    public String getSymbol() {
        return symbol;
    }

    public String getPrice() {
        return price;
    }

    public String getFormattedText() {
        return  price + " bitcoin";
    }
}