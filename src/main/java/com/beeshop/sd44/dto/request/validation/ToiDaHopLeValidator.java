package com.beeshop.sd44.dto.request.validation;

import com.beeshop.sd44.dto.request.VoucherRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ToiDaHopLeValidator implements ConstraintValidator<ToiDaHopLe, VoucherRequest> {

    @Override
    public boolean isValid(VoucherRequest req, ConstraintValidatorContext ctx) {
        if (req == null) return true;

        boolean valid = true;
        ctx.disableDefaultConstraintViolation();

        // toiDa phải >= toiThieu (nếu toiDa được cung cấp)
        if (req.getToiDa() != null && req.getToiThieu() != null
                && req.getToiDa() > 0 && req.getToiDa() < req.getToiThieu()) {
            ctx.buildConstraintViolationWithTemplate("Giảm tối đa không được nhỏ hơn giá trị tối thiểu")
                    .addPropertyNode("toiDa")
                    .addConstraintViolation();
            valid = false;
        }

        // ngayKetThuc phải sau ngayBatDau
        if (req.getNgayBatDau() != null && req.getNgayKetThuc() != null
                && !req.getNgayKetThuc().after(req.getNgayBatDau())) {
            ctx.buildConstraintViolationWithTemplate("Ngày kết thúc phải sau ngày bắt đầu")
                    .addPropertyNode("ngayKetThuc")
                    .addConstraintViolation();
            valid = false;
        }

        // Nếu loại giảm theo % thì giaTriGiam tối đa 100
        if (req.getLoaiGiam() != null && req.getLoaiGiam() == 0
                && req.getGiaTriGiam() != null && req.getGiaTriGiam() > 100) {
            ctx.buildConstraintViolationWithTemplate("Giảm theo % không được vượt quá 100")
                    .addPropertyNode("giaTriGiam")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}

