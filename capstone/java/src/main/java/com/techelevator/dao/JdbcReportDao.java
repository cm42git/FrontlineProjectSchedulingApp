package com.techelevator.dao;

import com.techelevator.model.MasterSchedule;
import com.techelevator.model.StatusReport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcReportDao implements ReportDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcReportDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<StatusReport> listStatusReport() {
        List<StatusReport> statusReports = new ArrayList<>();
        String sql = "SELECT " +
                "contract_name, " +
                "contract_id, " +
                "project_name, " +
                "project_id, " +
                "phase_item, " +
                "item_date, " +
                "item_tasks, " +
                "month_year, " +
                "phase, " +
                "item_description, " +
                "schedule_notes, " +
                "pct_complete, " +
                "sched_prod_idx " +
                "FROM current_items_vw;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            statusReports.add(mapRowToStatusReport(results));
        }
        return statusReports;
    }


    @Override
    public List<MasterSchedule> listMaster() {
        List<MasterSchedule> masterSchedules = new ArrayList<>();
        String sql = "SELECT " +
                "contract_name, " +
                "contract_id, " +
                "project_name, " +
                "project_id, " +
                "phase, " +
                "start_dt, " +
                "end_dt, " +
                "duration_days " +
                "FROM schedule_vw;";
        sql = "SELECT contract_name, " +
                "contract_id, " +
                "project_name, " +
                "project_id, " +
                "phase, " +
                "start_dt, " +
                "end_dt, " +
                "duration_days, " +
                "pct_complete " +
                "FROM schedule_vw " +
                "UNION ALL " +
                "SELECT contract_name, " +
                "contract_id, " +
                "project_name, " +
                "project_id, " +
                "'Project', " +
                "MIN(start_dt), " +
                "MAX(end_dt), " +
                "MAX(end_dt) - MIN(start_dt) + 1, " +
                "pct_complete " +
                "FROM schedule_vw " +
                "GROUP BY contract_name, " +
                "contract_id, " +
                "project_name, " +
                "project_id, " +
                "pct_complete " +
                "ORDER BY project_id, start_dt;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            masterSchedules.add(mapRowToMasterSchedule(results));
        }
        return masterSchedules;
    }

    private MasterSchedule mapRowToMasterSchedule(SqlRowSet rs) {
        MasterSchedule masterSchedule = new MasterSchedule();
        masterSchedule.setContractName(rs.getString("contract_name"));
        masterSchedule.setContractId(rs.getInt("contract_id"));
        masterSchedule.setProjectName(rs.getString("project_name"));
        masterSchedule.setProjectId(rs.getInt("project_id"));
        masterSchedule.setPhase(rs.getString("phase"));
        if (rs.getDate("start_dt") != null) {
            masterSchedule.setStart(rs.getDate("start_dt").toLocalDate());
        }
        if (rs.getDate("end_dt") != null) {
            masterSchedule.setEnd(rs.getDate("end_dt").toLocalDate());
        }
        masterSchedule.setDurationDays(rs.getInt("duration_days"));
        masterSchedule.setPctComplete(rs.getInt("pct_complete"));
        return masterSchedule;
    }

    private StatusReport mapRowToStatusReport(SqlRowSet rs) {
        StatusReport statusReport = new StatusReport();
        statusReport.setContractName(rs.getString("contract_name"));
        statusReport.setContractId(rs.getInt("contract_id"));
        statusReport.setProjectName(rs.getString("project_name"));
        statusReport.setProjectId(rs.getInt("project_id"));
        statusReport.setPhaseItem(rs.getInt("phase_item"));
        if (rs.getDate("item_date") != null) {
            statusReport.setItemDate(rs.getDate("item_date").toLocalDate());
        }
        statusReport.setItemTasks(rs.getInt("item_tasks"));
        if (rs.getDate("month_year") != null) {
            statusReport.setMonthYear(rs.getDate("month_year").toLocalDate());
        }
        statusReport.setPhase(rs.getString("phase"));
        statusReport.setItemDescription(rs.getString("item_description"));
        statusReport.setSchedule_notes(rs.getString("schedule_notes"));
        int pctAdjusted = pctCompleteAdjust(rs.getInt("pct_complete"), statusReport.getItemDescription(), statusReport.getItemDate());
//        statusReport.setPct_complete(rs.getInt("pct_complete"));
        statusReport.setPct_complete(pctAdjusted);
        statusReport.setSchedProdIdx(rs.getFloat("sched_prod_idx"));
        return statusReport;
    }

    private int pctCompleteAdjust(int pctMonthly, String phaseItem, LocalDate itemDate) {
        if (phaseItem.matches("\\d\\d%") && itemDate.isBefore(LocalDate.now())) {
            return Math.min(Integer.parseInt(phaseItem.substring(0, 2)), pctMonthly);
        } else
            return pctMonthly;
    }
}
