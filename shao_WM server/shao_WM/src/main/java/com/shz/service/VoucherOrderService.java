package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.dto.RespBean;
import com.shz.entity.VoucherOrder;

import java.util.List;

public interface VoucherOrderService extends IService<VoucherOrder> {
    RespBean seckillVoucher(Long voucherId);


}
