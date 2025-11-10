1import com.prowidesoftware.swift.model.mx.MxCamt05300108;
2import com.prowidesoftware.swift.model.mx.dic.*;
3import java.math.BigDecimal;
4import java.time.LocalDate;
5import java.util.List;
6
7/**
8 * CAMT.053 Extractor using correct Prowide library types
9 */
10public class Camt053Extractor {
11    
12    public Camt053Data extract(MxCamt05300108 camt053) {
13        Camt053Data data = new Camt053Data();
14        
15        // Extract document-level information from AppHdr
16        data.setMessageId(camt053.getAppHdr().getBizMsgIdr());
17        data.setFromBic(camt053.getAppHdr().getFr().getFIId().getFinInstnId().getBICFI());
18        data.setToBic(camt053.getAppHdr().getTo().getFIId().getFinInstnId().getBICFI());
19        
20        // Extract statements
21        BankToCustomerStatementV08 bkToCstmrStmt = camt053.getBkToCstmrStmt();
22        List<AccountStatement10> statements = bkToCstmrStmt.getStmt();
23        
24        for (int i = 0; i < statements.size(); i++) {
25            Statement stmt = extractStatement(statements.get(i));
26            data.addStatement(stmt);
27        }
28        
29        return data;
30    }
31    
32    private Statement extractStatement(AccountStatement10 stmtObj) {
33        Statement stmt = new Statement();
34        
35        // Statement identification
36        stmt.setStmtId(stmtObj.getId());
37        stmt.setSeqNb(stmtObj.getElctrncSeqNb());
38        stmt.setPageNumber(stmtObj.getStmtPgntn().getPgNb());
39        stmt.setLastPageIndicator(stmtObj.getStmtPgntn().isLastPgInd());
40        
41        // Account information
42        stmt.setCurrency(stmtObj.getAcct().getCcy());
43        stmt.setAccountId(stmtObj.getAcct().getId().getOthr().getId());
44        
45        // Extract balances
46        List<CashBalance8> balances = stmtObj.getBal();
47        for (int i = 0; i < balances.size(); i++) {
48            Balance balance = extractBalance(balances.get(i));
49            stmt.addBalance(balance);
50        }
51        
52        // Extract entries
53        List<ReportEntry10> entries = stmtObj.getNtry();
54        for (int i = 0; i < entries.size(); i++) {
55            Entry entry = extractEntry(entries.get(i));
56            stmt.addEntry(entry);
57        }
58        
59        return stmt;
60    }
61    
62    private Balance extractBalance(CashBalance8 balObj) {
63        Balance balance = new Balance();
64        
65        // Balance type (OPBD, CLBD, CLAV)
66        balance.setBalanceType(balObj.getTp().getCdOrPrtry().getCd());
67        
68        // Amount
69        balance.setAmount(new BigDecimal(balObj.getAmt().getValue()));
70        
71        // Credit/Debit Indicator
72        balance.setCreditDebitIndicator(balObj.getCdtDbtInd().name());
73        
74        // Date
75        balance.setDate(LocalDate.parse(balObj.getDt().getDt().toString()));
76        
77        return balance;
78    }
79    
80    private Entry extractEntry(ReportEntry10 ntryObj) {
81        Entry entry = new Entry();
82        
83        // Amount
84        entry.setAmount(new BigDecimal(ntryObj.getAmt().getValue()));
85        
86        // Credit/Debit Indicator
87        entry.setCreditDebitIndicator(ntryObj.getCdtDbtInd().name());
88        
89        // Status
90        entry.setStatus(ntryObj.getSts().getCd());
91        
92        // Booking Date
93        entry.setBookingDate(LocalDate.parse(ntryObj.getBookgDt().getDt().toString()));
94        
95        // Account Servicer Reference
96        entry.setAccountServicerReference(ntryObj.getAcctSvcrRef());
97        
98        // Transaction Credit/Debit Indicator (from NtryDtls -> TxDtls)
99        if (ntryObj.getNtryDtls() != null && !ntryObj.getNtryDtls().isEmpty()) {
100            EntryDetails9 entryDetails = ntryObj.getNtryDtls().get(0);
101            if (entryDetails.getTxDtls() != null && !entryDetails.getTxDtls().isEmpty()) {
102                EntryTransaction10 txDetails = entryDetails.getTxDtls().get(0);
103                entry.setTransactionCreditDebitIndicator(txDetails.getCdtDbtInd().name());
104            }
105        }
106        
107        // Fallback: if no transaction details, use entry-level indicator
108        if (entry.getTransactionCreditDebitIndicator() == null) {
109            entry.setTransactionCreditDebitIndicator(entry.getCreditDebitIndicator());
110        }
111        
112        return entry;
113    }
114}
115