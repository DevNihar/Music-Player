package DataModel;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import org.kordamp.ikonli.javafx.FontIcon;

public class Song {
    private SimpleStringProperty id;
    private SimpleStringProperty songName;
    private SimpleStringProperty duration;
    private SimpleStringProperty artistName;
    private SimpleStringProperty album;
    private SimpleStringProperty url;
    private ObservableValue<Button> playButton;

    public  Song(String id, String songName, String duration, String artistName, String album, String url){
        this.id =  new SimpleStringProperty(id);
        this.songName =  new SimpleStringProperty(songName);
        this.duration =  new SimpleStringProperty(duration);
        this.artistName =  new SimpleStringProperty(artistName);
        this.album =  new SimpleStringProperty(album);
        this.url =  new SimpleStringProperty(url);
        this.playButton = new ObservableValueBase<Button>() {
            @Override
            public Button getValue() {
                return new Button("", new FontIcon("cil-media-play"));
            }
        };
    }

    public Song() {
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getSongName() {
        return songName.get();
    }

    public SimpleStringProperty songNameProperty() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName.set(songName);
    }

    public String getDuration() {
        return duration.get();
    }

    public SimpleStringProperty durationProperty() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    public String getArtistName() {
        return artistName.get();
    }

    public SimpleStringProperty artistNameProperty() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName.set(artistName);
    }

    public String getAlbum() {
        return album.get();
    }

    public SimpleStringProperty albumProperty() {
        return album;
    }

    public void setAlbum(String album) {
        this.album.set(album);
    }

    public String getUrl() {
        return url.get();
    }

    public SimpleStringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public Button getPlayButton() {
        return playButton.getValue();
    }

    public ObservableValue<Button> playButtonProperty() {
        return playButton;
    }

    @Override
    public String toString() {
        return "{ id: " + this.id.toString() + "\n"
                + "songName: " + this.songName.toString() + "\n"
                + "duration: " + this.duration.toString() + "\n"
                + "artistName: " + this.artistName.toString() + "\n"
                + "album: " + this.album.toString() + "\n"
                + "url: " + this.url.toString() + "\n";
    }
}
