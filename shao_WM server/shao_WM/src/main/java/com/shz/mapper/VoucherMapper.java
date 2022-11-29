package com.shz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shz.entity.Voucher;

import java.util.List;

public interface VoucherMapper extends BaseMapper<Voucher> {
    List<Voucher> getMyVoucher(int uid);
}
