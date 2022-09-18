package com.github;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class A extends JPanel{
   static Point point;      // 用来定位鼠标位置
   static int str;          // 用来显示进度

   A(){
        point = new Point(0, 0);  // 初始化鼠标位置。
        str = 0;
   }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        // 画一个固定长宽空心的长方形，然后画一个实心的长方形，但是长度随着鼠标移动而移动
        g.drawRect(100,100,200,30);
        g.fillRect(100,100, point.x-100,30);
        g.drawString("进度为："+str+"%",100,50);
    }

    public static void main(String[] args)
    {

        JFrame jFrame = new JFrame();
        jFrame.setBounds(100,100,500,500);
        A a = new A();
        jFrame.add(a);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//直接退出
        jFrame.addMouseListener(new MouseAdapter() {
            
        });
        jFrame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }

            // 鼠标按住不放，不断监测鼠标位置然后不断更新point.x和str以达到动态变化的效果
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                
                            point = new Point(e.getX(), e.getY());
                            if (e.getX()>=300) point.x=300;
                            str = (point.x-100)/2;
                            System.out.println("x:" + point.x + "y:" + point.y);
                            a.repaint(); // 重绘
                            
            }
        });
    }

}
