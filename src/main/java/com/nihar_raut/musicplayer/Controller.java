package com.nihar_raut.musicplayer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import DataModel.Song;
import com.mpatric.mp3agic.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import com.jfoenix.controls.JFXSlider;


public class Controller {

    @FXML
    private FontIcon play_pause_icon;
    @FXML
    private FontIcon muteIcon;
    @FXML
    private JFXSlider songSlider;
    @FXML
    private Slider volumeSlider;
    @FXML
    private TableView<Song> songTableView;
    @FXML
    private TableColumn<Song, String> nameColumn;
    @FXML
    private TableColumn<Song, String> durationColumn;
    @FXML
    private TableColumn<Song, String> artistColumn;
//    @FXML
//    private TableColumn<Song, Button> playButtonColumn;
    @FXML
    private TableColumn<Song, String> playButtonColumn;
    @FXML
    private Button playPauseButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button muteButton;
//    @FXML
//    private Button eom;
    @FXML
    private Label songStopLabel;
    @FXML
    private Label songStartLabel;
    @FXML
    private Label songNameLabel;
    @FXML
    private ComboBox<String> sortComboBox;

    private ObservableList<Song> songs;
    private Map<String, MediaPlayer> players;

    private String dirPath;

    private Song currentSong;
    private MediaPlayer currentPlayer;
    private boolean isPlaying;

    public void initialize(){
        songs = FXCollections.observableArrayList();
        dirPath = "D:\\Projects\\JavaSoftwareProjects\\MusicPlayer\\MusicLibrary";
        loadSongs();
//        System.out.println(songs.size());
        createMediaPlayer();
//        songBar.setDisable(true);
        sortComboBox.getSelectionModel().select(0);
//        System.out.println(players.size());
        isPlaying = false;
        volumeSlider.setMin(0);
        volumeSlider.setMax(100);
        volumeSlider.setValue(50);
        songSlider.setValue(0);
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
        playButtonColumn.setCellFactory(new Callback<TableColumn<Song, String>, TableCell<Song, String>>() {
            @Override
            public TableCell<Song, String> call(TableColumn<Song, String> param) {
                final TableCell<Song, String> cell = new TableCell<Song, String>() {
                    @Override
                    public void updateItem(String value, boolean empty) {
                        super.updateItem(value, empty);

                        final VBox vbox = new VBox(5);
                        Button button = new Button("", new FontIcon("cil-media-play"));
                        final TableCell<Song, String> c = this;
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
//                                System.out.println(c.getIndex());
                                songTableView.getSelectionModel().select(c.getIndex());
                                Song currentSelectedSong = songTableView.getSelectionModel().getSelectedItem();
                                updateSongBarData(currentSelectedSong);
                                if(!currentSong.equals(currentSelectedSong)){
                                    currentPlayer.pause();
                                    currentPlayer.seek(new Duration(0.0));
                                    currentSong = currentSelectedSong;
                                    currentPlayer = players.get(currentSong.getSongName());
                                    currentPlayer.play();
                                }else {
                                    currentPlayer.seek(new Duration(0.0));
                                    currentPlayer.play();
                                }
                                play_pause_icon.setIconLiteral("cil-media-pause");
                                isPlaying = true;

                            }
                        });
                        vbox.getChildren().add(button);
                        setGraphic(vbox);
                    }
                };
                cell.setAlignment(Pos.TOP_RIGHT);
                return cell;
            }
        });

        SortedList<Song> sortedList= sort();
        songTableView.setItems(sortedList);
        songTableView.getSelectionModel().selectFirst();
        currentSong = songTableView.getSelectionModel().getSelectedItem();
        updateSongBarData(currentSong);
        currentPlayer = players.get(currentSong.getSongName());
        currentPlayer.seek(new Duration(0.0));
        currentPlayer.setVolume(volumeSlider.getValue()/100);
        playPauseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!isPlaying){
                    playSong();
                }else {
                    pauseSong();
                }
            }
        });

        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                nextSong();
                updateSongBar();

            }
        });
        prevButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                prevSong();
                updateSongBar();
            }
        });
        muteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(muteIcon.getIconLiteral().equals("cil-volume-low") || muteIcon.getIconLiteral().equals("cil-volume-high")){
                    muteIcon.setIconLiteral("cil-volume-off");
                    currentPlayer.setVolume(0);
                    volumeSlider.setValue(0);
                }else {
                    muteIcon.setIconLiteral("cil-volume-low");
                    currentPlayer.setVolume(0.5);
                    volumeSlider.setValue(50);
                }
            }
        });

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                currentPlayer.setVolume(volumeSlider.getValue()/100);
            }
        });

        songSlider.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(songSlider.getValue());
//                currentPlayer.pause();
                currentPlayer.seek(new Duration(songSlider.getValue()));
//                currentPlayer.play();
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
//                    System.out.println(id);
                    Song newSong = new Song(String.valueOf(id), tag.getTitle()!=null ? tag.getTitle() : file.getName().replace(".mp3", ""),
                            secToMin(mp3File.getLengthInSeconds()),
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


    public void createMediaPlayer(){
        players = new HashMap<>();
        for(Song song: songs){
            Media media = new Media(new File(song.getUrl()).toURI().toString());
            MediaPlayer tempPlayer = new MediaPlayer(media);
            tempPlayer.setStartTime(new Duration(0.0));
            tempPlayer.setStopTime(new Duration(getMilliFromDuration(song.getDuration())));
            tempPlayer.setCycleCount(0);
            tempPlayer.setAutoPlay(false);
            tempPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
//                    System.out.println("calling eom");
                    nextSong();
                }
            });
            players.put(song.getSongName(), tempPlayer);
        }
    }

    public long getMilliFromDuration(String duration){
        String[] durationSplit = duration.split(":");
        long min = Integer.parseInt(durationSplit[0]);
        long sec = Integer.parseInt(durationSplit[1]);
        sec = sec + min * 60;
        return sec * 1000;
    }

    public void handleMouseClicked(){

    }

    public SortedList<Song> sort(){
        int index = sortComboBox.getSelectionModel().getSelectedIndex();
        SortedList<Song> tempSortedList = new SortedList<>(songs);
        if(index == 0){
            tempSortedList.setComparator(new Comparator<>() {
                @Override
                public int compare(Song o1, Song o2) {
                    int firstId = Integer.parseInt(o1.getId());
                    int secondId = Integer.parseInt(o2.getId());
                    return Integer.compare(firstId, secondId);
                }
            });

        }else {
            tempSortedList.setComparator(new Comparator<>() {
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
//        System.out.println("Combo box ran");
        songTableView.setItems(sort());
    }

    public void playSong(){

        play_pause_icon.setIconLiteral("cil-media-pause");
        currentSong = songTableView.getSelectionModel().getSelectedItem();
        songSlider.setMax(getMilliFromDuration(currentSong.getDuration()));
        currentPlayer = players.get(currentSong.getSongName());
        currentPlayer.play();
        isPlaying = true;
        updateSongBar();
        updateSongBarData(currentSong);
    }

    public void pauseSong(){
        play_pause_icon.setIconLiteral("cil-media-play");
        currentPlayer.pause();
        isPlaying = false;
    }

    public void nextSong(){
        currentPlayer.seek(new Duration(0.0));
        currentPlayer.pause();
//        updateSongBarData(currentSong);

        songSlider.setValue(0);

        int id = songTableView.getSelectionModel().getSelectedIndex();
        id++;

        if(id >= songTableView.getItems().size()){
            id = 0;
        }
        songTableView.getSelectionModel().select(id);
        currentSong = songTableView.getSelectionModel().getSelectedItem();
        updateSongBar();
        songTableView.scrollTo(id);
        playSong();
    }

    public void prevSong(){
        currentPlayer.seek(new Duration(0.0));
        currentPlayer.pause();
        songSlider.setValue(0);
//        updateSongBarData(currentSong);


        int id = songTableView.getSelectionModel().getSelectedIndex();
//                System.out.println(id);
        id--;
//                System.out.println(id);
        if(id < 0){
            id = songTableView.getItems().size() - 1;
        }
        songTableView.getSelectionModel().select(id);
        currentSong = songTableView.getSelectionModel().getSelectedItem();
        updateSongBar();
        songTableView.scrollTo(id);
        playSong();
    }
    public void updateSongBarData(Song currentSong){
        songNameLabel.setText(currentSong.getSongName());
        songStopLabel.setText(currentSong.getDuration());
    }

    public String secToMin(long duration){
        String time = null;
        if((duration % 60)< 10){
            time = duration / 60 + ":0" + duration % 60;
        }else {
            time = duration / 60 + ":" + duration % 60;
        }
        return time;
    }

    public void updateSongBar(){
        Thread updateThread = new Thread(new Runnable() {

            final Duration duration = new Duration(getMilliFromDuration(currentSong.getDuration()));
            @Override
            public void run() {
                do {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
//                            System.out.println("Running thread");
//                          System.out.println(currentPlayer.getCurrentTime());
                            String currentTime = currentPlayer.getCurrentTime().toString().replace(" ms", "");
                            songSlider.setValue(Double.parseDouble(currentTime));
                            Duration tDuration = currentPlayer.getCurrentTime();
//                            System.out.println(tDuration);
//                          System.out.println(duration.subtract(tDuration).toSeconds());
                            songStartLabel.setText(secToMin((long) tDuration.toSeconds()));
                            songStopLabel.setText(secToMin((long) duration.subtract(tDuration).toSeconds()));
                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        break;
                    }
                }while(isPlaying);
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }
}
