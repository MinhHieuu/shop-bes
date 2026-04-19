package com.beeshop.sd44.dto.request;

import java.util.Date;

/**
 * DTO để lọc đơn hàng
 * - status: trạng thái đơn hàng
 * - paymentStatus: trạng thái thanh toán
 * - type: 0 = tại quầy, 1 = online
 * - paymentMethod: 'COD', 'VNPAY', 'CASH'
 * - fromDate, toDate: khoảng thời gian
 */
public class OrderFilterRequest {
    private String name;
    private Integer status;
    private Integer paymentStatus;
    private Integer type;
    private String paymentMethod;
    private Date fromDate;
    private Date toDate;

    public OrderFilterRequest() {
    }

    public OrderFilterRequest(Integer status, Integer paymentStatus, Integer type, String paymentMethod, Date fromDate, Date toDate) {
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}


