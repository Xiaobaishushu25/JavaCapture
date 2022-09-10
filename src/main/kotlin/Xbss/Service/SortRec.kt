package Xbss.Service

import java.awt.Rectangle

/**
 * @author  Xbss
 * @create 2022-09-03-15:37
 * @version  1.0
 * @describe
 */
fun main() {
    val windowsRec = getWindowsRec()
    sortRec(windowsRec)
}
fun sortRec(list: List<Rectangle>):List<Rectangle>{
    return list.sortedBy { it.computeRecArea() }
//    sortedBy.forEach { println(it.computeRecArea()) }
//    println("**********************")
//    for(rec in list)
//        println(rec.computeRecArea())
}
//fun Rectangle.computeRecArea():Int{
////    return rec.width*rec.height
//    return this.width*this.height
//}