import sun.audio.AudioPlayer;
import sun.audio.AudioStream;


import java.io.IOException;

public class PlayMusic {

    public AudioStream as;

    public PlayMusic(){
        load();
    }

    public void load() {
        try {
            as=new AudioStream(getClass().getResource("/music/c.wav").openStream());
        }catch (Exception ex){
            System.out.println(ex.getStackTrace());
        }
    }

    public void play(){
        //多线程播放
        Thread playThread = createPlayThread();
        playThread.start();

    }

    public Thread createPlayThread(){
        Thread thread = new Thread(()->{
            try{
                as=new AudioStream(getClass().getResource("/music/c.wav").openStream());
                if (as!=null){
                    AudioPlayer.player.start(as);
                }
            }catch (Exception ex){

            }
        });
        return thread;
    }


}
