import com.melloware.jintellitype.JIntellitype
import org.junit.jupiter.api.Test
import java.awt.Robot

/**
 * @author  Xbss
 * @create 2022-09-01-19:36
 * @version  1.0
 * @describe
 */
class MyTest {
    @Test
     fun judgeColor(){
        val robot = Robot()
        val pixelColor = robot.getPixelColor(100, 100)
        println(pixelColor)
    }
    @Test
    fun registerHotKey() {
//    val keyCode = 'F1'.code
//    JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT, keyCode)
        //三个参数分别表示：热键的标识、主快捷键（ctrl、shift等，0表示不需要）、设定的快捷键
//    JIntellitype.getInstance().registerHotKey(1,0,  keyCode)
        JIntellitype.getInstance().registerHotKey(1,0,  112)
        JIntellitype.getInstance().addHotKeyListener {
            if (it == 1) {
                //写上触发快捷键后的处理逻辑
                println("已按下快捷键")
            }
        }
    }
}