package com.nihar_raut.musicplayer;

import java.io.File;
import java.io.IOException;
import java.util.*;

import DataModel.Song;
import com.mpatric.mp3agic.*;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

public class Controller {

    @FXML
    private FontIcon play_pause_icon;
    @FXML
    private Slider songSlider;
    @FXML
    private TableView<Song> songTableView;
    @FXML
    private TableColumn<Song, String> nameColumn;
    @FXML
    private TableColumn<Song, String> durationColumn;
    @FXML
    private TableColumn<Song, String> artistColumn;
    @FXML
    private VBox songBar;

    private ObservableList<Song> songs;
    private ArrayList<MediaPlayer> players;

    private String dirPath;


    public void initialize(){
        songs = FXCollections.observableArrayList();
        players = new ArrayList<>();
        dirPath = "D:\\Projects\\JavaSoftwareProjects\\MusicPlayer\\MusicLibrary";

        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Song, String> songStringCellDataFeatures) {
                return songStringCellDataFeatures.getValue().songNameProperty();
            }
        });
        String file = "MusicLibrary\\Ed Sheeran - Perfect 320kbps(Mobmirchi.in).mp3";

        File audioFile = new File(file);
        try {
            Mp3File mp3File =new Mp3File(audioFile.getPath());
            ID3v2 tag = mp3File.getId3v2Tag();
            Song newSong = new Song("0", tag.getTitle(),
                    formatDuration(mp3File.getLengthInSeconds()),
                    tag.getArtist(),
                    tag.getAlbum(),
                    tag.getUrl());
        }catch (IOException e){
            e.printStackTrace();
        }catch (UnsupportedTagException e){
            System.out.println("unsupported tag");
        }catch (InvalidDataException e){
            System.out.println("Invalid Data");
        }
        songTableView.setItems(songs);
//        String file = "MusicLibrary\\Ed Sheeran - Perfect 320kbps(Mobmirchi.in).mp3";
//
//        File audioFile = new File(file);
//        try {
//            Mp3File mp3File =new Mp3File(audioFile.getPath());
//            ID3v2 tag = mp3File.getId3v2Tag();
//            System.out.println(tag.getArtist());
//            System.out.println(tag.getAlbumArtist());
//            System.out.println(tag.getLength());
//            System.out.println(mp3File.getLengthInSeconds()/60 + "  " + mp3File.getLengthInSeconds() % 60);
//            System.out.println(tag.getTitle());
//        }catch (IOException e){
//            e.printStackTrace();
//        }catch (UnsupportedTagException e){
//            System.out.println("unsupported tag");
//        }catch (InvalidDataException e){
//            System.out.println("Invalid Data");
//        }
//
//        Media media = new Media(audioFile.toURI().toString());
//        MediaPlayer player =new MediaPlayer(media);
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

    @FXML
    public void handleMouseClicked(){

    }

    public String formatDuration(long duration){
        long seconds = duration;
        if((seconds%60) < 10){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(seconds/60);
            stringBuilder.append(":");
            stringBuilder.append("0");
            stringBuilder.append(seconds%60);
            System.out.println(stringBuilder);
            return stringBuilder.toString();
        }else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(seconds/60);
            stringBuilder.append(":");
            stringBuilder.append(seconds%60);
            System.out.println(stringBuilder);
            return stringBuilder.toString();
        }
    }

}
