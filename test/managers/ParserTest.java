package managers;

import static org.junit.jupiter.api.Assertions.*;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import utility.TaskParser;

class ParserTest {

    @Test
    void parserTasks() {

        assertEquals(new Task("title", "desc", TaskStatus.NEW, 1),
                TaskParser.parseTaskFromCSVString("1,Task,title,NEW,desc,0,null,null\n"));
        assertEquals(new Epic("title", "desc", TaskStatus.NEW, 2),
                TaskParser.parseTaskFromCSVString("2,Epic,title,NEW,desc,0,null,null\n"));
        assertEquals(new Subtask("title", "desc", TaskStatus.NEW, 3, 2),
                TaskParser.parseTaskFromCSVString("3,Subtask,title,NEW,desc,2,null,null\n"));

        assertThrows(IllegalArgumentException.class, () -> TaskParser.parseTaskFromCSVString("2, 1,TASK,1,NEW,desc\n"));
        assertThrows(IllegalArgumentException.class, () -> TaskParser.parseTaskFromCSVString("1,TASK,1,a,desc\n"));
    }


}
