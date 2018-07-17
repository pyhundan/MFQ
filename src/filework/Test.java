package filework;

import javax.swing.*;

public class Test {
    private JButton button;
    private JPanel panel;

    public static void main(String[] args){

        JFrame jframe=new JFrame("test");
        jframe.setContentPane(new Test().panel);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(1000,800);
        jframe.setVisible(true);

    }
}
