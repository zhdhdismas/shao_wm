package com.shz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.entity.MailLog;
import com.shz.mapper.MailLogMapper;
import com.shz.service.MailLogService;
import org.springframework.stereotype.Service;

@Service
public class MailLogServiceImpl extends ServiceImpl<MailLogMapper, MailLog> implements MailLogService {

}
