package Xbss.View

import Xbss.Service.getWindowsRec
import Xbss.Utils.KeyUtil
import Xbss.View.Block.getDragBox
import Xbss.View.Block.myDragStage
import com.melloware.jintellitype.JIntellitype
import javafx.application.Application
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.*
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon

/**
 * @author  Xbss
 * @create 2022-09-01-17:37
 * @version  1.0
 * @describe
 */
class capture:Application() {
    private val path: String = System.getProperty("user.dir")
    private lateinit var stage:Stage //主窗口
    private var screenWidth = 0.0 //当前屏幕的宽度
    private var screenHeight = 0.0//当前屏幕的高度
    private var coverStage: Stage   //开始截图时覆盖全屏的窗口
//    private var pane: AnchorPane = AnchorPane().apply { style = "-fx-background-color:#87CEFA20" }
    private var pane: AnchorPane = AnchorPane().apply { style = "-fx-background-color:#82828201" } //盛放view的容器，透明度不能设置为0，否则会鼠标透明
    private val canvas: Canvas
    private var context: GraphicsContext
    private val fillColor = Color.valueOf("#1C1C1C90")//遮蔽全屏的背景色
    private lateinit var view: HBox   //截图区域的框
    private var rect = javafx.scene.shape.Rectangle()//截图区域的rect，用于移动时改变高亮区域
    private  var windowInfo = mutableListOf<Rectangle>() //每次覆盖前存取当前所有窗口信息
//    var viewRec: Rectangle? = null
    private lateinit var captureFile:RadioButton
    private lateinit var captureImage:RadioButton
    private lateinit var quickSave:CheckBox //选择是否快速保存
    private lateinit var savePath:Label //一个标签，内容是快速保存图片文件的路径
//    private  var dialogPane = DialogPane()
    private var beginX=0.0 //截图的几个坐标
    private var beginY=0.0
    private var endX=0.0
    private var endY=0.0
    //截图的框的边框设置
    private val borderWidths = 2.0
    private  var borderColor = Paint.valueOf("#1C86EE")
    init {
        Screen.getPrimary().bounds.apply {
            screenWidth=width
            screenHeight=height
        }
        canvas = Canvas(screenWidth,screenHeight).apply {
            context = graphicsContext2D.apply {
                fill= fillColor
                fillRect(0.0,0.0,screenWidth,screenHeight)
            }
            var recBeginX = 0.0
            var recBeginY = 0.0
            var recEndX = 0.0
            var recEndY = 0.0
            setOnMousePressed {
                context.clearRect(0.0,0.0,screenWidth,screenHeight)
                context.fillRect(0.0,0.0,screenWidth,screenHeight)
                recBeginX=it.x
                recBeginY=it.y
                it.consume()
            }
//               setOnMouseClicked {
//                   it.consume()
//               }
            setOnDragDetected {
                this.startFullDrag()
            }
            setOnMouseDragged {
                if (it.x<recEndX||it.y<recEndY){
                    context.clearRect(0.0,0.0,screenWidth,screenHeight)
                    context.fillRect(0.0,0.0,screenWidth,screenHeight)
                }
                recEndX=it.x
                recEndY=it.y
                context.clearRect(recBeginX,recBeginY,it.x-recBeginX,it.y-recBeginY)
                it.consume()
            }
        }
        rect.xProperty().addListener { _,_,_ ->
            context.clearRect(0.0,0.0,screenWidth,screenHeight)
            context.fillRect(0.0,0.0,screenWidth,screenHeight)
            context.clearRect(rect.x,rect.y,rect.width,rect.height)
        }
        rect.yProperty().addListener { _,_,_ ->
            context.clearRect(0.0,0.0,screenWidth,screenHeight)
            context.fillRect(0.0,0.0,screenWidth,screenHeight)
            context.clearRect(rect.x,rect.y,rect.width,rect.height)
        }
        val stackPane = StackPane(canvas,pane).apply { style = "-fx-background-color:#82828200"
            setOnMousePressed {
                Event.fireEvent(canvas,it)
            }
            setOnMouseDragged {
                Event.fireEvent(canvas,it)
            }
        }
        coverStage = Stage().apply {
//            this.scene = Scene(pane).apply { fill= Paint.valueOf("#ffffff00") }
            this.scene = Scene(stackPane).apply { fill= Paint.valueOf("#ffffff00") }
            initStyle(StageStyle.TRANSPARENT)
            isFullScreen = true
            //设置全屏后提示退出的信息
            fullScreenExitHint = ""
        }
    }
    override fun start(primaryStage: Stage?) {
        stage = primaryStage!!
        captureFile = RadioButton("截文件").apply {
            styleClass.addAll("cf-radio-button")
            isSelected = true
        }
        captureImage = RadioButton("截图").apply { styleClass.addAll("cf-radio-button") }
        ToggleGroup().apply {
            captureFile.toggleGroup=this
            captureImage.toggleGroup=this
        }
        val hBox = HBox(captureFile, captureImage).apply { spacing=20.0 }
        savePath = Label().apply {
            styleClass.addAll("Xbss-label-url")
            style="-fx-font-size :13px"
            setOnMouseClicked { Desktop.getDesktop().open(File(this.text)); }
            isVisible=false
        }
        quickSave = CheckBox("快速保存").apply {
            styleClass.addAll("cf-check-box")
            captureFile.selectedProperty().addListener { _,_,newValue ->
                isVisible=newValue
            }
            isVisible=captureFile.isSelected
            this.selectedProperty().addListener { _,_,newValue ->
                if (this.isVisible)
                    savePath.isVisible = newValue
                if (newValue&&savePath.text!=null)
                    stage.width=460.0
                else
                    stage.width=260.0
            }
            this.visibleProperty().addListener { _, _, newValue ->
                if (this.isSelected)
                    savePath.isVisible = newValue
            }
        }
        val hhBox = HBox(quickSave,savePath).apply { spacing=20.0 }
        val button = Button("截X").apply {
            styleClass.addAll("cf-but")
            setOnAction { fullCover() }
        }
//        val vBox = VBox(hBox,hhBox,button,dialogPane).apply {
        val vBox = VBox(hBox,hhBox,button).apply {
            spacing=20.0
            padding=Insets(20.0)
            setOnDragOver { it.acceptTransferModes(*TransferMode.ANY) }
            setOnDragDropped {
                val content = it.dragboard.getContent(DataFormat.FILES) as MutableList<*>
                savePath.text=content[0].toString()
                println(content[0].toString())
                if(quickSave.isSelected)
                    stage.width=460.0
            }
        }
        stage.apply {
//            this.scene= Scene(vBox,470.0,150.0).apply {
            this.scene= Scene(vBox,260.0,150.0).apply {
                stylesheets.addAll("css/color.css","css/core.css","css/Xbss.css")
            }
            title="Xbss截图3.1"
            icons.addAll(Image("img/logo.png"))
            show()
            toFront()
            setOnCloseRequest { stage.hide() }
        }
        //不使用默认关闭
        Platform.setImplicitExit(false)
        //初始化系统托盘
        initSystemTray()
        JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT, 'Z'.code)
        JIntellitype.getInstance().registerHotKey(2, JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT, 'X'.code)
        JIntellitype.getInstance().registerHotKey(1,0,  112)
        JIntellitype.getInstance().registerHotKey(2,0,  113)
        JIntellitype.getInstance().addHotKeyListener {
            if (it == 1) {
                Platform.runLater {
                    captureImage.isSelected = true
                    fullCover()
                }
            }else if (it == 3){
                if (!stage.isShowing)
                    Platform.runLater {
                        stage.show()
                        stage.toFront()
                    }
            }else if (it == 2){
                Platform.runLater{
                    captureFile.isSelected =true
                    fullCover()
                }
            }
        }
    }
    //覆盖全屏、准备框选截图区域
    private fun fullCover(){
        stage.isIconified =true
        windowInfo.clear()
        val oldWindowInfo = getWindowsRec()
        for (rec in oldWindowInfo){
            //这几个区域不知道哪里来的，只能手动过滤一下
            if ((rec.x==0&&rec.width==136)||(rec.x==0&&rec.y==1)||(rec.x==40&&rec.y==32)||(rec.x==1114&&rec.y==292)
                ||(rec.x==389&&rec.y==78)||(rec.x==473&&rec.y==66)){
            }else{
                windowInfo.add(rec)
            }
        }
        context.clearRect(0.0,0.0,screenWidth,screenHeight)
        context.fillRect(0.0,0.0,screenWidth,screenHeight)
        pane.children.clear()
        coverStage.show()
        drag()
    }
    private fun drag(){
        val msg = Label("选啊").apply {
            alignment = Pos.CENTER
            prefHeight = 20.0
            style= "-fx-background-color:black;"+"-fx-text-fill:white"
        }
        val nail = Button("钉").apply {
            prefWidth=40.0
            prefHeight=20.0
            setOnAction {
                coverStage.close()
                val imageView = screenShot()
                myDragStage(Rectangle(beginX.toInt(),beginY.toInt(), (endX-beginX).toInt(), (endY-beginY).toInt()),imageView).apply { start(Stage()) }
            }
        }
        val cancel = Button("取消").apply {
            prefWidth=40.0
            prefHeight=20.0
            setOnAction { coverStage.close() }
        }
        val ok = Button("完成").apply{
            prefWidth=40.0
            prefHeight=20.0
            setOnAction { screenShot() }
        }
        val hBox = HBox(nail,cancel,ok)
        val eventHandler = EventHandler<javafx.scene.input.MouseEvent> {
            for ( value in windowInfo){
                if (value.contains(it.x,it.y)){
                    pane.children.clear()
                    AnchorPane.setLeftAnchor(msg, value.x.toDouble())
                    AnchorPane.setTopAnchor(msg, value.y.toDouble())
                    msg.text="${value.getWidth()} * ${value.getHeight()}"
                    context.clearRect(0.0,0.0,screenWidth,screenHeight)
                    context.fillRect(0.0,0.0,screenWidth,screenHeight)
                    context.clearRect(value.x.toDouble(),value.y.toDouble(),value.getWidth(),value.getHeight())
                    view = HBox().apply {
                        prefWidth=value.getWidth()
                        prefHeight=value.getHeight()
                        translateX=value.x.toDouble()
                        translateY=value.y.toDouble()
                        border = Border(BorderStroke(borderColor, BorderStrokeStyle.DASHED, null, BorderWidths(borderWidths)))
                        setOnScroll {it ->
                            println("股东了")
                            if (it.deltaY<0)//鼠标滚轮向下，截图
                                screenShot()
                            else ////鼠标滚轮向下，截图并钉在桌面
                                nail.fire()
                        }
                        //很奇怪，明明这个box在开始拖拽的时候就已经被清除了，但是仍然还需要把这些事件传递，不然第一次无法拖拽
                        setOnMousePressed { it -> Event.fireEvent(pane,it) }
                        setOnDragDetected { pane.startFullDrag() }
                        setOnMouseDragged { it -> Event.fireEvent(pane,it) }
                    }
                    pane.children.addAll(msg,view)
                    break
                }
            }
        }
        pane.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_MOVED,eventHandler)
        pane.setOnMousePressed {
            //按压就代表事先框好的区域不需要，要把移动事件删除掉
            pane.removeEventHandler(javafx.scene.input.MouseEvent.MOUSE_MOVED,eventHandler)
            println("移除窗口监听")
            pane.children.clear()
            judgeColor(it.x.toInt(),it.y.toInt())
            //这四个归零是为了在拖拽后直接再次框选时位置不混乱，因为框是每次新生成的，两个组件是一直有的，每次生成新的框需要把位置归零
            msg.translateX=0.0
            msg.translateY=0.0
            hBox.translateX=0.0
            hBox.translateY=0.0
            view = getDragBox(msg,hBox,rect).apply {
                border =
                    Border(
                        BorderStroke(
                            borderColor,
                            BorderStrokeStyle.DASHED,
                            null,
                            BorderWidths(borderWidths)
                        )
                    )
//                hoverProperty().addListener { _,_,newValue ->
//                    if (newValue)
//                        cursor= Cursor.MOVE
//                 }
            }
            beginX = it.screenX
            beginY = it.screenY
            pane.children.add(view)
            AnchorPane.setLeftAnchor(view, beginX)
            AnchorPane.setTopAnchor(view, beginY)
            AnchorPane.setLeftAnchor(msg, beginX)
            AnchorPane.setTopAnchor(msg, beginY - msg.prefHeight)
            pane.children.add(msg)
        }
        pane.setOnMouseClicked {
            if (it.button==MouseButton.SECONDARY)
                coverStage.close()
        }
        pane.setOnDragDetected { pane.startFullDrag() }
        pane.setOnMouseDragged {
            val dragendX=it.screenX
            val dragendY=it.screenY
            view.prefWidth = dragendX-beginX
            view.prefHeight = dragendY-beginY
            msg.text="${view.prefWidth} * ${view.prefHeight}"
        }
        pane.setOnMouseDragExited {
            endX=it.screenX
            endY=it.screenY
            //拖动结束后，如果pane只有两个孩子：msg标签和框选box，就加上确认和取消按钮
            if (pane.children.size==2){
                pane.children.addAll(hBox)
                AnchorPane.setLeftAnchor(hBox,endX-ok.prefWidth-cancel.prefWidth-nail.prefWidth)
                AnchorPane.setTopAnchor(hBox,endY)
            }
        }
    }
    //返回值用于将截图钉在桌面上
    private fun screenShot():ImageView{
        coverStage.close() //先把覆盖全屏的窗口关掉，不然截屏会挡住
        Robot().apply {
            val localToScreen = view.localToScene(view.boundsInLocal)
//            val rectangle = Rectangle(beginX.toInt(),beginY.toInt(), (endX-beginX).toInt(), (endY-beginY).toInt())
//            val bufferedImage = createScreenCapture(rectangle)
            val bufferedImage = createScreenCapture(Rectangle(localToScreen.minX.toInt(),localToScreen.minY.toInt(),(localToScreen.maxX-localToScreen.minX).toInt(),
                (localToScreen.maxY-localToScreen.minY).toInt()))
//            val multiResolutionImage = createMultiResolutionScreenCapture(
//                Rectangle(
//                    localToScreen.minX.toInt(),
//                    localToScreen.minY.toInt(),
//                    (localToScreen.maxX - localToScreen.minX).toInt(),
//                    (localToScreen.maxY - localToScreen.minY).toInt()
//                )
//            )
//            val bufferedImage = multiResolutionImage.getResolutionVariant(1920.0, 1080.0) as BufferedImage
            val systemClipboard = Clipboard.getSystemClipboard()
            val fxImage:Image =SwingFXUtils.toFXImage(bufferedImage, null)
            ClipboardContent().apply {
                if(captureFile.isSelected){
                    File("$path\\cache").apply {
                        if (!this.exists())
                            mkdir()
                        for (file in listFiles()) //删除缓存文件夹中之前的图片
                            file.delete()
                    }
                    val id = KeyUtil.genUniqueKey()
                    //这里用png的图分辨率更高
                    ImageIO.write(bufferedImage,"png",File("$path\\cache\\${id}.png"));
                    putFiles(mutableListOf(File("$path\\cache\\${id}.png")))
                    systemClipboard.setContent(this)
                    if (savePath.text.isNotEmpty() &&quickSave.isSelected){
                        ImageIO.write(bufferedImage,"png",File("${savePath.text}\\${id}.png"));
                    }
                }else{
                    putImage(fxImage)
//                    fxImage = SwingFXUtils.toFXImage(bufferedImage, null).apply { putImage(this) }
                    systemClipboard.setContent(this)
                }
            }
//            val fxImage: WritableImage = SwingFXUtils.toFXImage(bufferedImage, null)
            return ImageView(fxImage)
        }
    }
    //判断背景亮度，亮的话用颜色黑的线条，黑的话用亮的线条
    private fun judgeColor(x:Int,y:Int){
        val robot = Robot()
        val red = robot.getPixelColor(x, y).red
        borderColor = if (red>140)
            Paint.valueOf("#CD3700")//橘黄色
        else
            Paint.valueOf("#00FF00")//亮绿色
    }
    private fun initSystemTray(){
        val popupMenu =  Xbss.View.Block.PopupMenu(stage)
//        val toolkit = Toolkit.getDefaultToolkit()
        //制作一个不可见的窗口承载popmenu
        val popStage = Stage().apply {
            this.scene= Scene(AnchorPane())
            initStyle(StageStyle.UTILITY) //这个样式在任务栏没有窗口
            width=1.0
            height=1.0
            x= Double.MAX_VALUE
            show()
        }
//        val trayIcon = TrayIcon(ImageIcon(File("${path}//src/main/resources/img/logo.png").toURI().toURL()).image, "Xbss截图").apply {
        val trayIcon = TrayIcon(ImageIcon(javaClass.getResource("/img/logo.png")).image, "Xbss截图3.1").apply {
//        val trayIcon = TrayIcon(toolkit.getImage(javaClass.getResource("/img/logo.png")), "Xbss截图").apply {
            isImageAutoSize = true
            addMouseListener(object : MouseAdapter() {
                override fun mouseReleased(e: MouseEvent?) {
                    super.mouseReleased(e)
                    if (e?.button == 1)
                        Platform.runLater {
                            stage.show()
                            stage.toFront()
                        }
                    else if (e?.button == 3) {
                        val x = e.x
                        val y = e.y
                        Platform.runLater {
                            //不加这一句会一直显示菜单不知道为什么
                            popStage.requestFocus()
                            popupMenu.show(popStage,x.toDouble(),y.toDouble())
                        }
                    }
                }
            })
        }
        SystemTray.getSystemTray().add(trayIcon)
    }
}
fun main() {
    Application.launch(capture::class.java)
}