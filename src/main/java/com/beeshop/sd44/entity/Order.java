package com.beeshop.sd44.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "hoa_don")
public class Order {
    private static int sum = 0;

    public Order() {
        sum += 1;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "ma")
    private String code;
    @Column(name = "ghi_chu")
    private String note;
    @Column(name = "ngay_tao")
    private Date createdAt;
    @Column(name = "ngay_thanh_toan")
    private Date paymentDate;
    @Column(name = "phi_ship")
    private Integer shippingFee;
    @Column(name = "tong_tien")
    private Double total;
    @Column(name = "phuong_thuc_thanh_toan")
    private String paymentMethod;
    @Column(name = "trang_thai")
    private Integer status;
    @Column(name = "trang_thai_thanh_toan")
    private Integer paymentStatus;
    @Column(name = "phan_loai")
    private Integer type;
    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "khach_hang_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
    @OneToMany(mappedBy = "order")
    private List<OrderDetail> detailList;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Integer getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(Integer shippingFee) {
        this.shippingFee = shippingFee;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public List<OrderDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<OrderDetail> detailList) {
        this.detailList = detailList;
    }

    public int getSum() {
        return sum;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
