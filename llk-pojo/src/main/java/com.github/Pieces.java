package com.github;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * @author Arjen10
 * @date 2022/8/20 11:13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pieces {

    /**
     * 棋子在内存中的地址
     */
    private Integer address;

    /**
     * 值
     */
    private Integer value;

    /**
     * 棋子在棋盘中的下标
     */
    private Integer index;

    public Integer getPiecesY() {
        if (this.index < 18) {
            return 0;
        }
        return this.index / 19;
    }

    public Integer getPiecesX() {
        if (this.index < 18) {
            return this.index;
        }
        //下标要从0开始
        return this.index % 19;
    }

    public Integer getWindX(Integer windowX) {
        //棋子窗口位置 = getWindX() + 窗口偏移
        return getWindX() + windowX;
    }

    public Integer getWindX() {
        Integer piecesX = this.getPiecesX();
        //棋子窗口位置 = 棋子X轴（这个是从0开始计算） * 前面多少个棋子 + 棋子大小的一半 + 窗口最左边的边框大小
        return piecesX * 30 + 20 + 15;
    }

    public Integer getWindY(Integer windowY) {
        //180 是棋盘位置
        return getWindY() + windowY;
    }

    public Integer getWindY() {
        Integer piecesY = this.getPiecesY();
        return piecesY* 36 + 18 + 180;
    }

    /**
     * 获取这个棋子上下左右的坐标
     *
     * x   top x
     * left a right
     * x   bottom  x
     *
     * @return 返回上图b的棋子集合，返回顺序为b1、b2、b3、b4，如果不存在则是null
     */
    public com.github.PiecesBoxVO getBox(List<List<Pieces>> ps) {
        if (Objects.isNull(ps) || ps.isEmpty()) {
            throw new NullPointerException("集合不准为空！");
        }
        Integer x = this.getPiecesX();
        Integer y = this.getPiecesY();
        return  PiecesBoxVO.builder()
                .p(this)
                .top(y == 0 ? null : ps.get(y - 1).get(x).getValue().equals(0) ? null : ps.get(y - 1).get(x))//top
                .left(x == 0 ? null : ps.get(y).get(x - 1).getValue().equals(0) ? null : ps.get(y).get(x - 1))//left
                .right(x == 18 ? null : ps.get(y).get(x + 1).getValue().equals(0) ? null : ps.get(y).get(x + 1))//right
                .bottom(y == 10 ? null : ps.get(y + 1).get(x).getValue().equals(0) ? null : ps.get(y + 1).get(x))//bottom
                .build();
    }


}
