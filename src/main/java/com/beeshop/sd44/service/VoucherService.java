package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.VoucherRequest;
import com.beeshop.sd44.dto.response.VoucherResponse;
import com.beeshop.sd44.entity.Voucher;
import com.beeshop.sd44.repository.VoucherRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VoucherService {
    private final VoucherRepo voucherRepo;

    public VoucherService(VoucherRepo voucherRepo) {
        this.voucherRepo = voucherRepo;
    }

    public List<VoucherResponse> getAll() {
        List<Voucher> list = voucherRepo.findAll();
        List<VoucherResponse> responses = new ArrayList<>();
        for (Voucher voucher : list) {
            responses.add(buildResponse(voucher, null));
        }
        return responses;
    }

    public List<VoucherResponse> search(String keyword, Integer trangThai, Double price) {
        String kw = (keyword != null && keyword.isBlank()) ? null : keyword;
        Integer finalTrangThai = (price != null) ? null : trangThai;
        List<Voucher> list = voucherRepo.searchVouchers(kw, finalTrangThai);
        List<VoucherResponse> responses = new ArrayList<>();
        for (Voucher voucher : list) {
            responses.add(buildResponse(voucher, price));
        }
        return responses;
    }

    public Voucher getById(UUID id) {
        Optional<Voucher> voucher = voucherRepo.findById(id);
        if (voucher.isPresent()) {
            return voucher.get();
        }
        return null;
    }

    public VoucherResponse getResponseById(UUID id) {
        Voucher voucher = getById(id);
        if (voucher == null) {
            return null;
        }
        return buildResponse(voucher, null);
    }

    public VoucherResponse create(VoucherRequest request) {
        Voucher voucher = new Voucher();
        return buildResponse(saveVoucher(voucher, request), null);
    }

    public VoucherResponse update(UUID id, VoucherRequest request) {
        Voucher voucher = getById(id);
        if (voucher == null) {
            return null;
        }
        return buildResponse(saveVoucher(voucher, request), null);
    }

    public void deactivate(UUID id) {
        Voucher voucher = getById(id);
        if (voucher == null) {
            return;
        }
        voucher.setTrangThai(0);
        voucherRepo.save(voucher);
    }

    // Tìm voucher theo mã
    public Voucher getByMa(String ma) {
        return voucherRepo.findByMa(ma).orElse(null);
    }

    /**
     * Validate voucher và trả về entity nếu hợp lệ.
     */
    public Voucher validateAndGet(String voucherCode, double subTotal) {
        Voucher voucher = getByMa(voucherCode);
        if (voucher == null) {
            throw new IllegalArgumentException("Ma giam gia khong ton tai");
        }
        if (voucher.getTrangThai() == null || voucher.getTrangThai() != 1) {
            throw new IllegalArgumentException("Ma giam gia khong hoat dong");
        }
        Date now = new Date();
        if (voucher.getNgayBatDau() != null && now.before(voucher.getNgayBatDau())) {
            throw new IllegalArgumentException("Ma giam gia chua den ngay su dung");
        }
        if (voucher.getNgayKetThuc() != null && now.after(voucher.getNgayKetThuc())) {
            throw new IllegalArgumentException("Ma giam gia da het han");
        }
        if (voucher.getToiThieu() != null && subTotal < voucher.getToiThieu()) {
            throw new IllegalArgumentException("Don hang phai toi thieu " + voucher.getToiThieu() + " de su dung ma nay");
        }
        return voucher;
    }

    /**
     * Tính số tiền giảm dựa trên voucher.
     */
    public double calculateDiscount(Voucher voucher, double subTotal) {
        double discount = 0;
        if (voucher.getLoaiGiam() == 0) {
            // Giảm theo %
            discount = subTotal * voucher.getGiaTriGiam() / 100.0;
        } else {
            // Giảm theo tiền cố định
            discount = voucher.getGiaTriGiam();
        }
        // Áp dụng giới hạn tối đa
        if (voucher.getToiDa() != null && voucher.getToiDa() > 0 && discount > voucher.getToiDa()) {
            discount = voucher.getToiDa();
        }
        // Không giảm quá tổng tiền hàng
        if (discount > subTotal) {
            discount = subTotal;
        }
        return discount;
    }

    private Voucher saveVoucher(Voucher voucher, VoucherRequest request) {
        voucher.setMa(request.getMa());
        voucher.setTen(request.getTen());
        voucher.setLoaiGiam(request.getLoaiGiam());
        voucher.setToiDa(request.getToiDa());
        voucher.setToiThieu(request.getToiThieu());
        voucher.setGiaTriGiam(request.getGiaTriGiam());
        voucher.setTrangThai(request.getTrangThai());
        voucher.setNgayBatDau(request.getNgayBatDau());
        voucher.setNgayKetThuc(request.getNgayKetThuc());
        return voucherRepo.save(voucher);
    }

    private VoucherResponse buildResponse(Voucher voucher, Double price) {
        VoucherResponse response = new VoucherResponse();
        response.setId(voucher.getId());
        response.setMa(voucher.getMa());
        response.setTen(voucher.getTen());
        response.setLoaiGiam(voucher.getLoaiGiam());
        response.setToiDa(voucher.getToiDa());
        response.setToiThieu(voucher.getToiThieu());
        response.setGiaTriGiam(voucher.getGiaTriGiam());
        response.setTrangThai(voucher.getTrangThai());
        response.setNgayBatDau(voucher.getNgayBatDau());
        response.setNgayKetThuc(voucher.getNgayKetThuc());
        
        if (price != null) {
            boolean isValid = true;
            if (voucher.getTrangThai() == null || voucher.getTrangThai() == 0) {
                isValid = false;
            } else if (voucher.getToiThieu() != null && price < voucher.getToiThieu()) {
                isValid = false;
            }
            response.setValid(isValid);
        } else {
            response.setValid(voucher.getTrangThai() != null && voucher.getTrangThai() == 1);
        }
        
        return response;
    }
}
