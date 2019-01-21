package com.connext.wms.api;

import com.connext.wms.api.dto.InRepertoryDetailDTO;
import com.connext.wms.api.util.EntityAndDto;
import com.connext.wms.entity.InRepertory;
import com.connext.wms.service.InRepertoryService;
import com.connext.wms.util.AES;
import com.connext.wms.util.Constant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author: Marcus
 * @Date: 2019/1/7 10:44
 * @Version 1.0
 */
@RestController
@RequestMapping("/api")
public class InRepertoryApi {
    private final InRepertoryService inRepertoryService;
    private final Constant constant;
    private ObjectMapper objectMapper;
    private final EntityAndDto entityAndDto;

    @Autowired
    public InRepertoryApi(InRepertoryService inRepertoryService, Constant constant, ObjectMapper objectMapper, EntityAndDto entityAndDto) {
        this.inRepertoryService = inRepertoryService;
        this.constant = constant;
        this.objectMapper = objectMapper;
        this.entityAndDto = entityAndDto;
    }

    @PostMapping("/inRepertoryOrder")
    public void inRepertoryOrder(@RequestParam String tokens,
                                 @RequestParam String inRepoId,
                                 @RequestParam String orderId,
                                 @RequestParam String channelId,
                                 @RequestParam String expressId,
                                 @RequestParam String expressCompany,
                                 @RequestParam String detailDTOS
    ) throws IOException {
        //token校验
        if (Objects.equals(AES.AESDncode(constant.TOKENS, tokens), inRepoId) && validData(inRepoId, orderId, channelId, expressId, expressCompany, detailDTOS)) {
            List<InRepertoryDetailDTO> repertoryDetailDTOS = objectMapper.readValue(detailDTOS, new TypeReference<List<InRepertoryDetailDTO>>() {
            });
            Date nowTime = new Date();
            InRepertory inRepertory = new InRepertory(inRepoId, orderId, channelId, expressId, expressCompany, constant.INIT_STATUS, constant.SYNC_FALSE_STATES, constant.RECEIVING_REPERTORY, nowTime, constant.REVISER, nowTime, entityAndDto.toEntity(inRepoId, repertoryDetailDTOS));
            inRepertoryService.initInRepertory(inRepertory);
        } else {
            throw new IOException("tokens error or data illegal");
        }
    }

    private boolean validData(String inRepoId, String orderId, String channelId, String expressId, String expressCompany, String detailDTOS) {
        if (inRepoId.length() <= 10 && orderId.length() <= 30 && channelId.length() <= 30 && expressId.length() <= 30 && expressCompany.length() <= 20 && detailDTOS.length() >= 2) {
            return true;
        }
        return false;
    }
}
