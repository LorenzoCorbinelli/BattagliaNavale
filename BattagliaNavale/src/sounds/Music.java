package sounds;



import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Music implements Runnable 
{
    //private static boolean tryToInterruptSound = false;
    //private static long mainTimeOut;
    private final long startTime = System.currentTimeMillis();
    private Clip clip;
    private AudioInputStream inputStream;
    private long durationInMiliSeconds;
    
    private static BufferedInputStream f;
    
    public Music(final BufferedInputStream file,long duration) throws LineUnavailableException, UnsupportedAudioFileException, IOException 
    {
        this.f=file;
        //this.mainTimeOut = duration;
        this.clip = null;
        this.inputStream = null;
        this.clip = AudioSystem.getClip();
        this.inputStream = AudioSystem.getAudioInputStream(this.f);
    }
    
    public long getDurationSong()
    {
        return this.durationInMiliSeconds;
    }
    
    @Override
    public void run() 
    {
        System.out.println("I'm here");
        try 
        {
            this.Play();
            this.Stop();
            System.out.println("I don't hate you!");
        } 
        catch (LineUnavailableException | IOException | InterruptedException ex) 
        {
            Logger.getLogger(Music.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Play() throws LineUnavailableException, IOException, InterruptedException
    {
        try
        {
        this.clip.open(this.inputStream);
        this.durationInMiliSeconds = clip.getMicrosecondLength() / 1000;
        this.clip.start();
        Thread.sleep(this.durationInMiliSeconds);
        while (true) {
            if (!this.clip.isActive()) 
            {
                break;
            }
            long fPos = (long)(this.clip.getMicrosecondPosition() / 1000);
            long left = this.durationInMiliSeconds - fPos;
            if (left > 0) 
            {
                Thread.sleep(left);
            }
        }
        }
        catch(IOException | InterruptedException | LineUnavailableException e )
        {
            System.out.println(e);
        }
    }
    public void Stop() throws IOException
    {
        this.clip.stop();  
        this.clip.close();
        this.inputStream.close();
    }
    
    public boolean isAlive() 
    {
        return this.clip.isActive();
    }
}