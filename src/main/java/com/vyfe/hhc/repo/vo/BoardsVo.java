package com.vyfe.hhc.repo.vo;

import java.util.List;

import com.vyfe.hhc.poker.Card;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BoardsVo类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/19
 * Description: 本局手牌记录（最多发三次牌）
 */
@Data
@NoArgsConstructor
public class BoardsVo {
    private List<Card> board1;
    private List<Card> board2;
    private List<Card> board3;
}
