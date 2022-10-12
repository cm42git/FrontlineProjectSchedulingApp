package com.techelevator.controller;

import com.techelevator.dao.ReportDao;
import com.techelevator.model.MasterSchedule;
import com.techelevator.model.StatusReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ReportController {

    @Autowired
    ReportDao reportDao;

    @GetMapping(path = "/statusreport")
    public List<StatusReport> listStatusReport() {
        return reportDao.listStatusReport();
    }

    @GetMapping(path = "/master")
    public List<MasterSchedule> listMasterSchedules() {
        return reportDao.listMaster();
    }
}
