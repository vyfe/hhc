package com.vyfe.hhc.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.querydsl.core.BooleanBuilder;
import com.vyfe.hhc.api.BaseResponse;
import com.vyfe.hhc.api.HandRangeResponse;
import com.vyfe.hhc.domain.HandQueryService;
import com.vyfe.hhc.poker.type.GameType;
import com.vyfe.hhc.repo.GGSessionMsgRepo;
import com.vyfe.hhc.repo.QGGSessionMsg;
import com.vyfe.hhc.system.HhcException;
import com.vyfe.hhc.system.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * BaseController类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/28
 * Description: test RestController
 */
@RestController
@RequestMapping("/base")
@Validated
public class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
    @Autowired
    private GGSessionMsgRepo sessionMsgRepo;
    @Autowired
    private HandQueryService handQueryService;
    
    @RequestMapping(value = "/session/{id}")
    @ResponseBody
    public String getSession(@PathVariable("id") Long id) {
        // session信息输出
        return JsonUtils.toJson(sessionMsgRepo.findById(id).get());
    }
    
    @RequestMapping(value = "/analyze/handRange/{uid}")
    @ResponseBody
    public BaseResponse<HandRangeResponse> getHandRange(@PathVariable("uid") Integer uid,
                                                        @RequestParam(value = "gameType") Integer gameType,
                                                        @RequestParam(value = "buyInLevel", required = false, defaultValue = "0")
                                                                Integer buyInLevelGreater,
                                                        @RequestParam(value = "startDate") String startDate,
                                                        @RequestParam(value = "endDate", required = false)
                                                                    String endDate) throws HhcException {
        // 根据范围先找到对应的session，再根据session id找到hands并统计后，输出结果
        BooleanBuilder where = new BooleanBuilder();
        where.and(QGGSessionMsg.gGSessionMsg.uid.eq(uid))
                .and(QGGSessionMsg.gGSessionMsg.gameType.eq(GameType.getByCode(gameType)));
        if (buyInLevelGreater > 0) {
            where.and(QGGSessionMsg.gGSessionMsg.cashIn.gt(BigDecimal.valueOf(buyInLevelGreater)));
        }
        LocalDateTime start, end;
        start = LocalDate.parse(startDate).atStartOfDay();
        end = LocalDate.parse(endDate).atStartOfDay();
        // starDate小于当日
        if (!start.toLocalDate().isBefore(LocalDate.now())) {
            throw new HhcException("起始日期不可大于等于当日");
        }
        where.and(QGGSessionMsg.gGSessionMsg.startTime.between(start, end));
        LOGGER.info("where:{}", where.toString());
        return BaseResponse.withStatusAndInfoAndData(200, StringUtils.EMPTY,
                handQueryService.getHandStatistic(where));
    }
}
