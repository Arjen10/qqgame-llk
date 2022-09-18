package com.github;


import lombok.*;

/**
 * @author Arjen10
 * @date 2022/9/11 9:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PiecesBoxVO {

    /**
     * 中心棋子
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Pieces p;

    private Pieces top;

    private Pieces left;

    private Pieces right;

    private Pieces bottom;

    public Boolean isOneEmpty() {

        if (p.getPiecesX().equals(0)) {
            return top == null || right == null || bottom == null;
        }

        if (p.getPiecesY().equals(0)) {
            return left == null || right == null || bottom == null;
        }

        if (p.getPiecesX().equals(18)) {
            return top == null || left == null || bottom == null;
        }

        if (p.getPiecesY().equals(11)) {
            return top == null || left == null || right == null;
        }

        return top == null || left == null || right == null || bottom == null;
    }

    public Boolean getTopIsNull() {
        return top == null;
    }

    public Boolean getLeftIsNull() {
        return left == null;
    }

    public Boolean getRightIsNull() {
        return right == null;
    }

    public Boolean getBottomIsNull() {
        return bottom == null;
    }
}
