import com.sun.javaws.util.JfxHelper;
import javafx.scene.layout.Background;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GuiApplication extends JPanel{

    public static final int START=0;//开始
    public static final int RUNNING=1;//运行
    public static final int GAME_OVER=2;//结束
    public static final int VECTOR=3;//结束
    //难度列表
    public static final int DIFFICULTYTYPES[]=new int[]{3,14,14};
    public static final int DIFFICULTYGRUOPS[]=new int[]{3,15,18};
    public static final int DIFFICULTYUNKNOW[]=new int[]{0,0,2};
    public int state;
    //牌的总数量252种,到0游戏结束胜利,共十四种牌
    public static int difficulty;
    public static int cardTypes;
    public static int cardGroups;
    public static int cardUknows;



    public int currentCardCounts;
    public int moveoutCount=1;//移除机会
    public int withdrawCount=1;//撤销机会
    public int shuffleCount=1;//打乱机会

    public BufferedImage background;//背景图片
    public BufferedImage startImage;//开始图片
    public BufferedImage vectorImage;//胜利图片
    public BufferedImage defeatImage;//失败图片
    public BufferedImage processBarImage;//失败图片

    public CardContainer cardContainer;
    public CardSlot cardSlot;
    public BtnContainer btnContainer;
    public PlayMusic playMusic;
    public static void main(String[] args) throws Exception{
        //主窗体
        JFrame jFrame = new JFrame();
        jFrame.setTitle("羊了个羊");
        //主容器
        GuiApplication guiApplication = new GuiApplication();
        guiApplication.setLayout(new FlowLayout());
        jFrame.add(guiApplication);
        jFrame.setSize(612,1150);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        guiApplication.action();

    }

    public GuiApplication() throws IOException{
        upload();
        this.playMusic=new PlayMusic();
        this.state=START;

        //Mouse
        MouseListener l=new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int x=e.getX();
                    int y=e.getY();
                    //System.out.println(x+","+y);
                    if(state==START||state==VECTOR||state==GAME_OVER){
                        if(state==START){
                            state=RUNNING;
                            int difficulty=0;
                            if (x>=215&&x<=395){
                                if (y>=260&&y<=320){
                                    difficulty=0;
                                }else if (y>=340&&y<=400){
                                    difficulty=1;
                                }else if (y>=420&&y<=480){
                                    difficulty=2;
                                }
                            }
                            start(difficulty);
                        }
                        else{
                            state=START;
                        }
                    }else{
                        if (y>=956&&y<=1064){
                            if (x>=37&&x<=177){
                                //moveout
                                if (moveoutCount>0){
                                    boolean isSuccess = cardSlot.moveOut();
                                    if(isSuccess){
                                        moveoutCount-=1;
                                    }else{
                                        new MyDialog("卡槽为空，无法移除！");
                                    }

                                }else{
                                    new MyDialog("移除次数已用完！");
                                }
                            }else if(x>=223&&x<=363){
                                //withdraw
                                if(withdrawCount>0){
                                    boolean isSuccess = cardSlot.withdraw();
                                    if (isSuccess){
                                        withdrawCount-=1;
                                    }else{
                                        new MyDialog("不允许撤销！");
                                    }

                                }else{
                                    new MyDialog("撤销次数已用完！");
                                }

                            }else if (x>=407&&x<=547){
                                //shuffle
                                if (shuffleCount>0){
                                    cardContainer.shuffle();
                                    shuffleCount-=1;
                                }else{
                                    new MyDialog("洗牌次数已用完！");
                                }
                            }
                        }else{
                            cardContainer.clickCard(x,y);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        this.addMouseListener(l);
    }
    //加载音乐和图片
    public void upload() throws IOException {
        //加载背景
        this.background= ImageIO.read(getClass().getResource("/bg/background.png"));
        this.startImage= ImageIO.read(getClass().getResource("/image/start.png"));
        this.vectorImage= ImageIO.read(getClass().getResource("/image/vector.PNG"));
        this.defeatImage= ImageIO.read(getClass().getResource("/image/defeat.PNG"));
        this.processBarImage= ImageIO.read(getClass().getResource("/image/processBar.png"));
    }


    //开始游戏
    public void start(int dif) throws IOException {
        difficulty=dif;
        cardTypes=DIFFICULTYTYPES[difficulty];
        cardGroups=DIFFICULTYGRUOPS[difficulty];
        cardUknows=DIFFICULTYUNKNOW[difficulty];
        currentCardCounts=cardGroups*cardTypes;
        moveoutCount=1;//移除机会
        withdrawCount=1;//撤销机会
        shuffleCount=1;//打乱机会
        this.cardContainer=new CardContainer(this);
        this.cardSlot=new CardSlot(this);
        this.btnContainer=new BtnContainer(this);
        this.add(cardContainer);
        this.add(cardSlot);
        this.add(btnContainer);
        cardContainer.startGame();
        cardSlot.startGame();
    }

    @Override
    public void paint(Graphics g){
        if (this.state==START){
            g.drawImage(startImage, 0, 0, 612,1150,null);
        }else if (this.state==GAME_OVER){
            g.drawImage(defeatImage, 0, 0, 612,1150,null);
        }else if (this.state==VECTOR){
            g.drawImage(vectorImage, 0, 0, 612,1150,null);
        }else{
            // 绘制背景
            g.drawImage(background, 0, 0, 612,1150,null);
            //绘制牌库
            this.cardContainer.paint(g);
            //绘制卡槽
            this.cardSlot.paint(g);
            //绘制按钮
            this.btnContainer.paint(g);
        }
    }

    public void action() throws Exception{
        while (true){
            repaint();
            //100FPS
            Thread.sleep(1000/100);
        }
    }

    public void gameover() {
        this.state=GAME_OVER;
        System.out.println("游戏失败！");
    }

    public void victory() {
        this.state=VECTOR;
        System.out.println("游戏胜利！");
    }

    public void addToSlot(Card card){
        cardContainer.updateCardCount(card,-1);
        cardSlot.addCard(card);
    }

    public void addToArea(Card card,int targetX,int targetY,int idx){
        cardContainer.moveCard(card,targetX,targetY,false,idx);
    }

    public void startMusic(){
        playMusic.play();
    }

    public void updateCardCount(Card card,int count){
        cardContainer.updateCardCount(card,count);
    }

    public int[] getSlotNextPos(Card card){
        return cardSlot.getNextPositon(card);
    }

    public int[] getUnknowNextPos(Card card){
        return cardContainer.getUnknowNextPos(card);
    }
}
