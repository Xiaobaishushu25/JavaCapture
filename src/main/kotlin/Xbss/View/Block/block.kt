package Xbss.View.Block

import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle

/**
 * @author  Xbss
 * @create 2022-09-03-11:33
 * @version  1.0
 * @describe :获得一个可以拖动的Hbox
 */
fun getDragBox(msg:Label,hBox: HBox,rect:Rectangle): HBox {
    return HBox().apply {
        var oldNodeX = 0.0
        var oldNodeY = 0.0
        var oldMoveX=0.0
        var oldMoveY=0.0
        setOnMousePressed {
            oldNodeX = it.sceneX
            oldNodeY = it.sceneY
            oldMoveX=translateX
            oldMoveY=translateY
            it.consume()
        }
        setOnMouseDragged {
            val localToScene = localToScene(this.boundsInLocal)
            rect.x=localToScene.minX
            rect.y=localToScene.minY
            rect.width=localToScene.width
            rect.height=localToScene.height
            translateX= it.sceneX - oldNodeX+oldMoveX
            msg.translateX=it.sceneX - oldNodeX+oldMoveX
            hBox.translateX=it.sceneX - oldNodeX+oldMoveX
            translateY= it.sceneY - oldNodeY+oldMoveY
            msg.translateY= it.sceneY - oldNodeY+oldMoveY
            hBox.translateY = it.sceneY - oldNodeY+oldMoveY
            it.consume()
        }
        hoverProperty().addListener { _,_,newValue ->
            if (newValue)
                cursor= Cursor.MOVE
        }
    }
}