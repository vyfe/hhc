package com.vyfe.hhc.decoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vyfe.hhc.decoder.utils.RegexUtil;
import com.vyfe.hhc.poker.ActionLine;
import com.vyfe.hhc.poker.Card;
import com.vyfe.hhc.poker.type.GameType;
import com.vyfe.hhc.repo.GGHandMsg;
import com.vyfe.hhc.poker.constant.HoldemConstant;
import com.vyfe.hhc.poker.type.ActionType;
import com.vyfe.hhc.repo.vo.ActionLinesVo;
import com.vyfe.hhc.repo.vo.ActionVo;
import com.vyfe.hhc.repo.vo.BoardsVo;
import com.vyfe.hhc.system.util.JsonUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * GGCashDecoder类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description: GG战绩解析
 */
@Component
public class GGCashDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(GGCashDecoder.class);
    
    /**
     * 解析为一手牌:GG cash
     * @param descString
     * @param formerChip 前一手的余额，用于计算提现/买入
     * @param sessionId
     * @return
     */
    public GGHandMsg parseCashHand(List<String> descString, BigDecimal formerChip,
                                   Long sessionId) {
        var hand = new GGHandMsg();
        if (CollectionUtils.isEmpty(descString)) {
            return hand;
        }
        int i = 0;
        try {
            // id、大小盲、时间
            hand.setHandId(descString.get(i).split("#")[1].split(":")[0]);
            hand.setHandTime(LocalDateTime.parse(descString.get(i).split("-")[1].trim(),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
            String sbBb = descString.get(i).split("[)(]")[1];
            hand.setBbSize(RegexUtil.cashStringToDecimal(sbBb.split("/")[1]));
            hand.setSbSize(RegexUtil.cashStringToDecimal(sbBb.split("/")[0]));
            hand.setSessionId(sessionId);
            // 第二行: button位,
            i++;
            int buttonPos = Integer.parseInt(descString.get(i).split("#")[1].split(" ")[0]);
            i++;
            // 第3~n行: Seat 开头，有几行就是几人; Hero为自己，括号内$符号后为现金量
            Map<String, BigDecimal> uidAndChip = new HashMap<>();
            var heroPos = 0;
            for (; descString.get(i).split(" ")[0].equals("Seat"); i++) {
                var uid = descString.get(i).split(" ")[2].toLowerCase();
                uidAndChip.put(uid, RegexUtil.cashStringToDecimal(
                        descString.get(i).split("\\(")[1].split(" ")[0]));
                if (uid.equalsIgnoreCase(HoldemConstant.PLAYER_WORD_GG)) {
                    var posStr = descString.get(i).split(" ")[1];
                    heroPos = Integer.parseInt(posStr.substring(0, posStr.length() - 1));
                }
            }
            hand.setChips(uidAndChip.get(HoldemConstant.PLAYER_WORD_GG));
            // 局间筹码变化(发生了买入/提现)
            hand.setChipsSupply(hand.getChips().subtract(formerChip));
            // 计算Hero相对按钮的位置
            hand.setChairs(uidAndChip.size());
            if (buttonPos < heroPos ) {
                hand.setPosition(heroPos - (buttonPos + 1));
            } else {
                hand.setPosition(hand.getChairs() + (buttonPos - 1) - heroPos);
            }
            // n+1、n+2：小盲、大盲注,先忽略
            i = i + 2;
            LOGGER.info("ignoring sb and bb post, continue.. line num:{}", i);
            // 分隔线跳过
            i = i + 1;
            LOGGER.info("jumping hole card seperate, continue.. line num:{}", i);
            // 发牌记录+Hero手牌
            for (; descString.get(i).split(" ")[0].equalsIgnoreCase("dealt"); i++) {
                if (descString.get(i).split(" ")[2].equalsIgnoreCase(HoldemConstant.PLAYER_WORD_GG)) {
                    String heroHandDesc = descString.get(i).split("[\\[\\]]")[1];
                    LOGGER.info("hero hand:{}", heroHandDesc);
                    // 解析为Card
                    hand.setHeroHands(Pair.of(Card.parseUsualDesc(heroHandDesc.split(" ")[0]),
                            Card.parseUsualDesc(heroHandDesc.split(" ")[1])));
                }
            }
            // 行动线解析, 遇到SHOWDOWN代表提前结束
            // calls [投入量]
            // raises [本次投入] to [本圈投入]
            // folds弃牌
            List<String> actionStr = new ArrayList<>();
            BigDecimal uncalledChips = BigDecimal.ZERO;
            for (; !descString.get(i).contains(HoldemConstant.END_DEVIDE_WORD_GG); i++) {
                // 拦截一下分割线，读取最终牌面
                actionStr.add(descString.get(i));
                // hero 盘中赢得pot时，归还多余的chips到chipAfter
                if (descString.get(i).contains("returned") &&
                        descString.get(i).split(" ")[descString.get(i).split(" ").length - 1]
                                .equalsIgnoreCase(HoldemConstant.PLAYER_WORD_GG)) {
                    uncalledChips = RegexUtil.cashStringToDecimal(
                            descString.get(i).split("[(|)]")[1]);
                }
            }
            hand.setActionLines(parseGGActionLine(actionStr));
            // 是否入池、入池类型判断、投入筹码
            BigDecimal putInPotSize = BigDecimal.ZERO;
            if (hand.getActionLines().getActionLines().containsKey(HoldemConstant.PLAYER_WORD_GG)) {
                ActionLine heroLine = hand.getActionLines().getActionLines().get(HoldemConstant.PLAYER_WORD_GG);
                heroLine.getPreFlop().stream()
                        .filter(pair -> !ActionType.passiveActions().contains(pair.getAt()))
                        .findFirst().map(actionPair -> {
                    hand.setVpip(true);
                    hand.setVpipType(actionPair.getAt());
                    return null;
                });
                // 每条街的
                List<ActionVo> actionHeroAll = new ArrayList<>(heroLine.getPreFlop());
                if (heroLine.getFlop() != null) {
                    actionHeroAll.addAll(heroLine.getFlop());
                }
                if (heroLine.getTurn() != null) {
                    actionHeroAll.addAll(heroLine.getTurn());
                }
                if (heroLine.getRiver() != null) {
                    actionHeroAll.addAll(heroLine.getRiver());
                }
                putInPotSize = putInPotSize.add(actionHeroAll.stream().map(ActionVo::getPi)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
            }
            // 判断位置是否是盲注位
            if (hand.getPosition() == 0) {
                putInPotSize = putInPotSize.add(hand.getSbSize());
            }
            if (hand.getPosition() == 1) {
                putInPotSize = putInPotSize.add(hand.getBbSize());
            }
            // 跳过showdown
            i++;
            List<String> potDivideStr = new ArrayList<>();
            // showDown到summary之间的collected（目前不做对手分析可忽略）
            for (; !descString.get(i).contains(HoldemConstant.SUMMARY_DEVIDE_WORD_GG); i++) {
                potDivideStr.add(descString.get(i));
            }
            boolean winPot = false;
            BigDecimal winSize = BigDecimal.ZERO;
            for (String potDivide : potDivideStr) {
                if (potDivide.split(" ")[0].equalsIgnoreCase(HoldemConstant.PLAYER_WORD_GG)) {
                    winPot = true;
                    winSize = winSize.add(RegexUtil.cashStringToDecimal(potDivide.split(" ")[2]));
                }
            }
            hand.setWinPot(winPot);
            // 剩余筹码 = 局前 + 赢得筹码 + 未被跟注筹码 - (总投入筹码 + 盲注)
            hand.setChipsAfter(hand.getChips().add(uncalledChips).add(winSize).subtract(putInPotSize));
            i++;
            // 总结部分：目前仅读取牌面记录
            BoardsVo boardHandsObj = new BoardsVo();
            int boardTimes = 0;
            for (; i < descString.size(); i++) {
                if (descString.get(i).contains("Board")) {
                    boardTimes++;
                    List<Card> boardToAdd = new ArrayList<>();
                    String[] boardHands = descString.get(i).split("[\\[\\]]")[1].split(" ");
                    if (boardHandsObj.getBoard1() == null) {
                        boardToAdd.addAll(
                                Stream.of(boardHands).map(Card::parseUsualDesc).collect(Collectors.toList()));
                    } else {
                        // 2~n次发牌，若少于5需要组合第1次发牌牌面
                        if (boardHands.length < 5) {
                            List<Card> board1 = boardHandsObj.getBoard1();
                            for (int j = 5 - boardHands.length, k = 0; j <= 4; j++, k++) {
                                board1.set(j, Card.parseUsualDesc(boardHands[k]));
                            }
                            boardToAdd.addAll(board1);
                        } else {
                            boardToAdd.addAll(
                                    Stream.of(boardHands).map(Card::parseUsualDesc).collect(Collectors.toList()));
                        }
                    }
                    switch (boardTimes) {
                        case 1 -> boardHandsObj.setBoard1(boardToAdd);
                        case 2 -> boardHandsObj.setBoard2(boardToAdd);
                        case 3 -> boardHandsObj.setBoard3(boardToAdd);
                    }
                }
            }
            hand.setBoardHands(boardHandsObj);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("parsing meets exp: row{}, first row content:{}", i + 1, descString.get(0));
            return null;
        }
        LOGGER.info("handMsg:{}", hand.toString());
        return hand;
    }
    
    /**
     * 解析当局所有的行动线
     * @param actionStrs 行动相关的String
     * @return actionLines
     */
    private ActionLinesVo parseGGActionLine(List<String> actionStrs) {
        Map<String, ActionLine> actionMap = new HashMap<>();
        // 街数,本街raise次数
        int streets = 0;
        int raiseTime = 0;
        for (String desc : actionStrs) {
            // 遇到分隔行, 根据标志位调整街数
            if (desc.contains("***")) {
                String streetTag = desc.split(" ")[1];
                if (HoldemConstant.GG_STREET_SEPERATE_MAP.containsKey(streetTag)) {
                    streets = HoldemConstant.GG_STREET_SEPERATE_MAP.get(streetTag);
                    raiseTime = 0;
                } else {
                    LOGGER.error("error seperator: {}", streetTag);
                    break;
                }
                continue;
            }
            String[] actionArr = desc.split(": ");
            if (actionArr.length <= 1) {
                LOGGER.error("illegal action line: {}", actionArr[0]);
                continue;
            }
            String uid = actionArr[0].toLowerCase();
            String actionStr = actionArr[1];
            if (streets <= HoldemConstant.STREET_RIVER) {
                switch (streets) {
                    case HoldemConstant.STREET_PREFLOP -> {
                        // 任何人翻前必定行动, 仅第一个case compute，后面都computeIfPresent
                        int finalRaiseTimePre = raiseTime;
                        int finalStreetsPre = streets;
                        ActionLine actPre = actionMap.compute(uid, (k, v) -> {
                            if (v == null) {
                                v = new ActionLine();
                                v.setPreFlop(new ArrayList<>());
                            }
                            v.getPreFlop().add(parseGGAction(actionStr,
                                    finalRaiseTimePre, finalStreetsPre));
                            return v;
                        });
                        if (ActionType.raiseActions().contains(actPre.getPreFlop()
                                .get(actPre.getPreFlop().size() - 1).getAt())) {
                            raiseTime++;
                        }
                    }
                    case HoldemConstant.STREET_FLOP -> {
                        int finalRaiseTimeFlop = raiseTime;
                        int finalStreetsFlop = streets;
                        ActionLine actFlop = actionMap.computeIfPresent(uid, (k, v) -> {
                            if (v.getFlop() == null) {
                                v.setFlop(new ArrayList<>());
                            }
                            v.getFlop().add(parseGGAction(actionStr,
                                    finalRaiseTimeFlop, finalStreetsFlop));
                            return v;
                        });
                        if (actFlop != null && ActionType.raiseActions().contains(actFlop.getFlop()
                                .get(actFlop.getFlop().size() - 1).getAt())) {
                            raiseTime++;
                        }
                    }
                    case HoldemConstant.STREET_TURN -> {
                        int finalRaiseTimeTurn = raiseTime;
                        int finalStreetsTurn = streets;
                        ActionLine actTurn = actionMap.computeIfPresent(uid, (k, v) -> {
                            if (v.getTurn() == null) {
                                v.setTurn(new ArrayList<>());
                            }
                            v.getTurn().add(parseGGAction(actionStr,
                                    finalRaiseTimeTurn, finalStreetsTurn));
                            return v;
                        });
                        if (actTurn != null && ActionType.raiseActions().contains(actTurn.getTurn()
                                .get(actTurn.getTurn().size() - 1).getAt())) {
                            raiseTime++;
                        }
                    }
                    case HoldemConstant.STREET_RIVER -> {
                        int finalRaiseTimeRiver = raiseTime;
                        int finalStreetsRiver = streets;
                        ActionLine actRiver = actionMap.computeIfPresent(uid, (k, v) -> {
                            if (v.getRiver() == null) {
                                v.setRiver(new ArrayList<>());
                            }
                            v.getRiver().add(parseGGAction(actionStr,
                                    finalRaiseTimeRiver, finalStreetsRiver));
                            return v;
                        });
                        if (actRiver != null && ActionType.raiseActions().contains(actRiver.getRiver()
                                .get(actRiver.getRiver().size() - 1).getAt())) {
                            raiseTime++;
                        }
                    }
                    default -> LOGGER.error("error street: {}", streets);
                }
            }
        }
        return new ActionLinesVo(actionMap);
    }
    
    /**
     * 解析单行行动
     * @param actionStr 行动文本
     * @param raiseTimes 本轮该行动前的raise次数，用于判定具体行为
     * @param streetCode 当前属于哪条街
     * @return
     */
    private ActionVo parseGGAction(String actionStr, int raiseTimes, int streetCode) {
        LOGGER.debug("action Str:{}", actionStr);
        ActionType type;
        BigDecimal cashInPot = BigDecimal.ZERO;
        String[] tokenArr = actionStr.split(" ");
        if (actionStr.equalsIgnoreCase(HoldemConstant.FOLD_ACTION_GG)) {
            // fold
            type = ActionType.FOLD;
        } else if (tokenArr[0].equalsIgnoreCase(HoldemConstant.CHECK_ACTION_GG)) {
            // check
            type = ActionType.CHECK;
        } else if (tokenArr[0].equalsIgnoreCase(HoldemConstant.SHOW_ACTION_GG)) {
            // show
            LOGGER.info("showing: {}, no matter", actionStr);
            type = ActionType.SHOW;
        } else {
            // 其余操作必须投入，此金额为当次行动投入的金额
            cashInPot = RegexUtil.cashStringToDecimal(tokenArr[1]);
            if (tokenArr[0].equalsIgnoreCase(HoldemConstant.CALL_ACTION_GG)) {
                // 首圈且无人raise时limp, 其余为call
                type = (streetCode <= HoldemConstant.STREET_PREFLOP && raiseTimes <= 0) ?
                        ActionType.LIMP : ActionType.CALL;
            } else if (tokenArr[tokenArr.length - 1].equalsIgnoreCase(HoldemConstant.ALLIN_ACTION_GG)) {
                // all-in
                type = ActionType.ALLIN;
            } else if (tokenArr[0].equalsIgnoreCase(HoldemConstant.BET_ACTION_GG)) {
                // open or bet
                type = (streetCode <= HoldemConstant.STREET_PREFLOP) ?
                        ActionType.OPEN : ActionType.BET;
            } else if (tokenArr[0].equalsIgnoreCase(HoldemConstant.RAISE_ACTION_GG)) {
                // raise
                type = switch (raiseTimes) {
                    case 0 -> ActionType.RAISE;
                    case 1 -> ActionType.RAISE2;
                    default -> ActionType.RAISE3P;
                };
                // 若翻前圈的raise前无其他raise，取后者；否则取前者
                if (streetCode <= HoldemConstant.STREET_PREFLOP && raiseTimes <= 0) {
                    cashInPot = RegexUtil.cashStringToDecimal(tokenArr[tokenArr.length - 1]);
                }
            } else {
                // 未匹配上的 以外
                LOGGER.error("err while parsing action: {}", actionStr);
                type = ActionType.CHECK;
            }
        }
        return new ActionVo(type, cashInPot);
    }
}
