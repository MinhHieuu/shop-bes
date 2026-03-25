package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.VoucherRequest;
import com.beeshop.sd44.dto.response.VoucherResponse;
import com.beeshop.sd44.entity.Order;
import com.beeshop.sd44.entity.Voucher;
import com.beeshop.sd44.repository.VoucherRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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
        List<Voucher> list = voucherRepo.findAll();
        List<VoucherResponse> responses = new ArrayList<>();
        for (Voucher voucher : list) {
            responses.add(buildResponse(voucher));
        }
        return responses;
    }

    public List<VoucherResponse> search(String keyword, Integer trangThai) {
        String kw = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        List<Voucher> list = voucherRepo.searchVouchers(kw, trangThai);

        List<VoucherResponse> responses = new ArrayList<>();
        for (Voucher voucher : list) {
            responses.add(buildResponse(voucher));
        }
        return responses;
    }

    public Voucher getById(UUID id) {
        return voucherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));
    }

    public VoucherResponse getResponseById(UUID id) {
        return buildResponse(getById(id));
    }

    public VoucherResponse create(VoucherRequest request) {
        validateVoucherRequest(request);

        if (isCodeExists(request.getMa(), null)) {
            throw new RuntimeException("Mã voucher đã tồn tại");
        }

        Voucher voucher = new Voucher();
        mapRequestToVoucher(voucher, request);

        return buildResponse(voucherRepo.save(voucher));
    }

    public VoucherResponse update(UUID id, VoucherRequest request) {
        validateVoucherRequest(request);

        Voucher voucher = getById(id);

        if (isCodeExists(request.getMa(), id)) {
            throw new RuntimeException("Mã voucher đã tồn tại");
        }

        mapRequestToVoucher(voucher, request);

        return buildResponse(voucherRepo.save(voucher));
    }

    public void deactivate(UUID id) {
        Voucher voucher = getById(id);
        voucher.setTrangThai(0);
        voucherRepo.save(voucher);
    }

    public Voucher getByMa(String ma) {
        if (ma == null || ma.trim().isEmpty()) {
            return null;
        }
        return voucherRepo.findByMa(ma.trim()).orElse(null);
    }

    public Voucher validateAndGet(String voucherCode, double subTotal) {
        Voucher voucher = getByMa(voucherCode);

        if (voucher == null) {
            throw new RuntimeException("Mã giảm giá không tồn tại");
        }

        validateVoucherForOrder(voucher, subTotal);
        return voucher;
    }

    public double calculateDiscount(Voucher voucher, double subTotal) {
        double discount = 0;

        if (voucher.getLoaiGiam() == null) {
            return 0;
        }

        if (voucher.getLoaiGiam() == 0) {
            discount = subTotal * voucher.getGiaTriGiam() / 100.0;
        } else if (voucher.getLoaiGiam() == 1) {
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

    // =========================
    // CHỨC NĂNG MỚI
    // =========================

    public List<VoucherResponse> getActiveVouchers() {
        List<Voucher> all = voucherRepo.findAll();
        List<VoucherResponse> responses = new ArrayList<>();

        for (Voucher voucher : all) {
            if (getRealStatus(voucher) == 1) {
                responses.add(buildResponse(voucher));
            }
        }

        return responses;
    }

    public List<VoucherResponse> getAvailableVouchersForOrder(double subTotal) {
        List<Voucher> all = voucherRepo.findAll();
        List<VoucherResponse> responses = new ArrayList<>();

        for (Voucher voucher : all) {
            if (canApplyVoucher(voucher, subTotal)) {
                responses.add(buildResponse(voucher));
            }
        }

        return responses;
    }

    public VoucherResponse getBestVoucherForOrder(double subTotal) {
        List<Voucher> all = voucherRepo.findAll();

        Voucher bestVoucher = all.stream()
                .filter(v -> canApplyVoucher(v, subTotal))
                .max(Comparator.comparingDouble(v -> calculateDiscount(v, subTotal)))
                .orElse(null);

        if (bestVoucher == null) {
            return null;
        }

        return buildResponse(bestVoucher);
    }

    public boolean canApplyVoucher(Voucher voucher, double subTotal) {
        try {
            validateVoucherForOrder(voucher, subTotal);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public int getUsageCount(UUID voucherId) {
        Voucher voucher = getById(voucherId);
        return countUsedOrders(voucher);
    }

    public String getVoucherDisplayStatus(UUID voucherId) {
        Voucher voucher = getById(voucherId);
        return getDisplayStatus(voucher);
    }

    public int getVoucherRealStatus(UUID voucherId) {
        Voucher voucher = getById(voucherId);
        return getRealStatus(voucher);
    }

    // =========================
    // PRIVATE
    // =========================

    private void validateVoucherRequest(VoucherRequest request) {
        if (request == null) {
            throw new RuntimeException("Dữ liệu voucher không được để trống");
        }

        if (request.getMa() == null || request.getMa().trim().isEmpty()) {
            throw new RuntimeException("Mã voucher không được để trống");
        }

        if (request.getTen() == null || request.getTen().trim().isEmpty()) {
            throw new RuntimeException("Tên voucher không được để trống");
        }

        if (request.getLoaiGiam() == null || (request.getLoaiGiam() != 0 && request.getLoaiGiam() != 1)) {
            throw new RuntimeException("Loại giảm phải là 0 hoặc 1");
        }

        if (request.getGiaTriGiam() == null || request.getGiaTriGiam() <= 0) {
            throw new RuntimeException("Giá trị giảm phải lớn hơn 0");
        }

        if (request.getLoaiGiam() == 0 && request.getGiaTriGiam() > 100) {
            throw new RuntimeException("Voucher phần trăm chỉ được từ 1 đến 100");
        }

        if (request.getToiThieu() == null || request.getToiThieu() < 0) {
            throw new RuntimeException("Giá trị tối thiểu phải lớn hơn hoặc bằng 0");
        }

        if (request.getToiDa() != null && request.getToiDa() < 0) {
            throw new RuntimeException("Giá trị giảm tối đa không được âm");
        }

        if (request.getNgayBatDau() != null && request.getNgayKetThuc() != null
                && request.getNgayBatDau().after(request.getNgayKetThuc())) {
            throw new RuntimeException("Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }
    }

    private void validateVoucherForOrder(Voucher voucher, double subTotal) {
        if (voucher == null) {
            throw new RuntimeException("Voucher không tồn tại");
        }

        if (voucher.getTrangThai() == null || voucher.getTrangThai() != 1) {
            throw new RuntimeException("Voucher đang bị khóa");
        }

        Date now = new Date();

        if (voucher.getNgayBatDau() != null && now.before(voucher.getNgayBatDau())) {
            throw new RuntimeException("Voucher chưa đến ngày bắt đầu");
        }

        if (voucher.getNgayKetThuc() != null && now.after(voucher.getNgayKetThuc())) {
            throw new RuntimeException("Voucher đã hết hạn");
        }

        if (voucher.getToiThieu() != null && subTotal < voucher.getToiThieu()) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu");
        }
    }

    private boolean isCodeExists(String ma, UUID currentId) {
        if (ma == null || ma.trim().isEmpty()) {
            return false;
        }

        Voucher existed = voucherRepo.findByMa(ma.trim()).orElse(null);
        if (existed == null) {
            return false;
        }

        if (currentId == null) {
            return true;
        }

        return !existed.getId().equals(currentId);
    }

    private void mapRequestToVoucher(Voucher voucher, VoucherRequest request) {
        voucher.setMa(request.getMa().trim());
        voucher.setTen(request.getTen().trim());
        voucher.setLoaiGiam(request.getLoaiGiam());
        voucher.setToiDa(request.getToiDa());
        voucher.setToiThieu(request.getToiThieu());
        voucher.setGiaTriGiam(request.getGiaTriGiam());
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
        response.setToiDa(voucher.getToiDa());
        response.setToiThieu(voucher.getToiThieu());
        response.setGiaTriGiam(voucher.getGiaTriGiam());
        response.setTrangThai(voucher.getTrangThai());
        response.setNgayBatDau(voucher.getNgayBatDau());
        response.setNgayKetThuc(voucher.getNgayKetThuc());
        return response;
    }

    private int getRealStatus(Voucher voucher) {
        Date now = new Date();

        if (voucher.getTrangThai() == null || voucher.getTrangThai() == 0) {
            return 0; // bị khóa
        }

        if (voucher.getNgayBatDau() != null && now.before(voucher.getNgayBatDau())) {
            return 2; // sắp diễn ra
        }

        if (voucher.getNgayKetThuc() != null && now.after(voucher.getNgayKetThuc())) {
            return 3; // hết hạn
        }

        return 1; // đang hoạt động
    }

    private String getDisplayStatus(Voucher voucher) {
        int status = getRealStatus(voucher);

        switch (status) {
            case 0:
                return "Bị khóa";
            case 1:
                return "Đang hoạt động";
            case 2:
                return "Sắp diễn ra";
            case 3:
                return "Hết hạn";
            default:
                return "Không xác định";
        }
    }

    private int countUsedOrders(Voucher voucher) {
        if (voucher.getListOrder() == null) {
            return 0;
        }

        int count = 0;
        for (Order order : voucher.getListOrder()) {
            if (order != null) {
                count++;
            }
        }
        return count;
    }
}