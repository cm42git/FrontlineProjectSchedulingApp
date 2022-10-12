package com.techelevator.dao;


import com.techelevator.model.MonthlyPhaseItem;
import com.techelevator.model.MonthlySchedule;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcMonthlyScheduleDao implements MonthlyScheduleDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcMonthlyScheduleDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MonthlySchedule> listAllMonthlySchedule() {
        String sql = "SELECT id, " +
                "project_id, " +
                "month_year, " +
                "schedule_notes, " +
                "pct_complete, " +
                "sched_prod_idx, " +
                "same_prev_month, " +
                "why_one, " +
                "why_two, " +
                "why_three, " +
                "why_four, " +
                "why_five, " +
                "tasks_substantial, " +
                "tasks_construction " +
                "FROM monthly_schedule;";
        SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);
        List<MonthlySchedule> monthlySchedules = new ArrayList<>();
        while (rs.next()) {
            monthlySchedules.add(mapRowToMonthlySchedule(rs));
        }
        return monthlySchedules;
    }

    @Override
    public List<MonthlySchedule> listAllById(int projectId) {
        String sql = "SELECT id, " +
                "project_id, " +
                "month_year, " +
                "schedule_notes, " +
                "pct_complete, " +
                "sched_prod_idx, " +
                "same_prev_month, " +
                "why_one, " +
                "why_two, " +
                "why_three, " +
                "why_four, " +
                "why_five, " +
                "tasks_substantial, " +
                "tasks_construction " +
                "FROM monthly_schedule " +
                "WHERE project_id = ?;";
        SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, projectId);
        List<MonthlySchedule> monthlySchedules = new ArrayList<>();
        while (rs.next()) {
            monthlySchedules.add(mapRowToMonthlySchedule(rs));
        }
        return monthlySchedules;
    }

    @Override
    public MonthlySchedule listMonthlySchedule(int monthlySchedId) {
        String sql = "SELECT id, " +
                "project_id, " +
                "month_year, " +
                "schedule_notes, " +
                "pct_complete, " +
                "sched_prod_idx, " +
                "same_prev_month, " +
                "why_one, " +
                "why_two, " +
                "why_three, " +
                "why_four, " +
                "why_five, " +
                "tasks_substantial, " +
                "tasks_construction " +
                "FROM monthly_schedule " +
                "WHERE id = ?;";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, monthlySchedId);
        MonthlySchedule monthlySchedule = new MonthlySchedule();
        if (rs.next()) {
            monthlySchedule = mapRowToMonthlySchedule(rs);
        }
        return monthlySchedule;
    }

    @Override
    public List<MonthlyPhaseItem> listMonthlyScheduleItems(int monthlyScheduleId) {
        String sql = "SELECT m.id, m.monthly_sched_id, phase_item, item_date, item_tasks, item_description, phase " +
                "FROM monthly_sched_items m " +
                "JOIN phase_items pi on pi.id = m.phase_item " +
                "WHERE m.monthly_sched_id = ?;";
        SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, monthlyScheduleId);
        List<MonthlyPhaseItem> monthlyPhaseItems = new ArrayList<>();
        while (rs.next()) {
            monthlyPhaseItems.add(mapRowToMonthlyPhaseItems(rs));
        }
        return monthlyPhaseItems;
    }


    @Override
    @Transactional
    public Integer addNewMonthlySchedule(int projectId, LocalDate monthYear) {
        String sql = "BEGIN; " +
                "" +
                "INSERT INTO monthly_schedule " +
                "(project_id, month_year) " +
                "VALUES " +
                "(?, ?); " +
                "INSERT INTO monthly_sched_items " +
                "(monthly_sched_id, phase_item, item_date, item_tasks) " +
                "SELECT  " +
                "(SELECT MAX(id) FROM monthly_schedule WHERE project_id = ? AND month_year = ?), " +
                "a.phase_item, a.item_date, a.item_tasks " +
                "FROM all_items_vw a " +
                "LEFT JOIN all_items_vw b ON a.project_id = b.project_id AND " +
                "a.phase_item = b.phase_item AND " +
                "a.month_year < b.month_year " +
                "WHERE b.month_year IS NULL AND " +
                "NOT (a.item_date IS NULL and a.item_tasks IS NULL) " +
                "AND a.project_id = ?; " +
                "" +
                "COMMIT;";

        Integer monthlyIdOutput = 0;
        try {
            jdbcTemplate.update(sql, projectId, monthYear, projectId, monthYear, projectId);
            sql = "SELECT MAX(id) FROM monthly_schedule WHERE project_id = ? AND month_year = ?;";
            monthlyIdOutput = jdbcTemplate.queryForObject(sql, Integer.class, projectId, monthYear);
        } catch (NullPointerException e) {
            System.out.println("Unable to retrieve new monthly schedule...");
        } catch (DataAccessException e) {
            System.out.println("Unable to access data...");
        }
        return monthlyIdOutput;
    }

    @Override
    public void addNewMonthlyScheduleItem(MonthlyPhaseItem monthlyPhaseItem) {
        String sql = "INSERT INTO monthly_sched_items " +
                "(monthly_sched_id, phase_item, item_date, item_tasks) " +
                "VALUES (?,?,?,?);";
        try {
            jdbcTemplate.update(sql, monthlyPhaseItem.getMonthlyScheduleId(), monthlyPhaseItem.getPhaseItemId(), monthlyPhaseItem.getItemDate(), monthlyPhaseItem.getItemTasks());
        }catch(NullPointerException e){
            System.out.println("Unable to retrieve monthly schedule item...");
        }catch(DataAccessException e){
            System.out.println("Unable to access data...");
        }
    }

    @Override
    public void updateMonthlyScheduleItem(MonthlyPhaseItem monthlyPhaseItem) {
        String sql = "UPDATE monthly_sched_items " +
                "SET monthly_sched_id = ?, phase_item = ?, item_date = ? , item_tasks = ? " +
                "WHERE id = ?; ";
        jdbcTemplate.update(sql, monthlyPhaseItem.getMonthlyScheduleId(), monthlyPhaseItem.getPhaseItemId(), monthlyPhaseItem.getItemDate(), monthlyPhaseItem.getItemTasks(), monthlyPhaseItem.getId());
    }

    @Override
    public void deleteMonthlyScheduleItem(int id) {
        String sql = "DELETE FROM monthly_sched_items WHERE id = ?; ";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void updateMonthlySchedule(MonthlySchedule monthlySchedule) {
        String sql = "UPDATE monthly_schedule " +
                "SET month_year = ?, " +
                "schedule_notes = ?, " +
                "pct_complete = ?, " +
                "sched_prod_idx = ?, " +
                "same_prev_month = ?, " +
                "why_one = ?, " +
                "why_two = ?, " +
                "why_three = ?, " +
                "why_four =  ?, " +
                "why_five = ?, " +
                "tasks_substantial = ?, " +
                "tasks_construction = ? " +
                "WHERE id = ?; ";
        jdbcTemplate.update(sql, monthlySchedule.getMonthYear(), monthlySchedule.getScheduleNotes(),
                monthlySchedule.getPctComplete(), monthlySchedule.getScheduleProdIdx(), monthlySchedule.isSamePrevMonth(),
                monthlySchedule.getWhyOne(), monthlySchedule.getWhyTwo(), monthlySchedule.getWhyThree(),
                monthlySchedule.getWhyFour(), monthlySchedule.getWhyFive(), monthlySchedule.getTasksSubstantial(),
                monthlySchedule.getTasksConstruction(), monthlySchedule.getId());
    }


    private MonthlyPhaseItem mapRowToMonthlyPhaseItems(SqlRowSet rs) {
        MonthlyPhaseItem monthlyPhaseItem = new MonthlyPhaseItem();
        monthlyPhaseItem.setId(rs.getInt("id"));
        monthlyPhaseItem.setMonthlyScheduleId(rs.getInt("monthly_sched_id"));
        monthlyPhaseItem.setPhaseItemId(rs.getInt("phase_item"));
        if (rs.getDate("item_date") != null) {
            monthlyPhaseItem.setItemDate(rs.getDate("item_date").toLocalDate());
        }
        monthlyPhaseItem.setItemTasks(rs.getInt("item_tasks"));
        monthlyPhaseItem.setItemDescription(rs.getString("item_description"));
        monthlyPhaseItem.setPhaseDescription(rs.getString("phase"));
        return monthlyPhaseItem;
    }


    private MonthlySchedule mapRowToMonthlySchedule(SqlRowSet rs) {
        MonthlySchedule monthlySchedule = new MonthlySchedule();
        monthlySchedule.setId(rs.getInt("id"));
        monthlySchedule.setProjectId(rs.getInt("project_id"));
        if (rs.getDate("month_year") != null) {
            monthlySchedule.setMonthYear(rs.getDate("month_year").toLocalDate());
        }
        monthlySchedule.setScheduleNotes(rs.getString("schedule_notes"));
        monthlySchedule.setPctComplete(rs.getInt("pct_complete"));
        monthlySchedule.setScheduleProdIdx(rs.getFloat("sched_prod_idx"));
        monthlySchedule.setSamePrevMonth(rs.getBoolean("same_prev_month"));
        monthlySchedule.setWhyOne(rs.getString("why_one"));
        monthlySchedule.setWhyTwo(rs.getString("why_two"));
        monthlySchedule.setWhyThree(rs.getString("why_three"));
        monthlySchedule.setWhyFour(rs.getString("why_four"));
        monthlySchedule.setWhyFive(rs.getString("why_five"));
        monthlySchedule.setTasksSubstantial(rs.getInt("tasks_substantial"));
        monthlySchedule.setTasksConstruction(rs.getInt("tasks_construction"));
        return monthlySchedule;
    }
}
