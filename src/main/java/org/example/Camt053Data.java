package org.converter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Container for all CAMT.053 data extracted from XML
 */
@Data
@NoArgsConstructor
public class Camt053Data {
    private Statement statement;
    private BalanceInfo balanceInfo;
    private List<TransactionInfo> transactions = new ArrayList<>();
    public void addTransaction(TransactionInfo transaction) {
        this.transactions.add(transaction);
    }
}

/**
 * Statement-level information shared across all rows
 */
@Data
class Statement {
    private String stmtId;
    private String seqNb;
    private String pageNumber;
    private boolean lastPageIndicator;
    private String statementId;
    private String currency;
    private String accountId;
    private String fromBic;
    private String toBic;
}

/**
 * Balance information (OPBD and CLBD)
 */
@Data
class BalanceInfo {
    private LocalDate balanceDate;
    private BigDecimal openingBalance;  // OPBD
    private BigDecimal closingBalance;  // CLBD
    private String creditDebitIndicator; // From CLBD

    public BigDecimal getNetMovement() {
        if (openingBalance != null && closingBalance != null) {
            return closingBalance.subtract(openingBalance);
        }
        return BigDecimal.ZERO;
    }
}

/**
 * Individual transaction (Ntry) information
 */
@Data
class TransactionInfo {
    private LocalDate bookingDate;
    private BigDecimal amount;
    private String status;
    private String accountServicerReference;
    private String entryCreditDebitIndicator;
    private String transactionCreditDebitIndicator;

    /**
     * Returns signed amount: positive for CRDT, negative for DBIT
     */
    public BigDecimal getSignedAmount() {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return "DBIT".equals(entryCreditDebitIndicator) ? amount.negate() : amount;
    }

}
