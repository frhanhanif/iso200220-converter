package org.example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for all CAMT.053 data extracted from XML
 */
public class Camt053Data {
    private StatementInfo statementInfo;
    private BalanceInfo balanceInfo;
    private List<TransactionInfo> transactions;
    
    public Camt053Data() {
        this.transactions = new ArrayList<>();
    }
    
    public StatementInfo getStatementInfo() {
        return statementInfo;
    }
    
    public void setStatementInfo(StatementInfo statementInfo) {
        this.statementInfo = statementInfo;
    }
    
    public BalanceInfo getBalanceInfo() {
        return balanceInfo;
    }
    
    public void setBalanceInfo(BalanceInfo balanceInfo) {
        this.balanceInfo = balanceInfo;
    }
    
    public List<TransactionInfo> getTransactions() {
        return transactions;
    }
    
    public void addTransaction(TransactionInfo transaction) {
        this.transactions.add(transaction);
    }
}

/**
 * Statement-level information shared across all rows
 */
class StatementInfo {
    private String messageId;
    private String electronicSeqNb;
    private String pageNumber;
    private boolean lastPageIndicator;
    private String statementId;
    private String currency;
    private String accountId;
    private String fromBic;
    private String toBic;
    
    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getElectronicSeqNb() { return electronicSeqNb; }
    public void setElectronicSeqNb(String electronicSeqNb) { this.electronicSeqNb = electronicSeqNb; }
    
    public String getPageNumber() { return pageNumber; }
    public void setPageNumber(String pageNumber) { this.pageNumber = pageNumber; }
    
    public boolean isLastPageIndicator() { return lastPageIndicator; }
    public void setLastPageIndicator(boolean lastPageIndicator) { this.lastPageIndicator = lastPageIndicator; }
    
    public String getStatementId() { return statementId; }
    public void setStatementId(String statementId) { this.statementId = statementId; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public String getFromBic() { return fromBic; }
    public void setFromBic(String fromBic) { this.fromBic = fromBic; }
    
    public String getToBic() { return toBic; }
    public void setToBic(String toBic) { this.toBic = toBic; }
}

/**
 * Balance information (OPBD and CLBD)
 */
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
    
    // Getters and Setters
    public LocalDate getBalanceDate() { return balanceDate; }
    public void setBalanceDate(LocalDate balanceDate) { this.balanceDate = balanceDate; }
    
    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }
    
    public BigDecimal getClosingBalance() { return closingBalance; }
    public void setClosingBalance(BigDecimal closingBalance) { this.closingBalance = closingBalance; }
    
    public String getCreditDebitIndicator() { return creditDebitIndicator; }
    public void setCreditDebitIndicator(String creditDebitIndicator) { this.creditDebitIndicator = creditDebitIndicator; }
}

/**
 * Individual transaction (Ntry) information
 */
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
    
    // Getters and Setters
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAccountServicerReference() { return accountServicerReference; }
    public void setAccountServicerReference(String accountServicerReference) { 
        this.accountServicerReference = accountServicerReference; 
    }
    
    public String getEntryCreditDebitIndicator() { return entryCreditDebitIndicator; }
    public void setEntryCreditDebitIndicator(String entryCreditDebitIndicator) { 
        this.entryCreditDebitIndicator = entryCreditDebitIndicator; 
    }
    
    public String getTransactionCreditDebitIndicator() { return transactionCreditDebitIndicator; }
    public void setTransactionCreditDebitIndicator(String transactionCreditDebitIndicator) { 
        this.transactionCreditDebitIndicator = transactionCreditDebitIndicator; 
    }
}
