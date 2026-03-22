package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.VoucherRequest;
import com.beeshop.sd44.dto.response.VoucherResponse;
import com.beeshop.sd44.entity.Voucher;
import com.beeshop.sd44.repository.VoucherRepo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class VoucherService {

    private final VoucherRepo voucherRepo;

    public VoucherService(VoucherRepo voucherRepo) {
        this.voucherRepo = voucherRepo;
    }

    public List<VoucherResponse> getAll() {
        return voucherRepo.findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    public List<VoucherResponse> search(String keyword, Integer trangThai) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return voucherRepo.searchVouchers(kw, trangThai)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    public Voucher getById(UUID id) {
        return voucherRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy voucher"));
    }

    public VoucherResponse getResponseById(UUID id) {
        return buildResponse(getById(id));
    }

    public VoucherResponse create(VoucherRequest request) {
        validateBusiness(request, null);

        if (voucherRepo.existsByMa(request.getMa().trim())) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại");
        }

        Voucher voucher = new Voucher();
        mapRequestToEntity(voucher, request);
        return buildResponse(voucherRepo.save(voucher));
    }

    public VoucherResponse update(UUID id, VoucherRequest request) {
        Voucher voucher = getById(id);

        validateBusiness(request, id);

        if (voucherRepo.existsByMaAndIdNot(request.getMa().trim(), id)) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại");
        }

        mapRequestToEntity(voucher, request);
        return buildResponse(voucherRepo.save(voucher));
    }

    public void deactivate(UUID id) {
        Voucher voucher = getById(id);
        voucher.setTrangThai(0);
        voucherRepo.save(voucher);
    }

    public Voucher getByMa(String ma) {
        return voucherRepo.findByMa(ma.trim()).orElse(null);
    }

    public Voucher validateAndGet(String voucherCode, double subTotal) {
        Voucher voucher = getByMa(voucherCode);
        if (voucher == null) {
            throw new IllegalArgumentException("Mã giảm giá không tồn tại");
        }

        if (voucher.getTrangThai() == null || voucher.getTrangThai() != 1) {
            throw new IllegalArgumentException("Mã giảm giá không hoạt động");
        }

        Date now = new Date();

        if (voucher.getNgayBatDau() != null && now.before(voucher.getNgayBatDau())) {
            throw new IllegalArgumentException("Mã giảm giá chưa đến ngày sử dụng");
        }

        if (voucher.getNgayKetThuc() != null && now.after(voucher.getNgayKetThuc())) {
            throw new IllegalArgumentException("Mã giảm giá đã hết hạn");
        }

        if (voucher.getToiThieu() != null && subTotal < voucher.getToiThieu()) {
            throw new IllegalArgumentException(
                    "Đơn hàng phải tối thiểu " + voucher.getToiThieu() + " để sử dụng mã này"
            );
        }

        return voucher;
    }

    public double calculateDiscount(Voucher voucher, double subTotal) {
        double discount;

        if (voucher.getLoaiGiam() == 0) {
            discount = subTotal * voucher.getGiaTriGiam() / 100.0;
        } else {
            discount = voucher.getGiaTriGiam();
        }

        if (voucher.getToiDa() != null && voucher.getToiDa() > 0 && discount > voucher.getToiDa()) {
            discount = voucher.getToiDa();
        }

        if (discount > subTotal) {
            discount = subTotal;
        }

        return Math.max(discount, 0);
    }

    private void validateBusiness(VoucherRequest request, UUID id) {
        if (request.getLoaiGiam() == null || (request.getLoaiGiam() != 0 && request.getLoaiGiam() != 1)) {
            throw new IllegalArgumentException("Loại giảm chỉ được là 0 hoặc 1");
        }

        if (request.getNgayBatDau() != null && request.getNgayKetThuc() != null
                && request.getNgayBatDau().after(request.getNgayKetThuc())) {
            throw new IllegalArgumentException("Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }

        if (request.getLoaiGiam() == 0) {
            if (request.getGiaTriGiam() <= 0 || request.getGiaTriGiam() > 100) {
                throw new IllegalArgumentException("Voucher giảm phần trăm phải từ 1 đến 100");
            }
        }

        if (request.getLoaiGiam() == 1 && request.getGiaTriGiam() <= 0) {
            throw new IllegalArgumentException("Voucher giảm tiền phải lớn hơn 0");
        }

        if (request.getToiDa() != null && request.getToiDa() < 0) {
            throw new IllegalArgumentException("Giảm tối đa không được âm");
        }

        if (request.getToiThieu() != null && request.getToiThieu() < 0) {
            throw new IllegalArgumentException("Giá trị tối thiểu không được âm");
        }
    }

    private void mapRequestToEntity(Voucher voucher, VoucherRequest request) {
        voucher.setMa(request.getMa().trim());
        voucher.setTen(request.getTen().trim());
        voucher.setLoaiGiam(request.getLoaiGiam());
        voucher.setGiaTriGiam(request.getGiaTriGiam());
        voucher.setToiThieu(request.getToiThieu());
        voucher.setToiDa(request.getToiDa());
        voucher.setTrangThai(request.getTrangThai());
        voucher.setNgayBatDau(request.getNgayBatDau());
        voucher.setNgayKetThuc(request.getNgayKetThuc());
    }

    private VoucherResponse buildResponse(Voucher voucher) {
        VoucherResponse response = new VoucherResponse();
        response.setId(voucher.getId());
        response.setMa(voucher.getMa());
        response.setTen(voucher.getTen());
        response.setLoaiGiam(voucher.getLoaiGiam());
        response.setGiaTriGiam(voucher.getGiaTriGiam());
        response.setToiThieu(voucher.getToiThieu());
        response.setToiDa(voucher.getToiDa());
        response.setTrangThai(voucher.getTrangThai());
        response.setNgayBatDau(voucher.getNgayBatDau());
        response.setNgayKetThuc(voucher.getNgayKetThuc());
        return response;
    }
}