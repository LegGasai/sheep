import javafx.scene.layout.Background;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

import java.io.File;

public class test {
    public AudioStream as;

    public test() {
        playMusic();
    }

    private void playMusic(){
        try {
            as=new AudioStream(getClass().getResource("/music/c.wav").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start(){

        AudioPlayer.player.start(as);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AudioPlayer.player.stop(as);
    }

    public static void main(String[] args){

        test test = new test();
        //test.start();
        //String[] s1=new String[2];
        //s1[1]=new String("123");
        //String a=s1[1];
        //System.out.println(a);
        //s1[1]=null;
        //System.out.println(a);
        //System.out.println(s1[1]);
        ////test.playSound();
        //test.stest("123");
        test.play();
    }

    public void playSound(){
        try {

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/music/c.wav"));
            Clip clip = AudioSystem.getClip( );
            clip.open(audioInputStream);
            clip.start( );
        }
        catch(Exception e) {
            e.printStackTrace( );
        }
    }

    public void stest(String ...s){
        System.out.println(s.length);
    }

    public void play(){
        URL url=this.getClass().getResource("/music/c.wav");
        AudioClip ac= Applet.newAudioClip(url);

        ac.play();
    }
}

