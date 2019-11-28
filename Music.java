package Sounds;



import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Music 
{
    private static boolean tryToInterruptSound = false;
    private static long mainTimeOut = 3000;
    private static long startTime = System.currentTimeMillis();

    public static synchronized Thread Music(final File file) {

        Thread soundThread = new Thread() {
            @Override
            public void run() {
                try{
                    Clip clip = null;
                    AudioInputStream inputStream = null;
                    clip = AudioSystem.getClip();
                    inputStream = AudioSystem.getAudioInputStream(file);
                    AudioFormat format = inputStream.getFormat();
                    long audioFileLength = file.length();
                    int frameSize = format.getFrameSize();
                    float frameRate = format.getFrameRate();
                    long durationInMiliSeconds = 
                            (long) (((float)audioFileLength / (frameSize * frameRate)) * 1000);

                    clip.open(inputStream);
                    clip.start();
                    System.out.println("" + (System.currentTimeMillis() - startTime) + ": sound started playing!");
                    System.out.println("i'm here 1");
                    Thread.sleep(durationInMiliSeconds);
                    while (true) {
                        if (!clip.isActive()) {
                            System.out.println("" + (System.currentTimeMillis() - startTime) + ": sound got to it's end!");
                            break;
                        }
                        long fPos = (long)(clip.getMicrosecondPosition() / 1000);
                        long left = durationInMiliSeconds - fPos;
                        System.out.println("" + (System.currentTimeMillis() - startTime) + ": time left: " + left);
                        if (left > 0) Thread.sleep(left);
                    }
                    clip.stop();  
                    System.out.println("" + (System.currentTimeMillis() - startTime) + ": sound stoped");
                    clip.close();
                    inputStream.close();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.out.println("" + (System.currentTimeMillis() - startTime) + ": sound interrupted while playing.");
                }
            }
        };
        soundThread.setDaemon(true);
        soundThread.start();
        return soundThread;
    }

    public static void main(String[] args) {
        Thread soundThread = Music(new File("C:\\Users\\giuli\\BattagliaNavale\\BattagliaNavale-shipsList\\BattagliaNavale\\src\\Sounds\\gameSounds\\VictorySoundEffect.wav"));
        System.out.println("" + (System.currentTimeMillis() - startTime) + ": playSound returned, keep running the code");
        try 
        {   
           Thread.sleep(mainTimeOut*3); //change this for the time duration
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (tryToInterruptSound) {
            try {   
                soundThread.interrupt();
                Thread.sleep(1);
                // Sleep in order to let the interruption handling end before
                // exiting the program (else the interruption could be handled
                // after the main thread ends!).
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("" + (System.currentTimeMillis() - startTime) + ": End of main thread; exiting program " + 
                (soundThread.isAlive() ? "killing the sound deamon thread" : ""));
        Thread soundThread1 = Music(new File("C:\\Users\\giuli\\BattagliaNavale\\BattagliaNavale-shipsList\\BattagliaNavale\\src\\Sounds\\gameSounds\\BombTimerSoundEffect.wav"));
        System.out.println("" + (System.currentTimeMillis() - startTime) + ": playSound returned, keep running the code");
        try 
        {   
           Thread.sleep(mainTimeOut*3); //change this for the time duration
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (tryToInterruptSound) {
            try {   
                soundThread1.interrupt();
                Thread.sleep(1);
                // Sleep in order to let the interruption handling end before
                // exiting the program (else the interruption could be handled
                // after the main thread ends!).
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("" + (System.currentTimeMillis() - startTime) + ": End of main thread; exiting program " + 
                (soundThread.isAlive() ? "killing the sound deamon thread" : ""));
    }
}