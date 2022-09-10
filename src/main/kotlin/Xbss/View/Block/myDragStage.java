package Xbss.View.Block;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

/**
 * @author Xbss
 * @version 1.0
 * @create 2022-07-08-23:35
 * @descirbe
 */
public class myDragStage extends Application {
//    private static double oldStageX;
    private  double oldStageX;
    private  double oldStageY;
    private  double oldScreenX;
    private  double oldScreenY;
    private final Rectangle rec;
    private final ImageView imageView;

    public myDragStage(Rectangle rectangle, ImageView imageView) {
        rec=rectangle;
        this.imageView=imageView;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox box = new VBox(imageView);
        box.setOnMouseClicked(event -> {
            if (event.getButton()== MouseButton.SECONDARY){
                primaryStage.close();
            }
        });
//        box.setBackground(new Background(new BackgroundFill(Paint.valueOf("#00F5FF"),new CornerRadii(16.0),new Insets(5))));
        box.setOnMousePressed(event -> {
            oldStageX = primaryStage.getX();
            oldStageY = primaryStage.getY();
            oldScreenX = event.getScreenX();
            oldScreenY = event.getScreenY();
        });
        box.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX()-oldScreenX+oldStageX);
            primaryStage.setY(event.getScreenY()-oldScreenY+oldStageY);
        });
        box.hoverProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue){
                box.setCursor(Cursor.MOVE);
            }
        }));
        Scene scene = new Scene(box);
        //自定义窗体的时候如果设置圆角的话背景还是会是方形的窗口，可以给scene设置填充颜色，把填充颜色透明度设置为0就行（颜色代码后两位是透明度）
//        scene.setFill(Paint.valueOf("#483D8B"));
//        scene.setFill(Paint.valueOf("#483D8B00"));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setX(rec.x);
        primaryStage.setY(rec.y);
//        primaryStage.setHeight(500);
//        primaryStage.setWidth(500);
//        primaryStage.setTitle("你好");
        primaryStage.show();
//        primaryStage.getIcons().add(new Image("img/logo.png"))
        primaryStage.getIcons().add(new Image("img/logo.png"));
        primaryStage.toFront();
        primaryStage.setAlwaysOnTop(true);
    }
}
