package com.github;

import java.util.List;

/**
 * @author Arjen10
 * @date 2022/8/20 10:46
 */
public interface ClearService {

    /**
     * 获取棋盘的全部棋子
     *
     * @return 棋子Pieces对象集合
     */
    List<List<Pieces>> getPiecesList();

    /**
     * 消除两个一样满足规则的棋子
     *
     * @param p1 第一个棋子
     * @param p2 第二个棋子
     */
    Boolean clear(Pieces p1, Pieces p2);

    /**
     * 棋子X和Y在同一方向
     *
     * @param p1 棋子1
     * @param p2 棋子2
     * @param pList 棋盘棋子集合
     * @return 是否消除
     */
    Boolean XOrYEquals(Pieces p1, Pieces p2, List<List<Pieces>> pList);

    /**
     * 单折线消除
     *
     * @param p1 棋子一
     * @param p2 棋子二
     * @param pList 棋盘二维集合
     * @return 是否消除
     */
    Boolean singleBrokenLine(Pieces p1, Pieces p2, List<List<Pieces>> pList);

    /**
     * 双折线消除
     *
     * @param p1 棋子一
     * @param p2 棋子二
     * @param pList 棋盘二维集合
     * @return 是否消除
     */
    Boolean doubleBrokenLine(Pieces p1, Pieces p2, List<List<Pieces>> pList);

    /**
     * 单消，建议递归使用
     *
      * @param pList 棋盘二维集合
     * @return 棋盘二维集合
     */
    default List<List<Pieces>> singleClear(List<List<Pieces>> pList) {
        throw new AbstractMethodError("单消以后实现");
    }

    /**
     * 递归消除全部
     */
    void clearAll();

}
