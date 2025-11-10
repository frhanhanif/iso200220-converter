package org.converter;


import java.util.ArrayList;
import java.util.List;

/**
 * Generates CSV output from CAMT.053 data
 * Works with hierarchical structure: Camt053Data -> Statement -> Balances & Entries
 */
public class Camt053CsvBuilder {

    private static final String PIPE = "|";
    private static final String EMPTY = "";

    /**
     * Generate CSV lines from CAMT.053 data
     */
    public List<String> generate(Camt053Record data) {
        List<String> csvLines = new ArrayList<>();

        StatementRecord stmt = data.getStatement();

        // Add balance row (one per statement)
        csvLines.add(generateBalanceRow(data, stmt));

        // Add entry rows (one per Entry/Ntry)
        for (EntryRecord entryRecord : stmt.getEntries()) {
            csvLines.add(generateEntryRow(data, stmt, entryRecord));
        }

        return csvLines;
    }

    /**
     * Generate balance row (Bal|...)
     * Format: Bal|MsgId|SeqNb|PgNb|?|Date|Ccy|ClosingBal|OpeningBal|NetMovement|?|CdtDbtInd|?|?|?|?|FromBIC|ToBIC|AccountId|LastPgInd|StmtId
     */
    private String generateBalanceRow(Camt053Record data, StatementRecord stmt) {
        BalanceRecord closingBal = stmt.getClosingBalance();
        BalanceRecord openingBal = stmt.getOpeningBalance();

        return new CsvRowBuilder()
                .add("Bal")                                                             // Row type
                .add(stmt.getStmtId())                                                  // Statement ID
                .add(stmt.getSeqNbr())                                                  // Electronic Sequence Number
                .add(stmt.getPageNumber())                                              // Page Number
                .add(EMPTY)                                                             // Unknown field
                .add(closingBal.getDate())                                              // Balance Date (from CLBD)
                .add(stmt.getCurrency())                                                // Currency
                .add(formatAmount(closingBal != null ? closingBal.getAmount() : null)) // Closing Balance (CLBD)
                .add(formatAmount(openingBal != null ? openingBal.getAmount() : null)) // Opening Balance (OPBD)
                .add(formatAmount(stmt.getNetMovement()))                               // Net Movement (calculated)
                .add(EMPTY)                                                             // Unknown field
                .add(closingBal != null ? closingBal.getCreditDebitIndicator() : EMPTY) // Credit/Debit Indicator (from CLBD)
                .add(EMPTY)                                                             // Unknown field
                .add(EMPTY)                                                             // Unknown field
                .add(EMPTY)                                                             // Unknown field
                .add(EMPTY)                                                             // Unknown field
                .add(data.getFromBic())                                                 // From BIC
                .add(data.getToBic())                                                   // To BIC
                .add(stmt.getAccountId())                                               // Account ID
                .add(formatBoolean(stmt.isLastPageIndicator()))                        // Last Page Indicator
                .add(data.getBizMsgIdr())                                                  // Statement ID
                .build();
    }

    /**
     * Generate entry/transaction row (Trx|...)
     * Format: Trx|MsgId|SeqNb|PgNb|?|Date|Ccy|Amount|Status|?|AcctSvcrRef|CdtDbtInd|SignedAmount|TxCdtDbtInd|?|?|FromBIC|ToBIC|AccountId|LastPgInd|StmtId
     */
    private String generateEntryRow(Camt053Record data, StatementRecord stmt, EntryRecord entryRecord) {
        return new CsvRowBuilder()
                .add("Trx")                                            // Row type
                .add(data.getMessageId())                              // Message ID
                .add(stmt.getSeqNb())                                  // Electronic Sequence Number
                .add(stmt.getPageNumber())                             // Page Number
                .add("X")                                              // Unknown field (appears as "X" in sample)
                .add(formatDate(entryRecord.getBookingDate()))               // Booking Date
                .add(stmt.getCurrency())                               // Currency
                .add(formatAmount(entryRecord.getAmount()))                  // Amount
                .add(entryRecord.getStatus())                                // Status (e.g., BOOK)
                .add(EMPTY)                                            // Unknown field
                .add(entryRecord.getAccountServicerReference())              // Account Servicer Reference
                .add(entryRecord.getCreditDebitIndicator())                  // Entry Credit/Debit Indicator
                .add(formatAmount(entryRecord.getSignedAmount()))            // Signed Amount (positive for CRDT, negative for DBIT)
                .add(entryRecord.getTransactionCreditDebitIndicator())       // Transaction Credit/Debit Indicator
                .add(EMPTY)                                            // Unknown field
                .add(EMPTY)                                            // Unknown field
                .add(data.getFromBic())                                // From BIC
                .add(data.getToBic())                                  // To BIC
                .add(stmt.getAccountId())                              // Account ID
                .add(formatBoolean(stmt.isLastPageIndicator()))       // Last Page Indicator
                .add(stmt.getStmtId())                                 // Statement ID
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
     * Uses builder pattern for clean, readable row construction
     */
    private static class CsvRowBuilder {
        private final List<String> fields = new ArrayList<>();

        /**
         * Add a field to the row
         */
        public CsvRowBuilder add(String value) {
            fields.add(value != null ? value : EMPTY);
            return this;
        }

        /**
         * Build the final CSV row string
         */
        public String build() {
            return String.join(PIPE, fields);
        }
    }
}

