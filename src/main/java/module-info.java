module com.nihar_raut.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires mp3agic;

    requires org.kordamp.ikonli.javafx;

    opens com.nihar_raut.musicplayer to javafx.fxml;
    exports com.nihar_raut.musicplayer;
}