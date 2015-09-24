package be.foreseegroup.micro.resourceservice.assignment.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by Kaj on 24/09/15.
 */
public class Assignment {
    @Id
    private String id;

    private String consultantId;
    private String customerId;

    private Date startDate;
    private Date endDate;

    public Assignment(String id, String consultantId, String customerId, Date startDate, Date endDate) {
        this.id = id;
        this.consultantId = consultantId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Assignment() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsultantId() {
        return consultantId;
    }

    public void setConsultantId(String consultantId) {
        this.consultantId = consultantId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
