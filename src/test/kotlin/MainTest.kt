import com.lakroft.kotlin.*
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue


// https://habr.com/post/346452/
// see also @ParameterizedTest, Вложенные тесты
class MainTest {
    @Test
    fun firstTest() {
        print("JUnit5 test")
    }

    @RepeatedTest(4)
    fun getPageTest() {
        // Протестировать, что запрос getPage возврощает непустую страницу
        assertTrue(!getPage().isEmpty())
    }

    @Test
    fun serviceFileIOTest() {
        // Записать и считать данные из временного файла
        val fileName = "testFile.txt"
        for (newNum in 1..12) {
            for (oldNum in 0..12) {
                val newList: ArrayList<String> = ArrayList()
                var news = 0
                while (news < newNum) {
                    news++
                    newList.add("New $news")
                }
                val oldList: ArrayList<String> = ArrayList()
                var olds = 0
                while (olds < oldNum) {
                    olds++
                    oldList.add("New $olds")
                }
                writeToServiceFile(newList, oldList, fileName)
                val readedList = readFromServiceFile(fileName)
                assertEquals(readedList.size, Math.min(newNum+oldNum, 10))

            }
        }
        val file = File(fileName)
        file.delete()
    }


    // Протестить splitPage
//    @Test
//    fun splitPageTest() {
//        val page =
//    }
}
