package com.shz.service.impl;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.entity.Voucher;
import com.shz.mapper.VoucherMapper;
import com.shz.service.VoucherService;
import com.shz.utils.OSSUtil;
import com.shz.utils.UserUtil;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    @Override
    public List<Voucher> getAllVouchers() {
        List<Voucher> list = query().list();
        for (Voucher voucher : list) {
            String imgUrls="http://" + OSSUtil.SUFFER_URL + voucher.getImgUrl();
            String scaleAfter = imgUrls + "?x-oss-process=image/resize,m_fixed,w_650,h_400";
            voucher.setImgUrl(scaleAfter);
        }
        return list;
    }

    @Override
    public Pair queryMyVoucher() {
        int uid = UserUtil.getCurrentUser().getUid();
        List<Voucher> res=baseMapper.getMyVoucher(uid);
        List<Voucher> ans=new ArrayList<>();
        Timestamp timestamp=new Timestamp(System.currentTimeMillis());
        List<Integer> vids=new ArrayList<>();
        for (Voucher re : res) {
            if(timestamp.after(re.getDeadTime())){
                vids.add(re.getVid());
                continue;
            }
            ans.add(re);
        }
        return new Pair(ans,vids);
    }

}
