import javax.swing.*;
import java.awt.*;

class MyDialog extends JDialog {
    public MyDialog(String message) {
        setVisible(true);
        setBounds(100,100,300,150);
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 弹出窗口自带关闭
        setLocationRelativeTo(null);
        Container container = getContentPane();
        container.setLayout(null);

        JLabel jLabel = new JLabel(message,SwingConstants.CENTER);
        jLabel.setFont(new Font("Serief",Font.BOLD,25));
        jLabel.setSize(300,100);
        container.add(jLabel);
    }
}
