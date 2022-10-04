package com.nihar_raut.musicplayer;

import java.io.File;
import java.io.IOException;
import com.mpatric.mp3agic.*;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.kordamp.ikonli.javafx.FontIcon;

public class Controller {

    @FXML
    FontIcon play_pause_icon;
    @FXML
    Slider songSlider;

    public void initialize(){
        songSlider.setMaxWidth(500);

        String file = "MusicLibrary\\Ed Sheeran - Perfect 320kbps(Mobmirchi.in).mp3";

        File audioFile = new File(file);
        try {
            Mp3File mp3File =new Mp3File(audioFile.getPath());
            ID3v2 tag = mp3File.getId3v2Tag();
            System.out.println(tag.getArtist());
            System.out.println(tag.getAlbumArtist());
            System.out.println(tag.getLength());
            System.out.println(mp3File.getLengthInSeconds()/60 + "  " + mp3File.getLengthInSeconds() % 60);
            System.out.println(tag.getTitle());
        }catch (IOException e){
            e.printStackTrace();
        }catch (UnsupportedTagException e){
            System.out.println("unsupported tag");
        }catch (InvalidDataException e){
            System.out.println("Invalid Data");
        }

        Media media = new Media(audioFile.toURI().toString());
        MediaPlayer player =new MediaPlayer(media);
//        player.play();
    }

    @FXML
    public void handlePlayPause(){
        if(play_pause_icon.getIconLiteral().equals("cil-media-play")){
            play_pause_icon.setIconLiteral("cil-media-pause");
        } else if (play_pause_icon.getIconLiteral().equals("cil-media-pause")) {
            play_pause_icon.setIconLiteral("cil-media-play");
        }
    }

}
