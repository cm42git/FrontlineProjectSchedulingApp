package com.techelevator.dao;

import com.techelevator.model.ModChangeOrder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcModsChangeOrdersDao implements ModsChangeOrdersDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcModsChangeOrdersDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ModChangeOrder> listAllModsChangeOrders() {
        String sql = "SELECT id, " +
                "mod_co_name, " +
                "project_id, " +
                "type, " +
                "court_date, " +
                "schedule_impacted, " +
                "why_sched_impact, " +
                "how_sched_impact, " +
                "budget_impacted, " +
                "why_budgt_impact, " +
                "how_budgt_impact, " +
                "why_submit, " +
                "why_two, " +
                "why_three, " +
                "why_four, " +
                "why_five, " +
                "approved, " +
                "approv_datetm " +
                "FROM mods_changes;";
        SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);
        List<ModChangeOrder> modsChangeOrderDaos = new ArrayList<>();
        while (rs.next()) {
            modsChangeOrderDaos.add(mapRowToModsCO(rs));
        }
        return modsChangeOrderDaos;
    }

    @Override
    public Integer addToModsCO(ModChangeOrder modChangeOrder) {
        String sql = "INSERT INTO mods_changes " +
                "(mod_co_name, " +
                "project_id, " +
                "type, " +
                "court_date, " +
                "schedule_impacted, " +
                "why_sched_impact, " +
                "how_sched_impact, " +
                "budget_impacted, " +
                "why_budgt_impact, " +
                "how_budgt_impact, " +
                "why_submit, " +
                "why_two, " +
                "why_three, " +
                "why_four, " +
                "why_five) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        Integer modCoId = 0;
        try {
            jdbcTemplate.update(sql, modChangeOrder.getModCoName(), modChangeOrder.getProjectId(),
                    modChangeOrder.getType(),
                    modChangeOrder.getCourtDate(), modChangeOrder.isScheduleImpacted(),
                    modChangeOrder.getWhySchedImpact(),
                    modChangeOrder.getHowSchedImpact(), modChangeOrder.isBudgetImpacted(),
                    modChangeOrder.getWhyBudgetImpact(),
                    modChangeOrder.getHowBudgetImpact(), modChangeOrder.getWhySubmit(), modChangeOrder.getWhyTwo(),
                    modChangeOrder.getWhyThree(), modChangeOrder.getWhyFour(), modChangeOrder.getWhyFive());
            sql = "SELECT MAX(id) FROM mods_changes WHERE project_id = ? ;";
            modCoId = jdbcTemplate.queryForObject(sql, Integer.class, modChangeOrder.getProjectId());
        } catch (NullPointerException e) {
            System.out.println("Unable to retrieve new modification or change order...");
        } catch (DataAccessException e) {
            System.out.println("Unable to access data...");
        }
        return modCoId;
    }

    @Override
    public void updateModsCO(ModChangeOrder modChangeOrder) {
        String sql = "UPDATE mods_changes " +
                "SET mod_co_name = ?," +
                " project_id = ?, " +
                "type = ?, " +
                "court_date = ?, " +
                "schedule_impacted = ?, " +
                "why_sched_impact = ?, " +
                "how_sched_impact = ?, " +
                "budget_impacted = ?, " +
                "why_budgt_impact = ?, " +
                "how_budgt_impact = ?, " +
                "why_submit = ?, " +
                "why_two = ?, " +
                "why_three = ?, " +
                "why_four = ?, " +
                "why_five = ?, " +
                "approved = ?, " +
                "approv_datetm = ?" +
                "WHERE id = ?;";
        jdbcTemplate.update(sql, modChangeOrder.getModCoName(), modChangeOrder.getProjectId(), modChangeOrder.getType(),
                modChangeOrder.getCourtDate(), modChangeOrder.isScheduleImpacted(), modChangeOrder.getWhySchedImpact(),
                modChangeOrder.getHowSchedImpact(), modChangeOrder.isBudgetImpacted(),
                modChangeOrder.getWhyBudgetImpact(),
                modChangeOrder.getHowBudgetImpact(), modChangeOrder.getWhySubmit(), modChangeOrder.getWhyTwo(),
                modChangeOrder.getWhyThree(), modChangeOrder.getWhyFour(), modChangeOrder.getWhyFive(),
                modChangeOrder.isApproved(), modChangeOrder.getApproveDateTm(), modChangeOrder.getId());
    }

    @Override
    public void deleteModsChangeCO(int id) {
        String sql = "DELETE FROM mods_changes WHERE id = ?;";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean changeApprovedStatus(int id, boolean status) {
        String updateModCoSql = "UPDATE mods_changes SET approved = ?, approv_datetm = current_timestamp " +
                "WHERE id = ?;";
        return jdbcTemplate.update(updateModCoSql, status, id) == 1;
    }

    @Override
    public ModChangeOrder getSingleModCO(int id) {
        String sql = "SELECT id, " +
                "mod_co_name, " +
                "project_id, " +
                "type, " +
                "court_date, " +
                "schedule_impacted, " +
                "why_sched_impact, " +
                "how_sched_impact, " +
                "budget_impacted, " +
                "why_budgt_impact, " +
                "how_budgt_impact, " +
                "why_submit, " +
                "why_two, " +
                "why_three, " +
                "why_four, " +
                "why_five, " +
                "approved, " +
                "approv_datetm " +
                "FROM mods_changes " +
                "WHERE id = ?;";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        ModChangeOrder modChangeOrder = new ModChangeOrder();
        if (rs.next()) {
            modChangeOrder = mapRowToModsCO(rs);
        }
        return modChangeOrder;
    }

    private ModChangeOrder mapRowToModsCO(SqlRowSet rs) {
        ModChangeOrder modChangeOrder = new ModChangeOrder();
        modChangeOrder.setId(rs.getInt("id"));
        modChangeOrder.setModCoName(rs.getString("mod_co_name"));
        modChangeOrder.setProjectId(rs.getInt("project_id"));
        modChangeOrder.setType(rs.getString("type"));
        if (rs.getDate("court_date") != null) {
            try {
                modChangeOrder.setCourtDate(rs.getDate("court_date").toLocalDate());
            } catch (InvalidResultSetAccessException e) {
                System.out.println("Invalid date for " + modChangeOrder.getModCoName());
            } catch (NullPointerException e) {
                System.err.println("No court date provided for " + modChangeOrder.getModCoName());
            }
        }
        modChangeOrder.setScheduleImpacted(rs.getBoolean("schedule_impacted"));
        modChangeOrder.setWhySchedImpact(rs.getString("why_sched_impact"));
        modChangeOrder.setHowSchedImpact(rs.getString("how_sched_impact"));
        modChangeOrder.setBudgetImpacted(rs.getBoolean("budget_impacted"));
        modChangeOrder.setWhyBudgetImpact(rs.getString("why_budgt_impact"));
        modChangeOrder.setHowBudgetImpact(rs.getString("how_budgt_impact"));
        modChangeOrder.setWhySubmit(rs.getString("why_submit"));
        modChangeOrder.setWhyTwo(rs.getString("why_two"));
        modChangeOrder.setWhyThree(rs.getString("why_three"));
        modChangeOrder.setWhyFour(rs.getString("why_four"));
        modChangeOrder.setWhyFive(rs.getString("why_five"));
        modChangeOrder.setApproved(rs.getBoolean("approved"));
        if (rs.getTimestamp("approv_datetm") != null) {
            try {
                modChangeOrder.setApproveDateTm(rs.getTimestamp("approv_datetm").toLocalDateTime());
            } catch (InvalidResultSetAccessException e) {
                System.err.println("Invalid approval datetime for " + modChangeOrder.getModCoName());
            } catch (NullPointerException e) {
                System.err.println("No approval datetime provided for " + modChangeOrder.getModCoName());
            }
        }
        return modChangeOrder;
    }
}
