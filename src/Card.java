import java.awt.image.BufferedImage;

//卡片类
public class Card {

    public enum Type{
        //叉子
        ChaZi,
        //木桩
        MuZhuang,
        //萝卜
        LuoBo,
        //白菜
        BaiCai,
        //梳子
        ShuZi,
        //剪刀
        JianDao,
        //羊毛
        YangMao,
        //玉米
        YuMi,
        //线团
        XianTuan,
        //草垛
        CaoDuo,
        //火炬
        HuoJu,
        //水桶
        ShuiTong,
        //奶瓶
        NaiPing,
        //手套
        ShouTao,
    }

    public Type type;
    public int width=72;
    public int height=72;
    public int x;
    public int y;
    public int row;
    public int col;
    public int level;
    public BufferedImage image;
    public boolean isTop;
    public int coverCount=0;
    public Card(Type type,int x,int y,int row,int col,int level,BufferedImage image,boolean isTop) {
        this.type=type;
        this.x=x;
        this.y=y;
        this.row=row;
        this.col=col;
        this.level=level;
        this.image = image;
        this.isTop=isTop;
    }

    @Override
    public String toString() {
        return "Card{" +
                "type=" + type +
                ", width=" + width +
                ", height=" + height +
                ", x=" + x +
                ", y=" + y +
                ", row=" + row +
                ", col=" + col +
                ", level=" + level +
                ", image=" + image +
                ", isTop=" + isTop +
                '}';
    }
}
