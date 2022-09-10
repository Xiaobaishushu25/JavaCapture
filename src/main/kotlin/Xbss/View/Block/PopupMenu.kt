package Xbss.View.Block

import com.melloware.jintellitype.JIntellitype
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.stage.Stage
import kotlin.system.exitProcess

/**
 * @author  Xbss
 * @create 2022-09-02-13:39
 * @version  1.0
 * @describe
 */
class PopupMenu(stage: Stage):ContextMenu() {
    init {
        val set = MenuItem("设置").apply {
            setOnAction {
                stage.show()
                stage.toFront()
            }
        }
        val exit = MenuItem("退出").apply { setOnAction {
            JIntellitype.getInstance().cleanUp();
            exitProcess(0) }
        }
        this.items.addAll(set,exit)
    }
}