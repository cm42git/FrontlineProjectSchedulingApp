package com.techelevator.dao;

import com.techelevator.model.BaselinePhaseItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcScheduleDao implements ScheduleDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcScheduleDao(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }


    @Override
    public Integer projectIdFromProjectName(String projectName) {
        String sql = "SELECT id FROM project WHERE project_name = ?;";
        return jdbcTemplate.queryForObject(sql, Integer.class, projectName);
    }

    @Override
    public Integer phaseIdFromPhaseDescription(String description) {
        String sql = "SELECT id FROM phase_items WHERE item_description = ?;";
        return jdbcTemplate.queryForObject(sql, Integer.class, description);
    }

    @Override
    public List<BaselinePhaseItem> listBaselineScheduleItems(int projectId) {
        String sql = "SELECT b.id, b.project_id, phase_item, item_date, item_tasks, item_description, phase " +
                "FROM baseline_sched_items b " +
                "JOIN phase_items pi on pi.id = b.phase_item " +
                "WHERE b.project_id = ?;";
        SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, projectId);
        List<BaselinePhaseItem> baselinePhaseItem = new ArrayList<>();
        while (rs.next()) {
            baselinePhaseItem.add(mapRowToBaselineSchedule(rs));
        }
        return baselinePhaseItem;
    }

    @Override
    public void addToBaselineSchedule(BaselinePhaseItem baselinePhaseItem) {
        String sql = "INSERT INTO baseline_sched_items " +
                "(project_id, phase_item, item_date, item_tasks) " +
                "VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, baselinePhaseItem.getProjectId(), baselinePhaseItem.getPhaseItemId(), baselinePhaseItem.getItemDate(), baselinePhaseItem.getItemTasks());
    }

    @Override
    public void addNewProjectBaselineItems(int projectId) {
        String sql = "INSERT into baseline_sched_items " +
                "(project_id, phase_item) " +
                "select ?, id from phase_items;";
        jdbcTemplate.update(sql, projectId);
    }

    @Override
    public void updateBaselineScheduleItem(BaselinePhaseItem baselinePhaseItem) {
        String sql = "UPDATE baseline_sched_items " +
                "SET project_id = ?, phase_item = ?, item_date = ?, item_tasks = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sql, baselinePhaseItem.getProjectId(), baselinePhaseItem.getPhaseItemId(), baselinePhaseItem.getItemDate(), baselinePhaseItem.getItemTasks(), baselinePhaseItem.getId());
    }

    @Override
    public void deleteBaselineScheduleItem(int id) {
        String sql = "DELETE FROM baseline_sched_items WHERE id =?;";
        jdbcTemplate.update(sql, id);
    }

    private BaselinePhaseItem mapRowToBaselineSchedule(SqlRowSet rs) {
        BaselinePhaseItem baselinePhaseItem = new BaselinePhaseItem();
        baselinePhaseItem.setId(rs.getInt("id"));
        baselinePhaseItem.setProjectId(rs.getInt("project_id"));
        baselinePhaseItem.setPhaseItemId(rs.getInt("phase_item"));
        baselinePhaseItem.setItemDescription(rs.getString("item_description"));
        baselinePhaseItem.setPhaseDescription(rs.getString("phase"));
        if (rs.getDate("item_date") != null) {
            baselinePhaseItem.setItemDate(rs.getDate("item_date").toLocalDate());
        }
        baselinePhaseItem.setItemTasks(rs.getInt("item_tasks"));
        return baselinePhaseItem;
    }


}
