package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.VoucherRequest;
import com.beeshop.sd44.dto.response.VoucherResponse;
import com.beeshop.sd44.entity.Voucher;
import com.beeshop.sd44.repository.VoucherRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
            responses.add(buildResponse(voucher));
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
        return buildResponse(voucher);
    }

    public VoucherResponse create(VoucherRequest request) {
        Voucher voucher = new Voucher();
        return buildResponse(saveVoucher(voucher, request));
    }

    public VoucherResponse update(UUID id, VoucherRequest request) {
        Voucher voucher = getById(id);
        if (voucher == null) {
            return null;
        }
        return buildResponse(saveVoucher(voucher, request));
    }

    public void deactivate(UUID id) {
        Voucher voucher = getById(id);
        if (voucher == null) {
            return;
        }
        voucher.setTrangThai(0);
        voucherRepo.save(voucher);
    }

    private Voucher saveVoucher(Voucher voucher, VoucherRequest request) {
        voucher.setMa(request.getMa());
        voucher.setTen(request.getTen());
        voucher.setLoaiGiam(request.getLoaiGiam());
        voucher.setToiDa(request.getToiDa());
        voucher.setTrangThai(request.getTrangThai());
        voucher.setNgayBatDau(request.getNgayBatDau());
        voucher.setNgayKetThuc(request.getNgayKetThuc());
        return voucherRepo.save(voucher);
    }

    private VoucherResponse buildResponse(Voucher voucher) {
        VoucherResponse response = new VoucherResponse();
        response.setId(voucher.getId());
        response.setMa(voucher.getMa());
        response.setTen(voucher.getTen());
        response.setLoaiGiam(voucher.getLoaiGiam());
        response.setToiDa(voucher.getToiDa());
        response.setTrangThai(voucher.getTrangThai());
        response.setNgayBatDau(voucher.getNgayBatDau());
        response.setNgayKetThuc(voucher.getNgayKetThuc());
        return response;
    }
}
