package com.github.impl;

import com.github.ClearService;
import com.github.Pieces;
import com.github.PiecesBoxVO;
import com.github.conf.LlkInitialize;
import com.github.memory.WindowsUtils;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.conf.GameStanderConfig.CHESSBOARD_ADDRESS;
import static com.github.conf.GameStanderConfig.REMAINDER_ADDRESS;
import static com.github.conf.LlkInitialize.getHANDLE;
import static com.github.util.RobotUtil.onClickMessage;

/**
 * @author Arjen10
 * @date 2022/8/20 10:47
 */
@Slf4j
@Service
public class ClearServiceImpl implements ClearService {

    private static final AtomicInteger ATOMICINTEGER = new AtomicInteger();

    @Override
    public List<List<Pieces>> getPiecesList() {
        Collection<List<Pieces>> values = IntStream.range(0, 19 * 11)
                .mapToObj(i ->
                        Pieces.builder()
                                .address(CHESSBOARD_ADDRESS + i)
                                .value(WindowsUtils.readByteOfAddress(getHANDLE(), CHESSBOARD_ADDRESS + i).intValue())
                                .index(i)
                                .build()
                )
                .collect(Collectors.groupingBy(p -> p.getIndex() / 19))
                .values();
        return new ArrayList<>(values);
    }

    @Override
    public Boolean clear(Pieces p1, Pieces p2) {
        Objects.requireNonNull(p1);
        Objects.requireNonNull(p2);
        //剩余棋子数量。这个要注释掉，不能频繁读取内存
        Integer num = WindowsUtils.readIntOfAddress(getHANDLE(), REMAINDER_ADDRESS);
        if (num.equals(0)) {
            throw new IllegalArgumentException("棋子已经消除完毕！");
        }
        //WinDef.RECT rect = LlkInitialize.getRECT();
        log.debug("棋子一的窗口坐标：p1 x:{}, y:{}. p1棋盘下标: {}.", p1.getWindX(), p1.getWindY(), p1.getIndex());
        log.debug("棋子二的窗口坐标：p2 x:{}, y:{}. p2棋盘下标: {}. ", p2.getWindX(), p2.getWindY(), p2.getIndex());
        onClickMessage(p1.getWindX(), p1.getWindY());
        onClickMessage(p2.getWindX(), p2.getWindY());
        p1.setValue(0);
        p2.setValue(0);
        log.info("剩余的棋子数量：{}", WindowsUtils.readIntOfAddress(getHANDLE(), REMAINDER_ADDRESS));
        return true;
    }

    @Override
    public Boolean XOrYEquals(Pieces p1, Pieces p2, List<List<Pieces>> pList) {
        Objects.requireNonNull(p1);
        Objects.requireNonNull(p2);
        Integer x1 = p1.getPiecesX();
        Integer y1 = p1.getPiecesY();
        Integer x2 = p2.getPiecesX();
        Integer y2 = p2.getPiecesY();
        PiecesBoxVO p1Box = p1.getBox(pList);
        PiecesBoxVO p2Box = p2.getBox(pList);
        //Y轴一样的
        if (y1.equals(y2)) {
            //拿到最小的x
            var minX = Math.min(x1, x2);
            var maxX = Math.max(x1, x2);
            boolean b = IntStream.range(minX + 1, Math.max(x1, x2))
                    .mapToObj(i -> pList.get(y1).get(i))
                    .allMatch(p -> p.getValue().equals(0));
            if (!b && p1Box.getBottom() == null && p2Box.getBottom() == null && y1 != 10) {
                boolean b1 = IntStream.rangeClosed(y1 + 1, 10)
                        .anyMatch(y ->
                                IntStream.rangeClosed(minX, maxX)
                                        .allMatch(x -> pList.get(y).get(x).getValue().equals(0))
                                        && IntStream.rangeClosed(y1 + 1, y)
                                        .allMatch(p1y -> pList.get(p1y).get(x1).getValue().equals(0))
                                        && IntStream.rangeClosed(y2 + 1, y)
                                        .allMatch(p2y -> pList.get(p2y).get(x2).getValue().equals(0))
                        );
                return b1 ? this.clear(p1, p2) : false;
            }
            if (!b && p1Box.getTop() == null && p2Box.getTop() == null && y1 != 0) {
                boolean b1 = IntStream.rangeClosed(0, y1)
                        .anyMatch(y ->
                                IntStream.rangeClosed(minX, maxX)
                                        .allMatch(x -> pList.get(y).get(x).getValue().equals(0))
                                        && IntStream.rangeClosed(y, y1 - 1)
                                        .allMatch(p1y -> pList.get(p1y).get(x1).getValue().equals(0))
                                        && IntStream.rangeClosed(y, y2 - 1)
                                        .allMatch(p2y -> pList.get(p2y).get(x2).getValue().equals(0))
                        );
                return b1 ? this.clear(p1, p2) : false;
            }
            return b ? this.clear(p1, p2) : false;
        }
        //X轴一样的
        if (!x1.equals(x2)) {
            return false;
        }
        //拿到最小的Y
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        //fix bug abs: 2,minY: 3
        boolean b = IntStream.range(minY + 1, maxY)
                .mapToObj(i -> pList.get(i).get(x1))
                .allMatch(p -> p.getValue().equals(0));
        //处理两个棋子右边为空且X轴相同的情况
        if (!b && p1Box.getRightIsNull() && p2Box.getRightIsNull()
                && x1 != 0) {
            Boolean b1 = checkDoubleLeftOrRight(x1 + 1, 18, minY, maxY, y1, y2, pList);
            return b1 ? this.clear(p1, p2) : false;
        }
        //处理两个棋子左边为空且X轴相同的情况
        if (!b && p1Box.getLeft() == null && p2Box.getLeft() == null && x1 != 18) {
            Boolean b1 = checkDoubleLeftOrRight(0, x1 + 1, minY, maxY, y1, y2, pList);
            return b1 ? this.clear(p1, p2) : false;
        }
        return b ? this.clear(p1, p2) : false;
    }

    private Boolean checkDoubleLeftOrRight(Integer startX, Integer endX, Integer minY, Integer maxY,
                                           Integer y1, Integer y2, List<List<Pieces>> pList) {
        return IntStream.range(startX, endX)
                .anyMatch(x ->
                        IntStream.rangeClosed(minY, maxY)
                                .allMatch(y -> pList.get(y).get(x).getValue().equals(0))
                        && pList.get(y1).get(x).getValue().equals(0)
                        && pList.get(y2).get(x).getValue().equals(0)
                );
    }

    @Override
    public Boolean singleBrokenLine(Pieces p1, Pieces p2, List<List<Pieces>> pList) {
        //Y或者X相等不属于单折线情况
        if (p1.getPiecesY().equals(p2.getPiecesY()) || p1.getPiecesX().equals(p2.getPiecesX())) {
            return false;
        }
        PiecesBoxVO p1Box = p1.getBox(pList);
        PiecesBoxVO p2Box = p2.getBox(pList);
        //如果棋子四周都不是空的，直接返回
        if (!(p1Box.isOneEmpty() || p2Box.isOneEmpty())) {
            return false;
        }
        //* * * *
        //* * * *
        //* * | *
        //* - - *
        //一个折线的情况：就是一个缺口对应另一个棋子的多个缺口
        //处理第一种情况：棋子1的下对应棋子2的右或者左
        var p1x = p1.getPiecesX();
        var p2x = p2.getPiecesX();
        var maxY = p2.getPiecesY();
        var minY = p1.getPiecesY();
        if (p1Box.getBottom() == null && ((p2Box.getLeft() == null && p1x < p2x) || (p2Box.getRight() == null && p1x > p2x))) {
            //算最后棋子二的最后一排是不是通路
            var startX = p2Box.getLeft() == null && p1x < p2x ? p1.getPiecesX() : p2.getPiecesX() + 1;
            var endX = p2Box.getLeft() == null && p1x < p2x ? p2.getPiecesX() : p1.getPiecesX() + 1;
            //TODO 这个可能有个BUG，个人感觉偶尔出现，不好DEBUG。
            //TODO PS:2022-9-11 这个功能在边上的无法消除的BUG偶尔出现
            var b = IntStream.rangeClosed(minY + 1, maxY)
                    .mapToObj(y ->
                            //如果不等于最后一个Y，那就取每行对应的X。如果等于最后一个Y，就要取这一行的X
                            y != maxY
                                    ? Stream.of(pList.get(y).get(p1x))
                                    : IntStream.range(startX, endX).mapToObj(x -> pList.get(y).get(x))
                    )
                    .flatMap(s -> s)
                    .allMatch(p -> p.getValue().equals(0));
            return b ? this.clear(p1, p2) : false;
        }
        //处理第二种情况：棋子1的左或者右对应棋子2的上 怎么感觉跟上面的情况是反起的
        if (p2Box.getTop() == null && ((p1Box.getRight() == null && p1x < p2x) || (p1Box.getLeft() == null && p1x > p2x))) {
            var startX = p1Box.getRight() == null && p1x < p2x
                    ? p1.getPiecesX() + 1
                    : p2.getPiecesX();
            var endX = p1Box.getRight() == null && p1x < p2x
                    ? p2.getPiecesX() + 1
                    : p1.getPiecesX();
            boolean b = IntStream.range(minY, maxY)
                    .mapToObj(y ->
                            y == p1.getPiecesY()
                                    ? IntStream.range(startX, endX).mapToObj(x -> pList.get(y).get(x))
                                    : Stream.of(pList.get(y).get(p2x))
                    )
                    .flatMap(s -> s)
                    .allMatch(p -> p.getValue().equals(0));
            return b ? this.clear(p1, p2) : false;
        }
        return false;
    }

    @Override
    public Boolean doubleBrokenLine(Pieces p1, Pieces p2, List<List<Pieces>> pList) {
        Objects.requireNonNull(p1);
        Objects.requireNonNull(p2);
        PiecesBoxVO p1Box = p1.getBox(pList);
        PiecesBoxVO p2Box = p2.getBox(pList);
        //如果棋子四周都不是空的，直接返回
        if (!(p1Box.isOneEmpty() || p2Box.isOneEmpty())) {
            return false;
        }
        var x1 = p1.getPiecesX();
        var x2 = p2.getPiecesX();
        var y1 = p1.getPiecesY();
        var y2 = p2.getPiecesY();
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int maxX = Math.max(x1, x2);
        int minX = Math.min(x1, x2);
        if (p1.getPiecesX() != 10 && p2.getPiecesX() != 0 && p1Box.getBottomIsNull() && p2Box.getTopIsNull()) {
            var startY = minY + 1;
            var endY = maxY - 1;
            boolean b = IntStream.rangeClosed(startY, endY)
                    .anyMatch(y ->
                            //横线
                            IntStream.rangeClosed(minX, maxX)
                                    .allMatch(x -> pList.get(y).get(x).getValue().equals(0))
                            //竖线1
                            && IntStream.rangeClosed(startY, y)
                                    .allMatch(p1y -> pList.get(p1y).get(x1).getValue().equals(0))
                            //竖线2
                            && IntStream.rangeClosed(y, endY)
                                    .allMatch(p2y -> pList.get(p2y).get(x2).getValue().equals(0))
                    );
            return b ? this.clear(p1, p2) : false;
        }
        if ((x1 < x2 && p1Box.getRightIsNull() && p2Box.getLeftIsNull())
                || (x1 > x2 && p1Box.getLeftIsNull() && p2Box.getRightIsNull())) {
            var startX = minX + 1;
            var l1Y = x1 < x2 ? y1 : y2;
            var l2Y = x1 < x2 ? y2 : y1;
            boolean b = IntStream.range(startX, maxX)
                    .filter(x ->
                            //竖线通
                            IntStream.rangeClosed(minY, maxY)
                                    .allMatch(y -> pList.get(y).get(x).getValue().equals(0))
                    )
                    .anyMatch(x ->
                            //横线1通
                            IntStream.rangeClosed(startX, x)
                                    .allMatch(l1 -> pList.get(l1Y).get(l1).getValue().equals(0))
                            //横线2通
                            && IntStream.range(x, maxX)
                                    .allMatch(l2 -> pList.get(l2Y).get(l2).getValue().equals(0))
                    );
            return b ? this.clear(p1, p2) : false;
        }
        if ((y2 != 11 || y1 != 0) && p1Box.getBottomIsNull() && p2Box.getBottomIsNull()) {
            boolean b = IntStream.rangeClosed(minY + 1, maxY)
                    .anyMatch(y ->
                            IntStream.rangeClosed(minX, maxX)
                                    .allMatch(x -> pList.get(y).get(x).getValue().equals(0))
                                    && IntStream.rangeClosed(minY + 1, y)
                                    .allMatch(min -> pList.get(min).get(minX).getValue().equals(0))
                                    && IntStream.rangeClosed(maxY + 1, y)
                                    .allMatch(max -> pList.get(max).get(maxX).getValue().equals(0))
                    );
            return b ? this.clear(p1, p2) : false;
        }
        return false;
    }


    @Override
    public void clearAll() {
        List<List<Pieces>> piecesList = this.getPiecesList();
        List<Pieces> collect = piecesList.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        for (int i = 0; i < collect.size(); i++) {
            Pieces p1 = collect.get(i);
            if (p1.getValue().equals(0)) {
                continue;
            }
            for (int j = i + 1; j < collect.size(); j++) {
                Pieces p2 = collect.get(j);
                if (!p2.getValue().equals(p1.getValue())) {
                    continue;
                }
                Boolean aBoolean = XOrYEquals(p1, p2, piecesList);
                if (aBoolean) {
                    break;
                }
                Boolean aBoolean1 = singleBrokenLine(p1, p2, piecesList);
                if (aBoolean1) {
                    break;
                }
                Boolean aBoolean2 = doubleBrokenLine(p1, p2, piecesList);
                if (aBoolean2) {
                    break;
                }
            }
        }
        int sum = collect.stream()
                .mapToInt(Pieces::getValue)
                .sum();
        if (sum != 0 && ATOMICINTEGER.get() < 40) {
            ATOMICINTEGER.incrementAndGet();
            clearAll();
        }else {
            ATOMICINTEGER.set(0);
        }
    }

}
