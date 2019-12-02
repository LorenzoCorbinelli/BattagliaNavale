package sounds;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author giuli
 */
public class Main 
{
    public static void main(String[] args) throws LineUnavailableException, UnsupportedAudioFileException, IOException, InterruptedException 
    {
        try
        {
            Music m = new Music(new BufferedInputStream(Main.class.getResourceAsStream("/sound/VictorySoundEffect.wav")),8000);
            m.run();
        }
        catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) 
        {
            e.printStackTrace();
        }
        
    } 
}
