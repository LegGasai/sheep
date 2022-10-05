import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

//卡槽类
public class CardSlot extends JPanel {
    //卡槽最大数量
    public static final int MAXNUMBER=7;
    public int []map;
    public LinkedList<Card> cardList;
    public BufferedImage image;
    public GuiApplication application;
    public int offset=38;
    public boolean isAllowWithdraw=true;
    public Card lastCard;
    public CardSlot(GuiApplication application) throws IOException {
        //加载卡槽图片
        this.image= ImageIO.read(getClass().getResource("/image/slot.png"));
        this.map=new int[14];
        this.application=application;
        this.cardList=new LinkedList<>();
        this.lastCard=null;
    }

    public void startGame(){
        this.cardList.clear();
        this.isAllowWithdraw=true;
        this.map=new int[14];
    }

    public void removeAll(int index){
        for (int i = 0; i < 3; i++) {
            cardList.remove(index);
        }
    }

    //添加牌到卡槽
    public void addCard(Card card){
        //add
        lastCard=card;
        int id = card.type.ordinal();
        map[id]++;
        int index=-1;
        for (int i = 0; i < cardList.size(); i++) {
            if (cardList.get(i).type.ordinal()==id){
                index=i;
                break;
            }
        }
        if(index==-1){
            cardList.add(card);
        }else{
            cardList.add(index,card);
        }
        isAllowWithdraw=true;
        if(map[id]==3){
            //消除
            disapper(index);
        }else{
            if (cardList.size()==MAXNUMBER){
                application.gameover();
            }
        }
    }

    //从卡槽移出
    public boolean moveOut(){
        if(cardList.size()==0){
            return false;
        }
        int count=0;
        while (!cardList.isEmpty()&&count<3){
            Card removeCard = cardList.remove(0);
            map[removeCard.type.ordinal()]--;
            int targetX=185+count*72+36;
            int targetY=670+36;
            removeCard.level=100+count;
            application.addToArea(removeCard,targetX,targetY,count);
            application.updateCardCount(removeCard,+1);
            count++;
        }
        isAllowWithdraw=false;
        return true;
    }


    //撤销操作
    public boolean withdraw(){
        if(!isAllowWithdraw||cardList.size()==0) {
            return false;
        }

        Card removeCard = lastCard;
        cardList.remove(lastCard);
        map[removeCard.type.ordinal()]--;

        int targetX=-1;
        int targetY=-1;
        //恢复主牌区的位置
        if (removeCard.level>=0&&removeCard.level<100){
            targetX=48+removeCard.col*36+36;
            targetY=48+removeCard.row*36+36;
        }else if (removeCard.level>=100){
            //移出区
            targetX=185+(removeCard.level-100)*72+36;
            targetY=670+36;
        }else if (removeCard.level==-1||removeCard.level==-2){
            //左右盲牌区
            int[] unknowNextPos = application.getUnknowNextPos(removeCard);
            targetX=unknowNextPos[0];
            targetY=unknowNextPos[1];
        }
        //move
        application.addToArea(removeCard,targetX,targetY,0);
        application.updateCardCount(removeCard,+1);
        return true;
    }


    public void disapper(int startIndex){
        //播放消失动画
        //System.out.println(startIndex);
        for (int i = 0; i < 6; i++) {
            new Timer().schedule(getNewTask(0,startIndex),30*i);
        }
        //删除
        new Timer().schedule(getNewTask(1,startIndex),200);
    }
    public void reduce(int startIndex){
        for(int i=startIndex;i<startIndex+3;i++){
            Card card = cardList.get(i);
            card.height-=12;
            card.width-=12;
        }
    }
    public void delete(int startIndex){
        Card card=cardList.get(startIndex);
        int id=card.type.ordinal();
        removeAll(startIndex);
        map[id]=0;
        application.updateCardCount(card,-3);
        application.currentCardCounts-=3;
        if (application.currentCardCounts==0){
            application.victory();
        }
        isAllowWithdraw=false;
        lastCard=null;
    }

    public TimerTask getNewTask(int type,int startIndex){
        if (type==0){
            return new TimerTask() {
                @Override
                public void run() {
                    reduce(startIndex);
                }
            };
        }else if(type==1){
            return new TimerTask() {
                @Override
                public void run() {
                    delete(startIndex);
                }
            };
        }else{
            return null;
        }
    }

    public int[] getNextPositon(Card card){
        int index=cardList.size();
        for (int i = cardList.size()-1; i >= 0; i--) {
            if (cardList.get(i).type.ordinal()==card.type.ordinal()){
                index=i;
                break;
            }
        }
        if(index!=cardList.size()){
            index++;
        }
        int x=offset+index*74+36;
        int y=823+36;
        return new int[]{x,y};
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(this.image,0,770,595,180,null);
        //卡槽里的牌
        for (int i = 0; i < this.cardList.size(); i++) {
            Card card = this.cardList.get(i);
            g.drawImage(card.image,offset+i*74+36-card.width/2,823+36-card.height/2,card.width,card.height,null);
        }
    }

}
