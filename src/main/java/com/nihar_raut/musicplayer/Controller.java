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
import javafx.scene.control.*;
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
    @FXML
    private Label songStopLabel;

    private ObservableList<Song> songs;
    private Map<Integer, MediaPlayer> players;

    private String dirPath;

    private Song currentSong;
    private MediaPlayer currentPlayer;

    public void initialize(){
        songs = FXCollections.observableArrayList();
        dirPath = "D:\\Projects\\JavaSoftwareProjects\\MusicPlayer\\MusicLibrary";
        loadSongs();
        createMediaPlayer();
        songBar.setDisable(true);
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
        currentSong = songTableView.getSelectionModel().getSelectedItem();

        playPauseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentPlayer = players.get(Integer.parseInt(currentSong.getId()));
                if(play_pause_icon.getIconLiteral().equals("cil-media-play")){
                    play_pause_icon.setIconLiteral("cil-media-pause");
                    currentPlayer.play();

                } else if (play_pause_icon.getIconLiteral().equals("cil-media-pause")) {
                    play_pause_icon.setIconLiteral("cil-media-play");
                    currentPlayer.pause();
                }
            }
        });
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

    public void handleMouseClicked(){
        songBar.setDisable(false);
        currentSong = songTableView.getSelectionModel().getSelectedItem();
        songStopLabel.setText(currentSong.getDuration());
    }
}
