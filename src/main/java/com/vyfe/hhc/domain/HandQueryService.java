package com.vyfe.hhc.domain;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vyfe.hhc.api.HandRangeResponse;
import com.vyfe.hhc.api.vo.HandStatistic;
import com.vyfe.hhc.parse.utils.RegexUtil;
import com.vyfe.hhc.poker.Card;
import com.vyfe.hhc.repo.GGHandMsg;
import com.vyfe.hhc.repo.GGHandMsgRepo;
import com.vyfe.hhc.repo.GGSessionMsg;
import com.vyfe.hhc.repo.GGSessionMsgRepo;
import com.vyfe.hhc.repo.QGGSessionMsg;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HandQueryService类.
 * <p>
 * User: chenyifei03
 * Date: 2023/3/1
 * Description:
 */
@Service
public class HandQueryService {
    @Autowired
    private GGHandMsgRepo handMsgRepo;
    @Autowired
    private GGSessionMsgRepo sessionMsgRepo;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
    @Transactional
    public HandRangeResponse getHandStatistic(BooleanBuilder where) {
        // query session范围
        List<GGSessionMsg> resList = new JPAQueryFactory(entityManagerFactory.createEntityManager())
                .selectFrom(QGGSessionMsg.gGSessionMsg)
                .where(where).orderBy(QGGSessionMsg.gGSessionMsg.id.desc()).fetch();
        List<Long> sids = resList.stream().map(GGSessionMsg::getId).collect(Collectors.toList());
        // query 所有手牌
        List<GGHandMsg> hands = handMsgRepo.findBySessionIdIn(sids);
        var res = new HandRangeResponse();
        var handMap = new HashMap<String, HandStatistic>();
        res.setTotalHands(hands.size());
        AtomicInteger winHands = new AtomicInteger();
        hands.forEach(hand -> {
            if (hand.isWinPot()) {
                winHands.getAndIncrement();
            }
            // 统计
            handMap.compute(calculatePosition(hand.getHeroHands()), (k, v) -> {
                if (v == null) {
                    v = new HandStatistic();
                    v.setX(Integer.parseInt(k.split("-")[0]));
                    v.setY(Integer.parseInt(k.split("-")[1]));
                }
                v.addTotal();
                if (hand.isVpip()) {
                    v.addVpip();
                }
                if (hand.isWinPot()) {
                    v.addWin();
                }
                return v;
            });
        });
        res.setWinRate(hands.size() == 0 ? 0L : RegexUtil.parseDoubleToScale((double) winHands.get() / (double) hands.size()));
        handMap.values().forEach(hs -> {
            hs.setWinRate(hs.getTotalHand() == 0 ? 0L : RegexUtil.parseDoubleToScale((double) hs.getWinHand() / (double) hs.getTotalHand()));
            hs.setVpipRate(hs.getTotalHand() == 0 ? 0L : RegexUtil.parseDoubleToScale((double) hs.getVpipHand() / (double) hs.getTotalHand()));
        });
        res.setHandStatistics(handMap.values().stream().toList());
        return res;
    }
    
    /**
     * 返回形如"x-y"的string，其中x代表从左上角AA为原点的13*13表格的横坐标，y为纵坐标
     * @param hands
     * @return
     */
    private String calculatePosition(Pair<Card, Card> hands) {
        int leftOrder = 13 - hands.getLeft().getNumber().getOrder();
        int rightOrder = 13 - hands.getRight().getNumber().getOrder();
        boolean suited = hands.getLeft().getDecor().equals(hands.getRight().getDecor());
        if (suited) {
            // 同花时x>y
            return leftOrder > rightOrder ? leftOrder + "-" + rightOrder : rightOrder + "-" + leftOrder;
        } else {
            // 不同花时 x<=y
            return leftOrder >= rightOrder ? rightOrder + "-" + leftOrder : leftOrder + "-" + rightOrder;
        }
    }
}
