package sounds;



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
    private static boolean tryToInterruptSound = false;
    //private static long mainTimeOut;
    private static long startTime = System.currentTimeMillis();
    private static Clip clip;
    private static AudioInputStream inputStream;
    private static AudioFormat format;
    private static long audioFileLength;
    private static int frameSize;
    private static float frameRate;
    private static long durationInMiliSeconds;
    
    private static File f;
    
    public Music(final File file,long duration) throws LineUnavailableException, UnsupportedAudioFileException, IOException 
    {
        this.f=file;
        //this.mainTimeOut = duration;
        this.clip = null;
        this.inputStream = null;
        this.clip = AudioSystem.getClip();
        this.inputStream = AudioSystem.getAudioInputStream(this.f);
        this.format = this.inputStream.getFormat();
        this.audioFileLength = this.f.length();
        this.frameSize = this.format.getFrameSize();
        this.frameRate = this.format.getFrameRate();
        this.durationInMiliSeconds = (long)(((float)this.audioFileLength / (this.frameSize * this.frameRate)) * 1000);
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
        } 
        catch (LineUnavailableException ex) 
        {
            Logger.getLogger(Music.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Music.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InterruptedException ex) 
        {
            Logger.getLogger(Music.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Play() throws LineUnavailableException, IOException, InterruptedException
    {
        try
        {
        this.clip.open(this.inputStream);
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
        catch(Exception e )
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