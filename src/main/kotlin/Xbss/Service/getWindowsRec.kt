package Xbss.Service

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.RECT
import com.sun.jna.platform.win32.WinUser.*
import java.awt.Rectangle


/**
 * @author  Xbss
 * @create 2022-09-03-14:01
 * @version  1.0
 * @describe :返回按面积从小到大的窗口列表
 */
//fun getWindowsRec():Map<Int,Rectangle>{
fun getWindowsRec():List<Rectangle>{
    val u32 = User32.INSTANCE
//    return GetAllNextWin.getAllNextWin(u32.GetWindow(u32.GetForegroundWindow(),WinDef.DWORD(GW_HWNDFIRST.toLong())))
    return getAllNextWin(u32.GetWindow(u32.GetForegroundWindow(),WinDef.DWORD(GW_HWNDFIRST.toLong()))).sortedBy { it.computeRecArea() }
}
private fun getNextWin(hwnd:HWND): HWND? {
    return User32.INSTANCE.GetWindow(hwnd, WinDef.DWORD(GW_HWNDNEXT.toLong()))
}
private fun checkLegal(hwnd: HWND,rec: Rectangle):Boolean{
    val windowInfo = WINDOWINFO()
    User32.INSTANCE.GetWindowInfo(hwnd,windowInfo)
    val style = windowInfo.dwStyle
//    println("检查不行")
//    if (style and WS_VISIBLE == 0) {
//        return false //去掉不可视窗口
//    }
//    if (style and WS_DISABLED != 0) {
//        return false //去掉disable窗口
//    }
    if ((style.and(WS_VISIBLE)) == 0) {
        return false //去掉不可视窗口
    }
    if ((style.and(WS_DISABLED) )!= 0) {
        return false //去掉disable窗口
    }
    if (rec.width < 20 || rec.height < 20) {
        return false //去掉小不点窗口
    }
    if (style.and(WS_MINIMIZE) != 0) {
        return false //去掉最小化状态的窗口
    }
//    if (style and WS_MINIMIZE != 0) {
//        return false //去掉最小化状态的窗口
//    }
    return true
//    return if (rec.getWidth() === screenSize.getWidth() && rec.getHeight() === screenSize.getHeight()) {
//        false //去掉底层屏幕窗口
//    } else true
}
//private fun getAllNextWin(hwnd: HWND):Map<Int,Rectangle>{
private fun getAllNextWin(hwnd: HWND):List<Rectangle>{
//    return mutableMapOf<Int, Rectangle>().apply {
    return mutableListOf<Rectangle>().apply {
        var h = hwnd
        val r = RECT()
        var rec = Rectangle()
        while (true){
            h= getNextWin(h)?:break
            User32.INSTANCE.GetWindowRect(h, r)
            rec = r.toRectangle();
            if (!checkLegal(h, rec)) continue
//            put(layerPos++,rec)
            add(rec)
        }
//        while (h!=null) {
//            println("循环")
////            User32.INSTANCE.GetWindowRect(hwnd, r);
//            User32.INSTANCE.GetWindowRect(h, r)
//            rec = r.toRectangle();
////            if (!checkLegal(hwnd, rec)) continue;
//            h=getNextWin(hwnd)
//            if (!checkLegal(hwnd, rec)){
//                continue
//            }
//            put(layerPos++,rec)
//        }
    }
}
fun Rectangle.computeRecArea():Int{
//    return rec.width*rec.height
    return this.width*this.height
}