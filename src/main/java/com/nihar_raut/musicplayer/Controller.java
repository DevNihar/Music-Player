package com.nihar_raut.musicplayer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import DataModel.Song;
import com.mpatric.mp3agic.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    @FXML
    private Button playPauseButton;

    private ObservableList<Song> songs;
    private Map<Integer, MediaPlayer> players;

    private String dirPath;

    private boolean isPlaying = false;


    public void initialize(){
        songs = FXCollections.observableArrayList();
        dirPath = "D:\\Projects\\JavaSoftwareProjects\\MusicPlayer\\MusicLibrary";

//        File dir = new File(dirPath);
//        File[] fileList = dir.listFiles();
//        File audioFile = fileList[0];
//        Media media = new Media(new File(audioFile.getAbsolutePath()).toURI().toString());
//        MediaPlayer mediaPlayer = new MediaPlayer(media);
////        mediaPlayer.play();
//        System.out.println("=======================================");
        loadSongs();
        createMediaPlayer();
        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Song, String> songStringCellDataFeatures) {
                return songStringCellDataFeatures.getValue().songNameProperty();
            }
        });

        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Song, String> songStringCellDataFeatures) {
                return songStringCellDataFeatures.getValue().songNameProperty();
            }
        });
        durationColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Song, String> songStringCellDataFeatures) {
                return songStringCellDataFeatures.getValue().durationProperty();
            }
        });
        artistColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Song, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Song, String> songStringCellDataFeatures) {
                return songStringCellDataFeatures.getValue().artistNameProperty();
            }
        });
        songTableView.setItems(songs);

        playPauseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Song song = songTableView.getSelectionModel().getSelectedItem();
                MediaPlayer player = players.get(Integer.parseInt(song.getId()));
                if(play_pause_icon.getIconLiteral().equals("cil-media-play")){
                    play_pause_icon.setIconLiteral("cil-media-pause");
                    player.play();

                } else if (play_pause_icon.getIconLiteral().equals("cil-media-pause")) {
                    play_pause_icon.setIconLiteral("cil-media-play");
                    player.pause();
                }
            }
        });
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

    public void loadSongs(){
        File directory = new File(dirPath);
        File[] files =  directory.listFiles();
        int id = 0;
        for(File file: files){
//            System.out.println(file.getAbsolutePath());
            String name = file.getName();
//            System.out.println("Loading: " + name);
//            if(name.endsWith("m4a")){
//                file.delete();
//            }
            if(name.endsWith("mp3") || name.endsWith("wav")){

                try {
                    Mp3File mp3File =new Mp3File(file.getAbsolutePath());
                    ID3v2 tag = mp3File.getId3v2Tag();
                    if(tag == null){
                        continue;
                    }
                    id++;
//                    System.out.println(id);
                    Song newSong = new Song(String.valueOf(id), tag.getTitle()!=null ? tag.getTitle() : "-",
                            formatDuration(mp3File.getLengthInSeconds()),
                            tag.getArtist()!=null ? tag.getArtist(): "-",
                            tag.getAlbum()!=null ? tag.getAlbum() : "-",
                            file.getAbsolutePath());
//                    System.out.println(newSong);
                    songs.add(newSong);
                }catch (IOException e){
                    e.printStackTrace();
                }catch (UnsupportedTagException e){
                    System.out.println("unsupported tag");
                }catch (InvalidDataException e){
                    System.out.println("Invalid Data");
                }
            }
        }
    }


    public String formatDuration(long duration){
        long seconds = duration;
        if((seconds%60) < 10){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(seconds/60);
            stringBuilder.append(":");
            stringBuilder.append("0");
            stringBuilder.append(seconds%60);
//            System.out.println(stringBuilder);
            return stringBuilder.toString();
        }else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(seconds/60);
            stringBuilder.append(":");
            stringBuilder.append(seconds%60);
//            System.out.println(stringBuilder);
            return stringBuilder.toString();
        }
    }

    public void createMediaPlayer(){
        players = new HashMap<>();
        for(Song song: songs){
            Media media = new Media(new File(song.getUrl()).toURI().toString());
            MediaPlayer tempPlayer = new MediaPlayer(media);
            players.put(Integer.parseInt(song.getId()), tempPlayer);
        }
    }

}
