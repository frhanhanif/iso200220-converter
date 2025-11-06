package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates CSV output from CAMT.053 data
 */
public class Camt053CsvGenerator {
    
    private static final String PIPE = "|";
    private static final String EMPTY = "";
    
    /**
     * Generate CSV lines from CAMT.053 data
     */
    public List<String> generate(Camt053Data data) {
        List<String> csvLines = new ArrayList<>();
        
        // Add balance row (one per statement)
        csvLines.add(generateBalanceRow(data));
        
        // Add transaction rows (one per Ntry)
        for (TransactionInfo transaction : data.getTransactions()) {
            csvLines.add(generateTransactionRow(data, transaction));
        }
        
        return csvLines;
    }
    
    /**
     * Generate balance row (Bal|...)
     * Format: Bal|MsgId|ElctrncSeqNb|PgNb|?|Date|Ccy|ClosingBal|OpeningBal|NetMovement|?|CdtDbtInd|?|?|?|?|FromBIC|ToBIC|AccountId|LastPgInd|StmtId
     */
    private String generateBalanceRow(Camt053Data data) {
        StatementInfo stmt = data.getStatementInfo();
        BalanceInfo bal = data.getBalanceInfo();
        
        return new CsvRowBuilder()
            .add("Bal")                                    // Row type
            .add(stmt.getMessageId())                      // Message ID
            .add(stmt.getElectronicSeqNb())               // Electronic Sequence Number
            .add(stmt.getPageNumber())                     // Page Number
            .add(EMPTY)                                    // Unknown field
            .add(formatDate(bal.getBalanceDate()))         // Balance Date
            .add(stmt.getCurrency())                       // Currency
            .add(formatAmount(bal.getClosingBalance()))    // Closing Balance
            .add(formatAmount(bal.getOpeningBalance()))    // Opening Balance
            .add(formatAmount(bal.getNetMovement()))       // Net Movement
            .add(EMPTY)                                    // Unknown field
            .add(bal.getCreditDebitIndicator())            // Credit/Debit Indicator
            .add(EMPTY)                                    // Unknown field
            .add(EMPTY)                                    // Unknown field
            .add(EMPTY)                                    // Unknown field
            .add(EMPTY)                                    // Unknown field
            .add(stmt.getFromBic())                        // From BIC
            .add(stmt.getToBic())                          // To BIC
            .add(stmt.getAccountId())                      // Account ID
            .add(formatBoolean(stmt.isLastPageIndicator())) // Last Page Indicator
            .add(stmt.getStatementId())                    // Statement ID
            .build();
    }
    
    /**
     * Generate transaction row (Trx|...)
     * Format: Trx|MsgId|ElctrncSeqNb|PgNb|?|Date|Ccy|Amount|Status|?|AcctSvcrRef|CdtDbtInd|SignedAmount|TxCdtDbtInd|?|?|FromBIC|ToBIC|AccountId|LastPgInd|StmtId
     */
    private String generateTransactionRow(Camt053Data data, TransactionInfo tx) {
        StatementInfo stmt = data.getStatementInfo();
        
        return new CsvRowBuilder()
            .add("Trx")                                        // Row type
            .add(stmt.getMessageId())                          // Message ID
            .add(stmt.getElectronicSeqNb())                   // Electronic Sequence Number
            .add(stmt.getPageNumber())                         // Page Number
            .add("X")                                          // Unknown field (appears as "X" in sample)
            .add(formatDate(tx.getBookingDate()))              // Booking Date
            .add(stmt.getCurrency())                           // Currency
            .add(formatAmount(tx.getAmount()))                 // Amount
            .add(tx.getStatus())                               // Status (e.g., BOOK)
            .add(EMPTY)                                        // Unknown field
            .add(tx.getAccountServicerReference())             // Account Servicer Reference
            .add(tx.getEntryCreditDebitIndicator())           // Entry Credit/Debit Indicator
            .add(formatAmount(tx.getSignedAmount()))           // Signed Amount
            .add(tx.getTransactionCreditDebitIndicator())     // Transaction Credit/Debit Indicator
            .add(EMPTY)                                        // Unknown field
            .add(EMPTY)                                        // Unknown field
            .add(stmt.getFromBic())                            // From BIC
            .add(stmt.getToBic())                              // To BIC
            .add(stmt.getAccountId())                          // Account ID
            .add(formatBoolean(stmt.isLastPageIndicator()))   // Last Page Indicator
            .add(stmt.getStatementId())                        // Statement ID
            .build();
    }
    
    /**
     * Format date to string (yyyy-MM-dd)
     */
    private String formatDate(java.time.LocalDate date) {
        return date != null ? date.toString() : EMPTY;
    }
    
    /**
     * Format amount to string (without trailing zeros)
     */
    private String formatAmount(java.math.BigDecimal amount) {
        return amount != null ? amount.stripTrailingZeros().toPlainString() : EMPTY;
    }
    
    /**
     * Format boolean to string (true/false)
     */
    private String formatBoolean(boolean value) {
        return String.valueOf(value);
    }
    
    /**
     * Helper class for building CSV rows with pipe delimiter
     */
    private static class CsvRowBuilder {
        private final List<String> fields = new ArrayList<>();
        
        public CsvRowBuilder add(String value) {
            fields.add(value != null ? value : EMPTY);
            return this;
        }
        
        public String build() {
            return String.join(PIPE, fields);
        }
    }
}
