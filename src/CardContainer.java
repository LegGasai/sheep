import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

//卡片容器
public class CardContainer extends JPanel {

    public BufferedImage[] cardImages;
    public BufferedImage[] darkCardImages;
    public GuiApplication application;
    public int offsetX=48;
    public int offsetY=48;
    public Card[][][] cardsMatric;
    public int[] cardCounts;
    public ArrayList<Card> left;
    public ArrayList<Card> right;
    public ArrayList<Card> moveList;
    public BufferedImage hiddenImagel;
    public BufferedImage hiddenImager;
    public Card[] movingCard;
    public int[][] dic=new int[][]{{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1},{0,0}};
    public CardContainer(GuiApplication application) throws IOException {
        this.application=application;
        this.cardImages=new BufferedImage[GuiApplication.cardTypes];
        this.darkCardImages=new BufferedImage[GuiApplication.cardTypes];
        this.cardsMatric=new Card[13][13][7];
        this.cardCounts=new int[GuiApplication.cardTypes];
        this.left=new ArrayList<>();
        this.right=new ArrayList<>();
        this.moveList=new ArrayList<>();
        this.hiddenImagel=ImageIO.read(getClass().getResource("/image/hiddenl.png"));
        this.hiddenImager=ImageIO.read(getClass().getResource("/image/hiddenr.png"));
        init();
    }
    public void startGame(){
        this.left.clear();
        this.right.clear();
        this.moveList.clear();
        this.moveList.add(null);
        this.moveList.add(null);
        this.moveList.add(null);
        this.cardsMatric=new Card[13][13][7];
        this.cardCounts=new int[GuiApplication.cardTypes];
        this.movingCard=new Card[3];
        this.generate();
    }

    public void init() throws IOException{
        //加载图片
        upload();
        //生成卡牌库
        generate();
    }

    public void upload() throws IOException{
        for (int i = 0; i < cardImages.length; i++) {
            this.cardImages[i]= ImageIO.read(getClass().getResource("/image/common/"+i+".png"));
        }
        for (int i = 0; i < darkCardImages.length; i++) {
            this.darkCardImages[i]=ImageIO.read(getClass().getResource("/image/dark/"+i+".png"));
        }
    }

    //当前位置是否已经存在牌
    public boolean checkHave(int x,int y,int level){
        for (int k = 0; k < 9; k++) {
            int nx=x+dic[k][0];
            int ny=y+dic[k][1];
            if (nx>=0&&ny>=0&&nx<13&&ny<13&&cardsMatric[nx][ny][level]!=null){
                return true;
            }
        }
        return false;
    }

    //生成卡牌库
    public void generate(){
        Arrays.fill(this.cardCounts,GuiApplication.cardGroups);
        int[] currentcardCounts=new int[GuiApplication.cardTypes];
        int total=0;
        while (total<GuiApplication.cardTypes*(GuiApplication.cardGroups-GuiApplication.cardUknows)){
            //生成三维位置
            int randomx=(int)(Math.random()*(12-0+1)+0);
            int randomy=(int)(Math.random()*(12-0+1)+0);
            int randomLevel=(int)(Math.random()*(6-0+1)+0);

            if(checkHave(randomx,randomy,randomLevel)){
                continue;
            }
            Card card=new Card(null,offsetY+randomy*36+36, offsetX+randomx*36+36,randomx,randomy, randomLevel,null,true);
            //生成随机图案
            while (true){
                int random=(int)(Math.random()*(GuiApplication.cardTypes-0)+0);
                if (currentcardCounts[random]<cardCounts[random]){
                    card.image=cardImages[random];
                    card.type= Card.Type.values()[random];
                    currentcardCounts[random]++;
                    break;
                }
            }
            cardsMatric[randomx][randomy][randomLevel]=card;
            total++;
            //是否被其他牌覆盖
            checkCover(card);
            //覆盖所有底部的牌
            cover(card);
        }
        //生成盲牌区
        while (total<GuiApplication.cardTypes*(GuiApplication.cardGroups)){
            Card card=new Card(null,-1,-1,-1,-1,-1,null,true);
            while (true){
                int random=(int)(Math.random()*(GuiApplication.cardTypes-0)+0);
                if (currentcardCounts[random]<cardCounts[random]){
                    card.image=cardImages[random];
                    card.type= Card.Type.values()[random];
                    currentcardCounts[random]++;
                    break;
                }
            }
            total++;
            if (left.size()<(GuiApplication.cardUknows/2)*GuiApplication.cardTypes){
                card.x=48+(left.size()-1)*9;
                card.y=580;
                left.add(card);
            }else{
                card.level=-2;
                card.x=552-(right.size()-1)*9-card.width;
                card.y=580;
                right.add(card);
            }
        }
    }

    //覆盖底部所有的牌
    public void cover(Card card){
        if(card.level==0){
            return;
        }
        for (int k = 0; k < 9; k++) {
            int nx=card.row+dic[k][0];
            int ny=card.col+dic[k][1];
            for (int level=0;level<card.level;level++){
                if (nx<0||ny<0||nx>=13||ny>=13||cardsMatric[nx][ny][level]==null){
                    continue;
                }
                Card bottomCard=cardsMatric[nx][ny][level];
                bottomCard.coverCount+=1;
                if (bottomCard.coverCount>0&&bottomCard.isTop){
                    bottomCard.isTop=false;
                    bottomCard.image=darkCardImages[bottomCard.type.ordinal()];
                }
            }
        }
    }

    //清除覆盖
    public void unCover(Card card){
        if(card.level==0){
            return;
        }
        for (int k = 0; k < 9; k++) {
            int nx=card.row+dic[k][0];
            int ny=card.col+dic[k][1];
            for (int level=0;level<card.level;level++){
                if (nx<0||ny<0||nx>=13||ny>=13||cardsMatric[nx][ny][level]==null){
                    continue;
                }
                Card bottomCard=cardsMatric[nx][ny][level];
                bottomCard.coverCount-=1;
                if (bottomCard.coverCount==0){
                    bottomCard.isTop=true;
                    bottomCard.image=cardImages[bottomCard.type.ordinal()];
                }
            }
        }
    }

    //检查覆盖
    public void checkCover(Card card){
        if (card.level==6){
            return;
        }
        for (int k = 0; k < 9; k++) {
            int nx=card.row+dic[k][0];
            int ny=card.col+dic[k][1];
            for (int level=card.level+1;level<7;level++){
                if (nx<0||ny<0||nx>=13||ny>=13||cardsMatric[nx][ny][level]==null){
                    continue;
                }
                card.coverCount+=1;
                if (card.coverCount>0&&card.isTop){
                    card.isTop=false;
                    card.image=darkCardImages[card.type.ordinal()];
                }
            }
        }
    }

    //随机打乱
    public void shuffle() throws Exception{
        //缩小
        for (int i = 0; i < 6; i++) {
            new Timer().schedule(getNewTask(0),100*i);
        }
        //打乱
        new Timer().schedule(getNewTask(2),600);
        //放大
        for (int i = 0; i < 6; i++) {
            new Timer().schedule(getNewTask(1),600+100*i);
        }
    }
    public void moveCard(Card card,int targetX,int targetY,boolean isOut,int idx){
        this.movingCard[idx]=card;
        int totalX=targetX-card.x;
        int totalY=targetY-card.y;
        for (int i = 0; i < 10; i++) {
            int diffX=totalX/10;
            int diffY=totalY/10;
            if (i==9){
                diffX+=totalX%10;
                diffY+=totalY%10;
            }
            new Timer().schedule(getNewTask(3,idx,diffX,diffY),10*i);
        }
        if (isOut){
            new Timer().schedule(getNewTask(4,idx),110);
        }else{
            new Timer().schedule(getNewTask(5,idx),110);
        }
    }

    public TimerTask getNewTask(int type,int ...args){
        if (type==0){
            return new TimerTask() {
                @Override
                public void run() {
                    reduce();
                }
            };
        }
        else if (type==1) {
            return new TimerTask() {
                @Override
                public void run() {
                    enlarge();
                }
            };
        }else if (type==2){
            return new TimerTask() {
                @Override
                public void run() {
                    shuffleCard();
                }
            };
        }else if (type==3){
            return new TimerTask() {
                @Override
                public void run() {
                    move(movingCard[args[0]],args[1],args[2]);
                }
            };
        }else if (type==4){
            return new TimerTask() {
                @Override
                public void run() {
                    finishMoveOut(args[0]);
                }
            };
        }else if (type==5){
            return new TimerTask() {
                @Override
                public void run() {
                    finishMoveIn(args[0]);
                }
            };
        }else{
            return null;
        }
    }

    //缩小卡牌
    public void reduce(){
        //主牌区
        for (int k = 0; k < 7; k++) {
            for (int i = 0; i < 13; i++) {
                for (int j = 0; j < 13; j++) {
                    if (cardsMatric[i][j][k]==null) {
                        continue;
                    }
                    Card card = cardsMatric[i][j][k];
                    card.height-=12;
                    card.width-=12;
                }
            }
        }
        //盲牌区
        if (left.size()>0){
            Card card = left.get(left.size()-1);
            card.height-=12;
            card.width-=12;
        }
        if (right.size()>0){
            Card card = right.get(right.size()-1);
            card.height-=12;
            card.width-=12;
        }
        //移除区
        if (moveList.size()>0){
            for (int i = 0; i < moveList.size(); i++) {
                if(moveList.get(i)!=null){
                    Card card = moveList.get(i);
                    card.height-=12;
                    card.width-=12;
                }
            }
        }
    }
    //放大卡牌
    public void enlarge(){
        //主牌区
        for (int k = 0; k < 7; k++) {
            for (int i = 0; i < 13; i++) {
                for (int j = 0; j < 13; j++) {
                    if (cardsMatric[i][j][k]==null) {
                        continue;
                    }
                    Card card = cardsMatric[i][j][k];
                    card.height+=12;
                    card.width+=12;
                }
            }
        }
        //盲牌区
        if (left.size()>0){
            Card card = left.get(left.size()-1);
            card.height+=12;
            card.width+=12;
        }
        if (right.size()>0){
            Card card = right.get(right.size()-1);
            card.height+=12;
            card.width+=12;
        }
        //移除区
        if (moveList.size()>0){
            for (int i = 0; i < moveList.size(); i++) {
                if (moveList.get(i)!=null){
                    Card card = moveList.get(i);
                    card.height+=12;
                    card.width+=12;
                }
            }
        }
    }
    //随机打乱卡牌
    public void shuffleCard(){
        int[] currentcardCounts=new int[14];
        //主牌区打乱
        for (int k = 0; k < 7; k++) {
            for (int i = 0; i < 13; i++) {
                for (int j = 0; j < 13; j++) {
                    if (cardsMatric[i][j][k]==null) {
                        continue;
                    }
                    Card card = cardsMatric[i][j][k];
                    while (true){
                        int random=(int)(Math.random()*(13-0+1)+0);
                        if (currentcardCounts[random]<cardCounts[random]){
                            card.type= Card.Type.values()[random];
                            if (card.isTop){
                                card.image=cardImages[random];
                            }
                            else{
                                card.image=darkCardImages[random];
                            }
                            currentcardCounts[random]++;
                            break;
                        }
                    }
                }
            }
        }
        //盲牌区
        if (left.size()>0){
            for (int i = 0; i < left.size(); i++) {
                Card card = left.get(i);
                while (true){
                    int random=(int)(Math.random()*(13-0+1)+0);
                    if (currentcardCounts[random]<cardCounts[random]){
                        card.type= Card.Type.values()[random];
                        if (card.isTop){
                            card.image=cardImages[random];
                        }
                        else{
                            card.image=darkCardImages[random];
                        }
                        currentcardCounts[random]++;
                        break;
                    }
                }
            }
        }
        if (right.size()>0){
            for (int i = 0; i < right.size(); i++) {
                Card card = right.get(i);
                while (true){
                    int random=(int)(Math.random()*(13-0+1)+0);
                    if (currentcardCounts[random]<cardCounts[random]){
                        card.type= Card.Type.values()[random];
                        if (card.isTop){
                            card.image=cardImages[random];
                        }
                        else{
                            card.image=darkCardImages[random];
                        }
                        currentcardCounts[random]++;
                        break;
                    }
                }
            }
        }
        //移除牌区
        if (moveList.size()>0){
            for (int i = 0; i < moveList.size(); i++) {
                Card card = moveList.get(i);
                while (true){
                    int random=(int)(Math.random()*(13-0+1)+0);
                    if (currentcardCounts[random]<cardCounts[random]){
                        card.type= Card.Type.values()[random];
                        if (card.isTop){
                            card.image=cardImages[random];
                        }
                        else{
                            card.image=darkCardImages[random];
                        }
                        currentcardCounts[random]++;
                        break;
                    }
                }
            }
        }
    }
    //移动卡牌
    public void move(Card card,int diffX,int diffY){
        card.x+=diffX;
        card.y+=diffY;

    }
    //完成移出，卡牌->卡槽
    public void finishMoveOut(int idx){
        application.addToSlot(movingCard[idx]);
        movingCard[idx]=null;
    }
    //完成移入，卡槽->卡牌
    public void finishMoveIn(int idx){
        this.addCard(movingCard[idx]);
        movingCard[idx]=null;
    }
    //点击事件
    public void clickCard(int x,int y){
        //点击的是随机卡牌区
        if(y>=580&&y<=652){
            int leftSize=left.size();
            int rightSize=right.size();
            if (leftSize>0&&(x>=48+(leftSize-1)*9&&x<=48+(leftSize-1)*9+72)){
                Card card=left.get(leftSize-1);
                //移动到卡槽
                left.remove(leftSize-1);
                int[] slotNextPos = application.getSlotNextPos(card);
                moveCard(card,slotNextPos[0],slotNextPos[1],true,0);
                application.startMusic();
            }else if(rightSize>0&&(x>=552-(rightSize-1)*9-72&&x<=552-(rightSize-1)*9)){
                Card card=right.get(rightSize-1);
                //移动到卡槽
                right.remove(rightSize-1);
                int[] slotNextPos = application.getSlotNextPos(card);
                moveCard(card,slotNextPos[0],slotNextPos[1],true,0);
                application.startMusic();
            }
        }
        //主牌区
        else if (y>=48&&y<=552&&x>=48&&y<=552){
            int col=(x-48)/36;
            int row=(y-48)/36;
            int [][]choices=new int[][]{{0,0},{-1,-1},{-1,0},{0,-1}};
            //寻找第一个可被点击的牌
            //System.out.println(row+","+col);
            for (int level = 6; level >= 0; level--) {
                for (int k = 0; k < 4; k++) {
                    int nrow=row+choices[k][0];
                    int ncol=col+choices[k][1];
                    if (nrow>=0&&ncol>=0&&ncol<=12&&nrow<=12){
                        if (cardsMatric[nrow][ncol][level]!=null&&cardsMatric[nrow][ncol][level].isTop){
                            Card card = cardsMatric[nrow][ncol][level];
                            //System.out.println(card);
                            //移入卡槽
                            cardsMatric[nrow][ncol][level]=null;
                            int[] slotNextPos = application.getSlotNextPos(card);
                            moveCard(card,slotNextPos[0],slotNextPos[1],true,0);
                            application.startMusic();
                            //修改覆盖关系
                            unCover(card);
                            return;
                        }
                    }
                }
            }
        }
        //移除区
        else if (y>=670&&y<=670+72){
            if(moveList.size()==0) {
                return;
            }
            int index=(x-185)/72;
            if (moveList.get(index)!=null){
                Card card = moveList.get(index);
                moveList.set(index,null);
                int[] slotNextPos = application.getSlotNextPos(card);
                moveCard(card,slotNextPos[0],slotNextPos[1],true,0);
                application.startMusic();
            }
        }
    }
    public void addCard(Card card){
        //添加到移除区
        if (card.level>=100){
            int index=card.level-100;
            moveList.set(index,card);
            return;
        }
        //添加到盲牌区
        if(card.level==-1||card.level==-2){
            if (card.level==-1){
                left.add(card);
            }else{
                right.add(card);
            }
            return;
        }
        //添加到主牌区
        if (card.level>=0){
            cardsMatric[card.row][card.col][card.level]=card;
            //修改覆盖关系
            cover(card);
            return;
        }
    }
    public void updateCardCount(Card card,int count){
        cardCounts[card.type.ordinal()]+=count;
    }


    public int [] getUnknowNextPos(Card card){
        if(card.level==-1){
            return new int[]{left.size()*9+48,580};
        }else{
            return new int[]{552-right.size()*9-card.width,580};
        }
    }
    @Override
    public void paint(Graphics g){
        //主牌区
        for(int k = 0; k < 7;k++){
            for (int i = 0; i < 13; i++) {
                for (int j = 0; j < 13; j++) {
                    if (cardsMatric[i][j][k]==null) {
                        continue;
                    }
                    Card card = cardsMatric[i][j][k];
                    g.drawImage(card.image,card.x-card.width/2,card.y-card.height/2,card.width,card.height,null);
                }
            }
        }

        //底部左侧盲牌区域
        for (int i = 0; i < left.size(); i++) {
            Card card = left.get(i);
            if (i==left.size()-1){
                g.drawImage(card.image,48+i*9,580,card.width,card.height,null);
            }else{
                g.drawImage(hiddenImagel,48+i*9,580,10,card.height,null);
            }
        }
        //底部右侧盲牌区域
        for (int i = 0; i < right.size(); i++) {
            Card card = right.get(i);
            if (i==right.size()-1){
                g.drawImage(card.image,552-i*9-card.width,580,card.width,card.height,null);
            }else{
                g.drawImage(hiddenImager,552-i*9-9,580,10,card.height,null);
            }
        }
        //移出区
        for (int i = 0; i < moveList.size(); i++) {
            Card card = moveList.get(i);
            if (card!=null){
                g.drawImage(card.image,card.x-card.width/2,card.y-card.height/2,card.width,card.height,null);
            }
        }
        //虚拟的移动卡牌
        if (movingCard[0]!=null){
            Card card = movingCard[0];
            g.drawImage(card.image,card.x-card.width/2,card.y-card.height/2,card.width,card.height,null);
        }
        if (movingCard[1]!=null){
            Card card = movingCard[1];
            g.drawImage(card.image,card.x-card.width/2,card.y-card.height/2,card.width,card.height,null);
        }
        if (movingCard[2]!=null){
            Card card = movingCard[2];
            g.drawImage(card.image,card.x-card.width/2,card.y-card.height/2,card.width,card.height,null);
        }
    }
}
