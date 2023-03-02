package com.vyfe.hhc.poker.runner;

import com.vyfe.hhc.poker.Card;
import com.vyfe.hhc.poker.CardRunner;
import com.vyfe.hhc.system.HhcException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BestComboTest {
    public static CardRunner cardRunner;
    
    @BeforeAll
    static void setUp() {
        cardRunner = new CardRunner();
        // 花色顺序 s-h-c-d
    }
    
    @Test
    @DisplayName("test comparing")
    void getComboByCardsSF() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("3c"), Card.parseUsualDesc("4c"), Card.parseUsualDesc("5c")};
        var handCards = new Card[]{Card.parseUsualDesc("6c"), Card.parseUsualDesc("7c")};
        var publicCards2 = new Card[]{Card.parseUsualDesc("8c"), Card.parseUsualDesc("4c"), Card.parseUsualDesc("5c")};
        var handCards2 = new Card[]{Card.parseUsualDesc("6c"), Card.parseUsualDesc("7c")};
        var combo1 = cardRunner.getComboByCards(publicCards, handCards);
        var combo2 = cardRunner.getComboByCards(publicCards2, handCards2);
        // combo1 < combo2
        Assertions.assertTrue(combo1.compareTo(combo2) < 0);
        // 极端场景
        var publicCard3 = new Card[]{Card.parseUsualDesc("Ac"), Card.parseUsualDesc("As"), Card.parseUsualDesc("Ah"),
                Card.parseUsualDesc("Ad"), Card.parseUsualDesc("9s"),};
        var handCardsA = new Card[]{Card.parseUsualDesc("6h"), Card.parseUsualDesc("7s")};
        var handCardsB = new Card[]{Card.parseUsualDesc("Kh"), Card.parseUsualDesc("9d")};
        var combo3 = cardRunner.getComboByCards(publicCard3, handCardsA);
        var combo4 = cardRunner.getComboByCards(publicCard3, handCardsB);
        Assertions.assertTrue(combo3.compareTo(combo4) < 0);
    }
}