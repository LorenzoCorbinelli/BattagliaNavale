package client;



import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundFX implements Runnable 
{
    //private static boolean tryToInterruptSound = false;
    //private static long mainTimeOut;
    private Clip clip;
    private AudioInputStream inputStream;
    private long durationInMiliSeconds;
    private static Thread t;
    
    private static BufferedInputStream f;
    
    public SoundFX(final InputStream file)
    {
        try {
            t = new Thread(this);
            this.f=new BufferedInputStream(file);
            this.inputStream = AudioSystem.getAudioInputStream(this.f);
            DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
            clip = (Clip)AudioSystem.getLine(info);
            t.start();
        } catch (LineUnavailableException ex) {} catch (UnsupportedAudioFileException | IOException ex) {
            System.out.println(ex);
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
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
            Logger.getLogger(SoundFX.class.getName()).log(Level.SEVERE, null, ex);
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