package com.finforge.service;

import com.finforge.dto.ReportDTO;
import com.finforge.exception.DAOException;

/**
 * Service interface for generating financial reports.
 */
public interface ReportService {

    /**
     * Builds a complete {@link ReportDTO} for the given user that includes:
     * <ul>
     *   <li>Total income</li>
     *   <li>Total expense</li>
     *   <li>Net savings (income − expense)</li>
     *   <li>Monthly expense breakdown</li>
     *   <li>Category-wise expense breakdown</li>
     * </ul>
     */
    ReportDTO generateReport(int userId) throws DAOException;
}
