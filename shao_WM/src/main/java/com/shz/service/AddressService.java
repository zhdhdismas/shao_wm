package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.entity.Address;

public interface AddressService extends IService<Address> {
    boolean selAddressExist(String address1, Integer uid);

    boolean addOneAddress(String address1);
}
