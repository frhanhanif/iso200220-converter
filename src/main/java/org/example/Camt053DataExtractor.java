package org.example;

import com.prowidesoftware.swift.model.mx.MxCamt05300108;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Extracts data from CAMT.053 XML into structured data model
 */
public class Camt053DataExtractor {
    
    /**
     * Main extraction method
     */
    public Camt053Data extract(MxCamt05300108 camt053) {
        Camt053Data data = new Camt053Data();
        
        // Extract statement-level information
        data.setStatementInfo(extractStatementInfo(camt053));
        
        // Extract balance information
        data.setBalanceInfo(extractBalanceInfo(camt053));
        
        // Extract transactions
        extractTransactions(camt053, data);
        
        return data;
    }
    
    /**
     * Extract statement-level information
     */
    private StatementInfo extractStatementInfo(MxCamt05300108 camt053) {
        StatementInfo info = new StatementInfo();
        
        // TODO: Replace with actual Prowide library methods
        // Message ID from: camt053.getBkToCstmrStmt().getGrpHdr().getMsgId()
        info.setMessageId("PLACEHOLDER_MSG_ID");
        
        // Statement level data from: camt053.getBkToCstmrStmt().getStmt().get(0)
        // Statement ID: stmt.getId()
        info.setStatementId("PLACEHOLDER_STMT_ID");
        
        // Electronic Sequence Number: stmt.getElctrncSeqNb()
        info.setElectronicSeqNb("PLACEHOLDER_ELEC_SEQ");
        
        // Page Number: stmt.getStmtPgntn().getPgNb()
        info.setPageNumber("PLACEHOLDER_PAGE_NB");
        
        // Last Page Indicator: stmt.getStmtPgntn().getLastPgInd()
        info.setLastPageIndicator(true); // PLACEHOLDER
        
        // Currency: stmt.getAcct().getCcy()
        info.setCurrency("PLACEHOLDER_CCY");
        
        // Account ID: stmt.getAcct().getId().getOthr().getId()
        info.setAccountId("PLACEHOLDER_ACCT_ID");
        
        // From BIC: Extract from AppHdr or header
        // camt053.getAppHdr().getFr().getFIId().getFinInstnId().getBICFI()
        info.setFromBic("PLACEHOLDER_FROM_BIC");
        
        // To BIC: Extract from AppHdr or header
        // camt053.getAppHdr().getTo().getFIId().getFinInstnId().getBICFI()
        info.setToBic("PLACEHOLDER_TO_BIC");
        
        return info;
    }
    
    /**
     * Extract balance information (OPBD and CLBD)
     */
    private BalanceInfo extractBalanceInfo(MxCamt05300108 camt053) {
        BalanceInfo info = new BalanceInfo();
        
        // TODO: Replace with actual Prowide library methods
        // Get balances from: camt053.getBkToCstmrStmt().getStmt().get(0).getBal()
        // Loop through balances and find:
        // - OPBD (Opening Booked): bal.getTp().getCdOrPrtry().getCd().equals("OPBD")
        // - CLBD (Closing Booked): bal.getTp().getCdOrPrtry().getCd().equals("CLBD")
        
        // For OPBD:
        // info.setOpeningBalance(new BigDecimal(opbdBal.getAmt().getValue()));
        
        // For CLBD:
        // info.setClosingBalance(new BigDecimal(clbdBal.getAmt().getValue()));
        // info.setCreditDebitIndicator(clbdBal.getCdtDbtInd().value());
        // info.setBalanceDate(LocalDate.parse(clbdBal.getDt().getDt().toString()));
        
        // PLACEHOLDER VALUES
        info.setOpeningBalance(new BigDecimal("25.00"));
        info.setClosingBalance(new BigDecimal("20.00"));
        info.setCreditDebitIndicator("CRDT");
        info.setBalanceDate(LocalDate.parse("2025-05-30"));
        
        return info;
    }
    
    /**
     * Extract all transactions (Ntry elements)
     */
    private void extractTransactions(MxCamt05300108 camt053, Camt053Data data) {
        // TODO: Replace with actual Prowide library methods
        // Get entries from: camt053.getBkToCstmrStmt().getStmt().get(0).getNtry()
        // Loop through each entry:
        
        // for (Ntry entry : stmt.getNtry()) {
        //     TransactionInfo txInfo = extractSingleTransaction(entry);
        //     data.addTransaction(txInfo);
        // }
        
        // PLACEHOLDER: Add sample transactions
        for (int i = 0; i < 7; i++) {
            TransactionInfo txInfo = new TransactionInfo();
            txInfo.setBookingDate(LocalDate.parse("2025-05-30"));
            txInfo.setAmount(new BigDecimal("1.00"));
            txInfo.setStatus("BOOK");
            txInfo.setAccountServicerReference("OKASITHBK071603");
            
            // First transaction is CRDT, rest are DBIT
            String cdtDbtInd = (i == 0) ? "CRDT" : "DBIT";
            txInfo.setEntryCreditDebitIndicator(cdtDbtInd);
            txInfo.setTransactionCreditDebitIndicator(cdtDbtInd);
            
            data.addTransaction(txInfo);
        }
    }
    
    /**
     * Extract single transaction from Ntry element
     */
    private TransactionInfo extractSingleTransaction(Object entry) {
        TransactionInfo txInfo = new TransactionInfo();
        
        // TODO: Replace with actual Prowide library methods
        // Amount: entry.getAmt().getValue()
        // txInfo.setAmount(new BigDecimal(entry.getAmt().getValue()));
        
        // Credit/Debit Indicator: entry.getCdtDbtInd().value()
        // txInfo.setEntryCreditDebitIndicator(entry.getCdtDbtInd().value());
        
        // Status: entry.getSts().getCd()
        // txInfo.setStatus(entry.getSts().getCd());
        
        // Booking Date: entry.getBookgDt().getDt()
        // txInfo.setBookingDate(LocalDate.parse(entry.getBookgDt().getDt().toString()));
        
        // Account Servicer Reference: entry.getAcctSvcrRef()
        // txInfo.setAccountServicerReference(entry.getAcctSvcrRef());
        
        // Transaction level Credit/Debit from NtryDtls:
        // entry.getNtryDtls().get(0).getTxDtls().get(0).getCdtDbtInd().value()
        // txInfo.setTransactionCreditDebitIndicator(...);
        
        return txInfo;
    }
}
