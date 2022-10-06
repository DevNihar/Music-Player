package com.nihar_raut.musicplayer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import DataModel.Song;
import com.mpatric.mp3agic.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class Controller {

    @FXML
    private FontIcon play_pause_icon;
    @FXML
    private FontIcon muteIcon;
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
    private Button nextButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button muteButton;
    @FXML
    private Label songStopLabel;
    @FXML
    private ComboBox<String> sortComboBox;

    private ObservableList<Song> songs;
    private Map<String, MediaPlayer> players;

    private String dirPath;

    private Song currentSong;
    private MediaPlayer currentPlayer;

    public void initialize(){
        songs = FXCollections.observableArrayList();
        dirPath = "D:\\Projects\\JavaSoftwareProjects\\MusicPlayer\\MusicLibrary";
        loadSongs();
//        System.out.println(songs.size());
        createMediaPlayer();
        songBar.setDisable(true);
        sortComboBox.getSelectionModel().select(0);
//        System.out.println(players.size());
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
//        SortedList<Song> sortedList = new SortedList<>(songs, new Comparator<Song>() {
//            @Override
//            public int compare(Song o1, Song o2) {
//                int firstId = Integer.parseInt(o1.getId());
//                int secondId = Integer.parseInt(o2.getId());
//                return Integer.compare(firstId, secondId);
//            }
//        });
        SortedList<Song> sortedList= sort();
        songTableView.setItems(sortedList);
        songTableView.getSelectionModel().selectFirst();

        playPauseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Song currentSelection = songTableView.getSelectionModel().getSelectedItem();
                currentPlayer = players.get(currentSelection.getSongName());
                if(play_pause_icon.getIconLiteral().equals("cil-media-play")){
                    play_pause_icon.setIconLiteral("cil-media-pause");
                    currentPlayer.play();

                } else if (play_pause_icon.getIconLiteral().equals("cil-media-pause")) {
                    play_pause_icon.setIconLiteral("cil-media-play");
                    currentPlayer.pause();
                }
            }
        });
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(currentPlayer != null){
                    currentPlayer.pause();
                    currentPlayer.seek(new Duration(0.0));
                }

                int id = songTableView.getSelectionModel().getSelectedIndex();
                id++;

                if(id >= songTableView.getItems().size()){
                    id = 0;
                }
                Song currentSelection = songTableView.getItems().get(id);
                currentSong = currentSelection;
                currentPlayer = players.get(currentSelection.getSongName());
                songTableView.getSelectionModel().select(currentSelection);
                currentPlayer.play();
            }
        });
        prevButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(currentPlayer != null){
                    currentPlayer.pause();
                    currentPlayer.seek(new Duration(0.0));
                }
                int id = songTableView.getSelectionModel().getSelectedIndex();
                System.out.println(id);
                id--;
                System.out.println(id);
                if(id < 0){
                    id = songTableView.getItems().size() - 1;
                }
                Song currentSelected = songTableView.getItems().get(id);
                System.out.println(id);
                currentSong = currentSelected;
                currentPlayer = players.get(currentSelected.getSongName());
                songTableView.getSelectionModel().select(id);
                currentPlayer.play();
            }
        });
        muteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(muteIcon.getIconLiteral().equals("cil-volume-low") || muteIcon.getIconLiteral().equals("cil-volume-high")){
                    muteIcon.setIconLiteral("cil-volume-off");
                    currentPlayer.setVolume(0);
                }else {
                    muteIcon.setIconLiteral("cil-volume-low");
                    currentPlayer.setVolume(50);
                }
            }
        });
    }

    public void loadSongs(){
        File directory = new File(dirPath);
        File[] files =  directory.listFiles();
        int id = 0;
        for(File file:  files){
//            System.out.println(file.getAbsolutePath());
            String name = file.getName();
//            System.out.println("Loading: " + name);
//            if(name.endsWith("m4a")){
//                file.delete();
//            }
            if(name.endsWith(".mp3") || name.endsWith(".wav")){

                try {
                    Mp3File mp3File =new Mp3File(file.getAbsolutePath());
                    ID3v2 tag = mp3File.getId3v2Tag();
                    if(tag == null){
                        continue;
                    }
                    id++;
                    System.out.println(id);
                    Song newSong = new Song(String.valueOf(id), tag.getTitle()!=null ? tag.getTitle() : file.getName().replace(".mp3", ""),
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
            players.put(song.getSongName(), tempPlayer);
        }
    }

    public void handleMouseClicked(){
        System.out.println(songTableView.getItems().size());
        System.out.println(songTableView.getSelectionModel().getSelectedIndex());
        songBar.setDisable(false);
        currentSong = songTableView.getSelectionModel().getSelectedItem();
        songStopLabel.setText(currentSong.getDuration());
    }

    public SortedList<Song> sort(){
        int index = sortComboBox.getSelectionModel().getSelectedIndex();
        SortedList<Song> tempSortedList = new SortedList<>(songs);
        if(index == 0){
            tempSortedList.setComparator(new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    int firstId = Integer.parseInt(o1.getId());
                    int secondId = Integer.parseInt(o2.getId());
                    return Integer.compare(firstId, secondId);
                }
            });

        }else {
            tempSortedList.setComparator(new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    int firstId = Integer.parseInt(o1.getId());
                    int secondId = Integer.parseInt(o2.getId());
                    return Integer.compare(secondId, firstId);
                }
            });
        }
        return tempSortedList;
    }
    public void handleComboBox(){
        System.out.println("Combo box ran");
        songTableView.setItems(sort());
        System.out.println(songTableView.getSortPolicy().toString());
//        songTableView.setSortPolicy(new Callback<TableView<Song>, Boolean>() {
//            @Override
//            public Boolean call(TableView<Song> songTableView) {
//                return null;
//            }
//        });
    }
}
