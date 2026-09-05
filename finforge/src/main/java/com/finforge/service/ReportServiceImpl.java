package com.finforge.service;

import com.finforge.dao.ReportDAO;
import com.finforge.dto.ReportDTO;
import com.finforge.exception.DAOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;

/**
 * Business-logic implementation for report generation.
 */
public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LogManager.getLogger(ReportServiceImpl.class);

    private final ReportDAO reportDAO;

    public ReportServiceImpl(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    @Override
    public ReportDTO generateReport(int userId) throws DAOException {

        logger.debug("Generating report for userId={}", userId);

        BigDecimal totalIncome  = reportDAO.getTotalIncome(userId);
        BigDecimal totalExpense = reportDAO.getTotalExpense(userId);
        BigDecimal netSavings   = totalIncome.subtract(totalExpense);

        ReportDTO report = new ReportDTO();
        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);
        report.setNetSavings(netSavings);
        report.setMonthlyExpenses(reportDAO.getMonthlyExpenses(userId));
        report.setCategoryExpenses(reportDAO.getCategoryExpenses(userId));

        return report;
    }
}
