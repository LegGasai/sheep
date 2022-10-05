import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

//按钮
public class BtnContainer extends Panel {

    public BufferedImage[] btnImages;
    public GuiApplication application;

    public BtnContainer(GuiApplication application) throws IOException{
        this.btnImages=new BufferedImage[3];
        this.application=application;

        init();
    }

    public void init() throws IOException {
        for (int i = 0; i < 3; i++) {
            this.btnImages[i]= ImageIO.read(getClass().getResource("/image/button/"+i+".png"));
        }
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(btnImages[0],25,950,170,130,null);
        g.drawImage(btnImages[1],210,950,170,130,null);
        g.drawImage(btnImages[2],395,950,170,130,null);
    }


    public void withdraw(){
        System.out.println("withdraw");
    }

}
