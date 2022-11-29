package com.shz.service;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.entity.Voucher;

import java.util.List;

public interface VoucherService extends IService<Voucher> {

    List<Voucher> getAllVouchers();

    Pair queryMyVoucher();



}
