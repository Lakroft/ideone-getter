package com.lakroft.kotlin

import picocli.CommandLine
import java.io.File
import java.lang.Exception
import java.lang.StringBuilder
import java.net.*
import java.util.*
import kotlin.collections.ArrayList

const val OUTPUT_FILE_NAME = "links.txt"
const val SERVICE_FILE = "service_file.txt"
const val LINK_PREFIX = "https://ideone.com"
const val REQUEST_LINK =  "$LINK_PREFIX/recent/"
const val FINAL_PAGE = 39

fun main(args: Array<String>) {
    val cli = CLITool()
    CommandLine.run(cli, *args)

    val list = readFromServiceFile()
    for (str in list) println("\t$str")
    val langs = cli.langs  //langs = arrayListOf("Rust") //, "Java<")
    val links = getLinksList(langs, list, FINAL_PAGE)

    println("Found:${links.size}")
    writeToOutputFile(links)
    writeToServiceFile(links, list)
    for (link in links) {
        println(link)
    }
}

fun getLinksList(langs: List<String>, existList: List<String>, finalPage: Int): List<String> {
    val links = ArrayList<String>()

    for (pageNum: Int in 0..finalPage) {
        println("Page: $pageNum")
        val pages = splitPage(getPage(pageNum))
        for (str: String in pages) {
            if (str.lastIndexOfAny(langs)>0) {
                val link = LINK_PREFIX + getLink(str) + "\t" + getLang(str)

                if (existList.contains(link)) {
                    if (existList[0] != link) {
                        println("Not Last!!")
                        println(link)
                        println(existList[0])
                    }
                    return links
                } else links.add(link)
            }
        }
    }
    return links
}

fun getLink(str: String): String {
    val strt = str.indexOf("<strong>")
    val end = str.indexOf("</strong>", strt + 1)
    val tmp = str.substring(strt, if (end > 0) end else str.length).split("\"")
    return tmp[1]
}

fun getLang(str: String): String {
    try {
        val rough = str.indexOf("<span style=\"\">")
        val strt = str.indexOf(">", rough) + 1
        val end = str.indexOf("<", strt)
        return str.substring(strt, end)
    } catch (e: Exception) {
        println(str)
        e.printStackTrace()
        return ""
    }
}

fun readFromServiceFile(fileName: String = SERVICE_FILE ): List<String> {
    val result = ArrayList<String>()
    try {
        val file = File(fileName)
        file.useLines {
            for (str in it.iterator()) {
                result.add(str)
                if (result.size > 9) break
            }
        }

        result.trimToSize()
        return result
    }catch (e: Exception) {
        println(e.localizedMessage)
        result.trimToSize()
        return result
    }
}

fun writeToServiceFile(new: List<String>, old: List<String>, fileName: String = SERVICE_FILE) {
    if (new.isEmpty()) return
    val file = File(fileName)
    val list = ArrayList<String> ()
    list.addAll(new.subList(0, Math.min(10, new.size)))
    if (list.size < 10 && !old.isEmpty())
        list.addAll(old.subList(0, Math.min(10-new.size, old.size)))

    val builder = StringBuilder()
    for (link in list) {
        builder.append("$link\n")
    }
    file.writeText(builder.toString())
}

fun writeToOutputFile(links: List<String>) {
    val file = File(OUTPUT_FILE_NAME)
    for (link in links) {
        file.appendText("$link\n")
    }
}

fun writeToFile(text: String) {
    val file = File("debug.txt")
    file.writeText(text)
}

fun splitPage(page: String): List<String> {
    val strt = page.indexOf("source-view")
    val end = page.lastIndexOf("recent_pager_down")
    if (strt < 0 || end < 0) {
        writeToFile(page)
        println("Something Wrong in here: strt = $strt, end = $end")
    }
    return page.substring(strt, end).split("<div class=\"source-view\">")
}

fun getPage(pageNum: Int = 0): String {
    for (i in 0..4) {
//        val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("<host>", <port>))

        val url = URL(REQUEST_LINK + (pageNum + 1))
//        val conn: HttpURLConnection = url.openConnection(proxy) as HttpURLConnection
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

        val encoded = String(Base64.getEncoder().encode("username:password".toByteArray()))

//        conn.setRequestProperty("Proxy-Authorization", "Basic $encoded")
        conn.connect()

        if (conn.responseCode == 200 || conn.responseCode == 201) {
            val stream = conn.inputStream

            val result = stream.bufferedReader().use { it.readText() }

            return result
        }
        println("Responce code:${conn.responseCode}")
    }
    throw Exception("SITE IS UNREACHEBLE")
}
