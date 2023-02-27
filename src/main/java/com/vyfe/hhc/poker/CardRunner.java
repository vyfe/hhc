package com.vyfe.hhc.poker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.vyfe.hhc.poker.runner.BestCombo;
import com.vyfe.hhc.poker.runner.HoldemComboType;
import com.vyfe.hhc.poker.type.CardNumber;
import com.vyfe.hhc.poker.type.Decor;
import com.vyfe.hhc.system.HhcException;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CardRunner类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/25
 * Description: 跑马器；用于比较牌面，得出大小的逻辑和工具
 */
public class CardRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CardRunner.class);
    /**
     * 从公共牌和手牌中得到牌型
     * @param publicCards 公共牌，至少3个,至多5个
     * @param handCards 手牌对，至少2个
     * @return
     */
    public BestCombo getComboByCards(Card[] publicCards, Card[] handCards) throws HhcException {
        if (publicCards.length < 3 || publicCards.length > 5 || handCards.length != 2) {
            throw new HhcException("card number not right, public:" + publicCards.length + ", hand:" +
                    handCards.length);
        }
        var combo = new BestCombo();
        // 按级别自上至下倒推，得到最大牌型的组合
        
        // 检测分支:
        // cardMap内按花色排序
        Map<CardNumber, List<Card>> cardMap = Stream.concat(Stream.of(publicCards), Stream.of(handCards))
                .collect(Collectors.groupingBy(Card::getNumber, HashMap::new,
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.stream().sorted(Comparator.comparingInt(
                                        card -> card.getDecor().getOrder())).collect(Collectors.toList())
                        )));
        // 1.(是否有重复牌)四条->葫芦->三条->两对->一对
        if (cardMap.values().stream().anyMatch(v -> v.size() > 1)) {
            var counter = new RepeatCounter();
            cardMap.forEach((num, list) -> counter.compareAndAdjust(num, list.size()));
            // 得到counter后log一下
            LOGGER.debug("counter result:{}", counter);
            // 根据counter属性判断牌Combo类型，计算
            if (counter.getPriorRepeat().getValue() == 4) {
                // 四条
                combo.setType(HoldemComboType.FOUR);
                combo.setDim(new Integer[]{counter.getPriorRepeat().getKey().getOrder()});
                List<Card> comboCards = new ArrayList<>(cardMap.get(counter.getPriorRepeat().getKey()));
                // piece取最大高张
                if (counter.getMinorRepeat().getValue() == 1) {
                    // minor为1(剩余牌中无相同大小)时取minor的order(此时minor已经是最大number)
                    combo.setPieceDim(new Integer[]{counter.getMinorRepeat().getKey().getOrder()});
                    comboCards.add(cardMap.get(counter.getMinorRepeat().getKey()).get(0));
                } else {
                    // 否则取4条以外的牌中的最大CardNumber
                    cardMap.keySet().stream().filter(num -> !num.equals(counter.getPriorRepeat().getKey()))
                            .max(Comparator.comparing(CardNumber::getOrder)).ifPresent(num -> {
                        combo.setPieceDim(new Integer[]{num.getOrder()});
                        comboCards.add(cardMap.get(num).get(0));
                    });
                }
                combo.setComboCards(comboCards.toArray(new Card[0]));
            } else if (counter.getPriorRepeat().getValue() == 3 && counter.getMinorRepeat().getValue() >= 2) {
                // 葫芦
                combo.setType(HoldemComboType.FULLHOUSE);
                combo.setDim(new Integer[]{counter.getPriorRepeat().getKey().getOrder(),
                        counter.getMinorRepeat().getKey().getOrder()});
                List<Card> comboCards = new ArrayList<>(cardMap.get(counter.getPriorRepeat().getKey()));
                comboCards.addAll(cardMap.get(counter.getMinorRepeat().getKey())
                        .subList(0, 2));
                combo.setComboCards(comboCards.toArray(new Card[0]));
            } else if (counter.getPriorRepeat().getValue() == 3) {
                // 三条
                combo.setType(HoldemComboType.SET);
                combo.setDim(new Integer[]{counter.getPriorRepeat().getKey().getOrder()});
                // 从prior(肯定为3)和minor(肯定为1)以外选出max的牌
                List<Card> comboCards = new ArrayList<>(cardMap.get(counter.getPriorRepeat().getKey()));
                cardMap.keySet().stream().filter(num -> !num.equals(counter.getPriorRepeat().getKey()) &&
                        !num.equals(counter.getMinorRepeat().getKey()))
                        .max(Comparator.comparing(CardNumber::getOrder)).ifPresent(num -> {
                    combo.setPieceDim(new Integer[]{counter.getMinorRepeat().getKey().getOrder(), num.getOrder()});
                    // minor + 两项以外的max-card num
                    comboCards.add(cardMap.get(counter.getMinorRepeat().getKey()).get(0));
                    comboCards.add(cardMap.get(num).get(0));
                });
                combo.setComboCards(comboCards.toArray(new Card[0]));
            } else if (counter.getPriorRepeat().getValue() == 2 && counter.getMinorRepeat().getValue() == 2) {
                // 两对
                combo.setType(HoldemComboType.TWO_PAIR);
                combo.setDim(new Integer[]{counter.getPriorRepeat().getKey().getOrder(),
                        counter.getMinorRepeat().getKey().getOrder()});
                List<Card> comboCards = new ArrayList<>(cardMap.get(counter.getPriorRepeat().getKey()));
                comboCards.addAll(cardMap.get(counter.getMinorRepeat().getKey()));
                // 选出一张max
                cardMap.keySet().stream().filter(num -> !num.equals(counter.getPriorRepeat().getKey()) &&
                        !num.equals(counter.getMinorRepeat().getKey()))
                        .max(Comparator.comparing(CardNumber::getOrder)).ifPresent(num -> {
                    combo.setPieceDim(new Integer[]{num.getOrder()});
                    comboCards.add(cardMap.get(num).get(0));
                });
                combo.setComboCards(comboCards.toArray(new Card[0]));
            } else {
                // 一对
                combo.setType(HoldemComboType.PAIR);
                combo.setDim(new Integer[]{counter.getPriorRepeat().getKey().getOrder()});
                List<Card> comboCards = new ArrayList<>(cardMap.get(counter.getPriorRepeat().getKey()));
                List<Integer> pieceDim = new ArrayList<>();
                pieceDim.add(counter.getMinorRepeat().getKey().getOrder());
                comboCards.addAll(cardMap.get(counter.getMinorRepeat().getKey()));
                // 除minor外再按order倒排选出Top2
                List<CardNumber> cardSort = cardMap.keySet().stream()
                        .filter(num -> !num.equals(counter.getPriorRepeat().getKey()) &&
                                !num.equals(counter.getMinorRepeat().getKey()))
                        .sorted(Comparator.comparing(CardNumber::getOrder).reversed()).collect(Collectors.toList());
                
                for (int i = 0; i < 2 && i < cardSort.size(); i++) {
                    pieceDim.add(cardSort.get(i).getOrder());
                    comboCards.addAll(cardMap.get(cardSort.get(i)));
                }
                combo.setPieceDim(pieceDim.toArray(new Integer[0]));
                combo.setComboCards(comboCards.toArray(new Card[0]));
            }
        } else {
            // 此分支内无重复牌，判断是否满足花/顺，否则就是高张
            Map<Decor, List<Card>> decorMap = Stream.concat(Stream.of(publicCards), Stream.of(handCards))
                    .collect(Collectors.groupingBy(Card::getDecor));
            // 若为同花，有且只有可能为一种花色（需要至少5张同花色牌，最多只有7张）
            Optional<Decor> decorFlush = decorMap.values().stream().filter(v -> v.size() >= 5).findFirst()
                    .map(list -> list.get(0).getDecor());
            // 若非空，则为顺子的最大牌（范围为5~13）
            Optional<CardNumber> straightMax = checkStraight(cardMap.keySet());
            if (decorFlush.isPresent()) {
                Optional<CardNumber> flushStraight = checkStraight(decorMap.get(decorFlush.get()).stream()
                    .map(Card::getNumber).collect(Collectors.toSet()));
                if (flushStraight.isPresent()) {
                    // 同花成顺，则为同花顺
                    combo.setType(HoldemComboType.STRAIGHT_FLUSH);
                    combo.setDim(new Integer[]{flushStraight.get().getOrder()});
                    // 从顺子头递减寻找Card
                    var comboCards = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        comboCards.add(cardMap.get(CardNumber.getByOrder(flushStraight.get().getOrder() - i)).get(0));
                    }
                    combo.setComboCards(comboCards.toArray(new Card[0]));
                } else {
                    // 不成顺则为同花
                    combo.setType(HoldemComboType.FLUSH);
                    // 找到剩余的4个piece:排序后拿取前5张
                    List<CardNumber> cardSort = decorMap.get(decorFlush.get()).stream().map(Card::getNumber)
                            .sorted(Comparator.comparing(CardNumber::getOrder).reversed())
                            .collect(Collectors.toList());
                    combo.setDim(new Integer[]{cardSort.get(0).getOrder()});
                    combo.setPieceDim(new Integer[]{cardSort.get(1).getOrder(), cardSort.get(2).getOrder(),
                            cardSort.get(3).getOrder(), cardSort.get(4).getOrder()});
                    // 寻找比较麻烦，直接按花色生成5张牌
                    var comboCards = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        comboCards.add(cardMap.get(cardSort.get(i)).get(0));
                    }
                    combo.setComboCards(comboCards.toArray(new Card[0]));
                }
            } else if (straightMax.isPresent()) {
                // 顺子
                combo.setType(HoldemComboType.STRAIGHT);
                combo.setDim(new Integer[]{straightMax.get().getOrder()});
                // 从顺子头递减寻找Card
                var comboCards = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    comboCards.add(cardMap.get(CardNumber.getByOrder(straightMax.get().getOrder() - i)).get(0));
                }
                combo.setComboCards(comboCards.toArray(new Card[0]));
            } else {
                // 高张
                combo.setType(HoldemComboType.HIGH_CARD);
                // cardMap的key排序后取前5张
                List<CardNumber> cardSort = cardMap.keySet().stream()
                        .sorted(Comparator.comparing(CardNumber::getOrder).reversed()).collect(Collectors.toList());
                combo.setDim(new Integer[]{cardSort.get(0).getOrder()});
                combo.setPieceDim(new Integer[]{cardSort.get(1).getOrder(), cardSort.get(2).getOrder(),
                        cardSort.get(3).getOrder(), cardSort.get(4).getOrder()});
                // 取前5张入即可
                var comboCards = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    comboCards.add(cardMap.get(cardSort.get(i)).get(0));
                }
                combo.setComboCards(comboCards.toArray(new Card[0]));
            }
        }
        LOGGER.debug("combo result:{}", combo);
        return combo;
    }
    
    @Data
    @ToString
    static class RepeatCounter {
        private Pair<CardNumber, Integer> priorRepeat;
    
        private Pair<CardNumber, Integer> minorRepeat;
    
        /**
         * 判断该重复统计记录是否比已有的重复记录更大（如3个6>2个7，2个3<2个5）
         * @param num 牌大小
         * @param count 重复数
         */
        public void compareAndAdjust(CardNumber num, int count) {
            // 已有一组minor时，比大小，小的放minor
            Pair<CardNumber, Integer> secondPair = Pair.of(num, count);
            if (minorRepeat == null ) {
                minorRepeat = secondPair;
            } else if (priorRepeat == null) {
                // 已有一组minor时，比大小，小的放minor
                if (compareRepeat(secondPair, this.getMinorRepeat())) {
                    priorRepeat = secondPair;
                } else {
                    priorRepeat = minorRepeat;
                    minorRepeat = secondPair;
                }
            } else {
                // 已有两组时，先跟minor比大小，若大于minor再跟prior比大小
                if (compareRepeat(secondPair, this.getMinorRepeat())) {
                    if (compareRepeat(secondPair, this.getPriorRepeat())) {
                        minorRepeat = priorRepeat;
                        priorRepeat = secondPair;
                    } else {
                        minorRepeat = secondPair;
                    }
                }
            }
        }
    
        /**
         * 重复统计记录间的比较方法, a>b时为true；
         * （如3个6>2个7，2个3<2个5）
         * @return
         */
        private boolean compareRepeat(Pair<CardNumber, Integer> a, Pair<CardNumber, Integer> b) {
            if (a.getValue().equals(b.getValue())) {
                return a.getKey().getOrder() > b.getKey().getOrder();
            } else {
                return a.getValue() > b.getValue();
            }
        }
    }
    
    /**
     * 检测一组number（来自同花色集合、公共牌+手牌集合等）中是否存在顺子
     * @param cardsOnBoard 给定的牌面集合
     * @return
     */
    private Optional<CardNumber> checkStraight(Set<CardNumber> cardsOnBoard) {
        // 若非空，则为顺子的最大牌（范围为6~14）
        // 底顺要特殊处理
        Optional<CardNumber> straightMax = Optional.empty();
        int seqCount = 0;
        for (CardNumber num : CardNumber.getOrderList()) {
            if (cardsOnBoard.contains(num)) {
                seqCount++;
                if (seqCount >= 5) {
                    straightMax = Optional.of(num);
                }
            } else {
                seqCount = 0;
            }
        }
        // 若存在A-5的特例
        if (straightMax.isEmpty() && cardsOnBoard.containsAll(
                Lists.newArrayList(CardNumber.ACE, CardNumber.TWO, CardNumber.THREE,
                        CardNumber.FOUR, CardNumber.FIVE))) {
            straightMax = Optional.of(CardNumber.FIVE);
        }
        return straightMax;
    }
}
