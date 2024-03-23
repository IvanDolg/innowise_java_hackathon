package innowice.java.hackathon.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(name = "tb_echange_rate")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "price")
    private String price;

    @Column(name = "date")
    private String date;

    public String getFormattedText() {
        return  price + " bitcoin";
    }

}