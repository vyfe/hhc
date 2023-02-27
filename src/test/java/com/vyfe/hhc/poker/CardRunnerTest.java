package com.vyfe.hhc.poker;

import com.vyfe.hhc.poker.runner.BestCombo;
import com.vyfe.hhc.poker.runner.HoldemComboType;
import com.vyfe.hhc.poker.type.CardNumber;
import com.vyfe.hhc.system.HhcException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardRunnerTest {
    public static CardRunner cardRunner;
    
    @BeforeAll
    static void setUp() {
        cardRunner = new CardRunner();
        // 花色顺序 s-h-c-d
    }
    
    @Test
    @DisplayName("test combo straight flush")
    void getComboByCardsSF() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("3c"), Card.parseUsualDesc("4c"), Card.parseUsualDesc("5c")};
        var handCards = new Card[]{Card.parseUsualDesc("6c"), Card.parseUsualDesc("7c")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.STRAIGHT_FLUSH);
        testCombo.setDim(new Integer[]{CardNumber.SEVEN.getOrder()});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("7c"), Card.parseUsualDesc("6c"),
                Card.parseUsualDesc("5c"), Card.parseUsualDesc("4c"), Card.parseUsualDesc("3c")});
        Assertions.assertEquals(combo, testCombo);
    }
    
    @Test
    @DisplayName("test combo straight1")
    void getComboByCardsS1() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("3c"), Card.parseUsualDesc("4c"), Card.parseUsualDesc("5h"),
                Card.parseUsualDesc("9h"),};
        var handCards = new Card[]{Card.parseUsualDesc("6c"), Card.parseUsualDesc("7s")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.STRAIGHT);
        testCombo.setDim(new Integer[]{CardNumber.SEVEN.getOrder()});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("7s"), Card.parseUsualDesc("6c"),
                Card.parseUsualDesc("5h"), Card.parseUsualDesc("4c"), Card.parseUsualDesc("3c")});
        Assertions.assertEquals(combo, testCombo);
    }
    
    @Test
    @DisplayName("test combo straight with a and 6")
    void getComboByCardsS2() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("2c"), Card.parseUsualDesc("3d"), Card.parseUsualDesc("4h"),
                Card.parseUsualDesc("6h"),};
        var handCards = new Card[]{Card.parseUsualDesc("Ac"), Card.parseUsualDesc("5s")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.STRAIGHT);
        testCombo.setDim(new Integer[]{CardNumber.SIX.getOrder()});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("6h"), Card.parseUsualDesc("5s"),
                Card.parseUsualDesc("4h"), Card.parseUsualDesc("3d"), Card.parseUsualDesc("2c")});
        Assertions.assertEquals(combo, testCombo);
    }
    
    @Test
    @DisplayName("test combo straight with a-5")
    void getComboByCardsS3() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("2c"), Card.parseUsualDesc("3d"), Card.parseUsualDesc("4h"),
                Card.parseUsualDesc("Jh"),};
        var handCards = new Card[]{Card.parseUsualDesc("Ac"), Card.parseUsualDesc("5s")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.STRAIGHT);
        testCombo.setDim(new Integer[]{CardNumber.FIVE.getOrder()});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("5s"), Card.parseUsualDesc("4h"),
                Card.parseUsualDesc("3d"), Card.parseUsualDesc("2c"), Card.parseUsualDesc("Ac")});
        Assertions.assertEquals(combo, testCombo);
    }
    
    @Test
    @DisplayName("test combo four")
    void getComboByCardsF1() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("Qc"), Card.parseUsualDesc("Qd"), Card.parseUsualDesc("Jh"),
                Card.parseUsualDesc("9h"), Card.parseUsualDesc("7c")};
        var handCards = new Card[]{Card.parseUsualDesc("Qh"), Card.parseUsualDesc("Qs")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.FOUR);
        testCombo.setDim(new Integer[]{CardNumber.QUEEN.getOrder()});
        testCombo.setPieceDim(new Integer[]{CardNumber.JACK.getOrder()});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("Qs"), Card.parseUsualDesc("Qh"), Card.parseUsualDesc("Qc"),
                Card.parseUsualDesc("Qd"), Card.parseUsualDesc("Jh")});
        Assertions.assertEquals(combo, testCombo);
    }
    
    @Test
    @DisplayName("test combo four with pair")
    void getComboByCardsF2() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("Qc"), Card.parseUsualDesc("Qd"), Card.parseUsualDesc("7h"),
                Card.parseUsualDesc("9h"), Card.parseUsualDesc("9c")};
        var handCards = new Card[]{Card.parseUsualDesc("Qh"), Card.parseUsualDesc("Qs")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.FOUR);
        testCombo.setDim(new Integer[]{CardNumber.QUEEN.getOrder()});
        testCombo.setPieceDim(new Integer[]{CardNumber.NINE.getOrder()});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("Qs"), Card.parseUsualDesc("Qh"), Card.parseUsualDesc("Qc"),
                Card.parseUsualDesc("Qd"), Card.parseUsualDesc("9h")});
        Assertions.assertEquals(combo, testCombo);
    }
    
    @Test
    @DisplayName("test combo set")
    void getComboByCardsSet1() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("Qc"), Card.parseUsualDesc("Qd"), Card.parseUsualDesc("Jh"),
                Card.parseUsualDesc("9h"), Card.parseUsualDesc("7c")};
        var handCards = new Card[]{Card.parseUsualDesc("3c"), Card.parseUsualDesc("Qs")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.SET);
        testCombo.setDim(new Integer[]{CardNumber.QUEEN.getOrder()});
        testCombo.setPieceDim(new Integer[]{CardNumber.JACK.getOrder(), CardNumber.NINE.getOrder()});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("Qs"), Card.parseUsualDesc("Qc"),
                Card.parseUsualDesc("Qd"), Card.parseUsualDesc("Jh"), Card.parseUsualDesc("9h")});
        Assertions.assertEquals(combo, testCombo);
    }
    
    @Test
    @DisplayName("test combo two pair")
    void getComboByCardsTP1() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("5c"), Card.parseUsualDesc("3d"), Card.parseUsualDesc("Jh"),
                Card.parseUsualDesc("9h"),};
        var handCards = new Card[]{Card.parseUsualDesc("3c"), Card.parseUsualDesc("Js")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.TWO_PAIR);
        testCombo.setDim(new Integer[]{CardNumber.JACK.getOrder(), CardNumber.THREE.getOrder()});
        testCombo.setPieceDim(new Integer[]{CardNumber.NINE.getOrder()});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("Js"), Card.parseUsualDesc("Jh"),
                Card.parseUsualDesc("3c"), Card.parseUsualDesc("3d"), Card.parseUsualDesc("9h")});
        Assertions.assertEquals(combo, testCombo);
    }
    
    @Test
    @DisplayName("test combo pair")
    void getComboByCardsP1() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("5c"), Card.parseUsualDesc("3d"), Card.parseUsualDesc("Qh"),
                Card.parseUsualDesc("9h"), Card.parseUsualDesc("As")};
        var handCards = new Card[]{Card.parseUsualDesc("Jc"), Card.parseUsualDesc("Js")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.PAIR);
        testCombo.setDim(new Integer[]{CardNumber.JACK.getOrder()});
        testCombo.setPieceDim(new Integer[]{CardNumber.ACE.getOrder(), CardNumber.QUEEN.getOrder(),
                CardNumber.NINE.getOrder(),});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("Js"), Card.parseUsualDesc("Jc"),
                Card.parseUsualDesc("As"), Card.parseUsualDesc("Qh"), Card.parseUsualDesc("9h")});
        Assertions.assertEquals(combo, testCombo);
    }
    
    @Test
    @DisplayName("test combo full house")
    void getComboByCardsFH1() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("Ac"), Card.parseUsualDesc("3d"), Card.parseUsualDesc("3h"),
                Card.parseUsualDesc("9h"),};
        var handCards = new Card[]{Card.parseUsualDesc("Ah"), Card.parseUsualDesc("As")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.FULLHOUSE);
        testCombo.setDim(new Integer[]{CardNumber.ACE.getOrder(), CardNumber.THREE.getOrder()});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("As"), Card.parseUsualDesc("Ah"),
                Card.parseUsualDesc("Ac"), Card.parseUsualDesc("3h"), Card.parseUsualDesc("3d")});
        Assertions.assertEquals(combo, testCombo);
    }
    
    @Test
    @DisplayName("test combo high card")
    void getComboByCardsH1() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("Ac"), Card.parseUsualDesc("3d"), Card.parseUsualDesc("7h"),
                Card.parseUsualDesc("9h"), Card.parseUsualDesc("Th")};
        var handCards = new Card[]{Card.parseUsualDesc("Kh"), Card.parseUsualDesc("Qs")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.HIGH_CARD);
        testCombo.setDim(new Integer[]{CardNumber.ACE.getOrder()});
        testCombo.setPieceDim(new Integer[]{CardNumber.KING.getOrder(), CardNumber.QUEEN.getOrder(),
                CardNumber.TEN.getOrder(), CardNumber.NINE.getOrder()});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("Ac"), Card.parseUsualDesc("Kh"),
                Card.parseUsualDesc("Qs"), Card.parseUsualDesc("Th"), Card.parseUsualDesc("9h")});
        Assertions.assertEquals(combo, testCombo);
    }
    
    @Test
    @DisplayName("test combo flush")
    void getComboByCardsFL1() throws HhcException {
        var publicCards = new Card[]{Card.parseUsualDesc("Ac"), Card.parseUsualDesc("3c"), Card.parseUsualDesc("7h"),
                Card.parseUsualDesc("9c"), Card.parseUsualDesc("Tc")};
        var handCards = new Card[]{Card.parseUsualDesc("Kc"), Card.parseUsualDesc("Qc")};
        var combo = cardRunner.getComboByCards(publicCards, handCards);
        // 理想输出
        var testCombo = new BestCombo();
        testCombo.setType(HoldemComboType.FLUSH);
        testCombo.setDim(new Integer[]{CardNumber.ACE.getOrder()});
        testCombo.setPieceDim(new Integer[]{CardNumber.KING.getOrder(), CardNumber.QUEEN.getOrder(),
                CardNumber.TEN.getOrder(), CardNumber.NINE.getOrder()});
        testCombo.setComboCards(new Card[]{Card.parseUsualDesc("Ac"), Card.parseUsualDesc("Kc"),
                Card.parseUsualDesc("Qc"), Card.parseUsualDesc("Tc"), Card.parseUsualDesc("9c")});
        Assertions.assertEquals(combo, testCombo);
    }
}